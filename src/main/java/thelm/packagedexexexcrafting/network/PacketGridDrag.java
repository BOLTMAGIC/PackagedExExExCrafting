package thelm.packagedexexexcrafting.network;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;
import thelm.packagedexexexcrafting.block.entity.LegendaryCrafterBlockEntity;

import java.util.function.Supplier;

public class PacketGridDrag {

    private final BlockPos pos;
    private final int[] indices;
    private final int button;

    public PacketGridDrag(BlockPos pos, int[] indices, int button) {
        this.pos = pos;
        this.indices = indices;
        this.button = button;
    }

    public static void encode(PacketGridDrag pkt, FriendlyByteBuf buf) {
        buf.writeBlockPos(pkt.pos);
        buf.writeInt(pkt.button);
        buf.writeInt(pkt.indices.length);
        for(int i : pkt.indices) buf.writeInt(i);
    }

    public static PacketGridDrag decode(FriendlyByteBuf buf) {
        BlockPos pos = buf.readBlockPos();
        int button = buf.readInt();
        int len = buf.readInt();
        int[] indices = new int[len];
        for(int i = 0; i < len; ++i) indices[i] = buf.readInt();
        return new PacketGridDrag(pos, indices, button);
    }

    public static void handle(PacketGridDrag pkt, Supplier<NetworkEvent.Context> ctx) {
        NetworkEvent.Context context = ctx.get();
        context.enqueueWork(() -> {
            ServerPlayer player = context.getSender();
            if(player == null) return;
            if(player.level().isClientSide()) return;
            if(player.blockPosition().distSqr(pkt.pos) > 64*64) return;
            if(!(player.level().getBlockEntity(pkt.pos) instanceof LegendaryCrafterBlockEntity be)) return;

            var itemHandler = be.getItemHandler();
            ItemStack carried = player.getMainHandItem().copy();
            if(carried.isEmpty()) return;
            int slots = pkt.indices.length;
            if(slots == 0) return;

            if(pkt.button == 0) {
                // left-drag: distribute items evenly across slots
                int per = carried.getCount() / slots;
                if(per <= 0) per = 1;
                for(int idx : pkt.indices) {
                    ItemStack target = itemHandler.getStackInSlot(idx);
                    if(target.isEmpty()) {
                        int toPlace = Math.min(per, carried.getMaxStackSize());
                        ItemStack put = carried.copy();
                        put.setCount(Math.min(toPlace, carried.getCount()));
                        itemHandler.setStackInSlot(idx, put);
                        carried.shrink(put.getCount());
                        if(carried.isEmpty()) break;
                    }
                    else if(ItemStack.isSameItemSameTags(target, carried)) {
                        int space = target.getMaxStackSize() - target.getCount();
                        int toPlace = Math.min(space, Math.min(per, carried.getCount()));
                        if(toPlace > 0) {
                            target.grow(toPlace);
                            itemHandler.setStackInSlot(idx, target);
                            carried.shrink(toPlace);
                            if(carried.isEmpty()) break;
                        }
                    }
                }
            }
            else if(pkt.button == 1) {
                // right-drag: put one item per slot
                for(int idx : pkt.indices) {
                    if(carried.isEmpty()) break;
                    ItemStack target = itemHandler.getStackInSlot(idx);
                    if(target.isEmpty()) {
                        ItemStack put = carried.copy();
                        put.setCount(1);
                        itemHandler.setStackInSlot(idx, put);
                        carried.shrink(1);
                    }
                    else if(ItemStack.isSameItemSameTags(target, carried) && target.getCount() < target.getMaxStackSize()) {
                        target.grow(1);
                        itemHandler.setStackInSlot(idx, target);
                        carried.shrink(1);
                    }
                }
            }

            player.setItemInHand(InteractionHand.MAIN_HAND, carried);
            be.setChanged();
            if(be.getLevel() != null) be.getLevel().sendBlockUpdated(be.getBlockPos(), be.getBlockState(), be.getBlockState(), 3);
        });
        context.setPacketHandled(true);
    }
}


