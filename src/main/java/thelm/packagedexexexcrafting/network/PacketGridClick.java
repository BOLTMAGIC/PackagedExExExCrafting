package thelm.packagedexexexcrafting.network;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;
import thelm.packagedexexexcrafting.block.entity.LegendaryCrafterBlockEntity;

import java.util.function.Supplier;

public class PacketGridClick {

    private final BlockPos pos;
    private final int index;
    private final int button;
    private final boolean shift;

    public PacketGridClick(BlockPos pos, int index, int button, boolean shift) {
        this.pos = pos;
        this.index = index;
        this.button = button;
        this.shift = shift;
    }

    public static void encode(PacketGridClick pkt, FriendlyByteBuf buf) {
        buf.writeBlockPos(pkt.pos);
        buf.writeInt(pkt.index);
        buf.writeInt(pkt.button);
        buf.writeBoolean(pkt.shift);
    }

    public static PacketGridClick decode(FriendlyByteBuf buf) {
        return new PacketGridClick(buf.readBlockPos(), buf.readInt(), buf.readInt(), buf.readBoolean());
    }

    public static void handle(PacketGridClick pkt, Supplier<NetworkEvent.Context> ctx) {
        NetworkEvent.Context context = ctx.get();
        context.enqueueWork(() -> {
            ServerPlayer player = context.getSender();
            if(player == null) return;
            if(player.level().isClientSide()) return;
            if(player.blockPosition().distSqr(pkt.pos) > 64*64) return;
            if(!(player.level().getBlockEntity(pkt.pos) instanceof LegendaryCrafterBlockEntity be)) return;

            var itemHandler = be.getItemHandler();
            ItemStack slotStack = itemHandler.getStackInSlot(pkt.index);
            ItemStack carried = player.getMainHandItem();

            // SHIFT-click: quick-move from grid slot to player inventory
            if(pkt.shift) {
                if(!slotStack.isEmpty()) {
                    ItemStack copy = slotStack.copy();
                    if(player.addItem(copy)) {
                        itemHandler.setStackInSlot(pkt.index, ItemStack.EMPTY);
                    }
                    else {
                        // try partial move
                        int moved = 0;
                        while(!copy.isEmpty() && player.addItem(copy.copy())) moved = 1; // fallback simple attempt
                        if(moved == 1) itemHandler.setStackInSlot(pkt.index, ItemStack.EMPTY);
                    }
                    be.setChanged();
                    if(be.getLevel() != null) be.getLevel().sendBlockUpdated(be.getBlockPos(), be.getBlockState(), be.getBlockState(), 3);
                }
            }
            else if(pkt.button == 0) {
                // Left click behavior
                if(carried.isEmpty()) {
                    // pick up whole stack
                    if(!slotStack.isEmpty()) {
                        player.setItemInHand(InteractionHand.MAIN_HAND, slotStack.copy());
                        itemHandler.setStackInSlot(pkt.index, ItemStack.EMPTY);
                        be.setChanged();
                        if(be.getLevel() != null) be.getLevel().sendBlockUpdated(be.getBlockPos(), be.getBlockState(), be.getBlockState(), 3);
                    }
                }
                else {
                    // place/merge whole carried into slot
                    if(slotStack.isEmpty()) {
                        itemHandler.setStackInSlot(pkt.index, carried.copy());
                        player.setItemInHand(InteractionHand.MAIN_HAND, ItemStack.EMPTY);
                        player.getInventory().setChanged();
                        be.setChanged();
                        if(be.getLevel() != null) be.getLevel().sendBlockUpdated(be.getBlockPos(), be.getBlockState(), be.getBlockState(), 3);
                    }
                    else if(ItemStack.isSameItemSameTags(carried, slotStack)) {
                        int transfer = Math.min(carried.getCount(), slotStack.getMaxStackSize()-slotStack.getCount());
                        if(transfer > 0) {
                            slotStack.grow(transfer);
                            carried.shrink(transfer);
                            itemHandler.setStackInSlot(pkt.index, slotStack);
                            player.setItemInHand(InteractionHand.MAIN_HAND, carried.isEmpty() ? ItemStack.EMPTY : carried);
                            player.getInventory().setChanged();
                            be.setChanged();
                            if(be.getLevel() != null) be.getLevel().sendBlockUpdated(be.getBlockPos(), be.getBlockState(), be.getBlockState(), 3);
                        }
                    }
                }
            }
            else if(pkt.button == 1) {
                // Right click behavior: pick half or place one
                if(carried.isEmpty()) {
                    if(!slotStack.isEmpty()) {
                        int take = (slotStack.getCount()+1)/2;
                        ItemStack taken = slotStack.copy();
                        taken.setCount(take);
                        slotStack.shrink(take);
                        itemHandler.setStackInSlot(pkt.index, slotStack);
                        player.setItemInHand(InteractionHand.MAIN_HAND, taken);
                        player.getInventory().setChanged();
                        be.setChanged();
                        if(be.getLevel() != null) be.getLevel().sendBlockUpdated(be.getBlockPos(), be.getBlockState(), be.getBlockState(), 3);
                    }
                }
                else {
                    // place one item from carried into slot
                    if(slotStack.isEmpty()) {
                        ItemStack put = carried.copy();
                        put.setCount(1);
                        itemHandler.setStackInSlot(pkt.index, put);
                        carried.shrink(1);
                        player.setItemInHand(InteractionHand.MAIN_HAND, carried.isEmpty() ? ItemStack.EMPTY : carried);
                        player.getInventory().setChanged();
                        be.setChanged();
                        if(be.getLevel() != null) be.getLevel().sendBlockUpdated(be.getBlockPos(), be.getBlockState(), be.getBlockState(), 3);
                    }
                    else if(ItemStack.isSameItemSameTags(carried, slotStack) && slotStack.getCount() < slotStack.getMaxStackSize()) {
                        slotStack.grow(1);
                        carried.shrink(1);
                        itemHandler.setStackInSlot(pkt.index, slotStack);
                        player.setItemInHand(InteractionHand.MAIN_HAND, carried.isEmpty() ? ItemStack.EMPTY : carried);
                        player.getInventory().setChanged();
                        be.setChanged();
                        if(be.getLevel() != null) be.getLevel().sendBlockUpdated(be.getBlockPos(), be.getBlockState(), be.getBlockState(), 3);
                    }
                }
            }
        });
        context.setPacketHandled(true);
    }
}




