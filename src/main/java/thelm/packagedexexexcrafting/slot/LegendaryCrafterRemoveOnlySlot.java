package thelm.packagedexexexcrafting.slot;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.SlotItemHandler;
import org.jetbrains.annotations.NotNull;
import thelm.packagedexexexcrafting.block.entity.LegendaryCrafterBlockEntity;

//Code from CoFHCore
public class LegendaryCrafterRemoveOnlySlot extends SlotItemHandler {

	public final LegendaryCrafterBlockEntity blockEntity;

	public LegendaryCrafterRemoveOnlySlot(LegendaryCrafterBlockEntity blockEntity, int index, int x, int y) {
		super(blockEntity.getItemHandler(), index, x, y);
		this.blockEntity = blockEntity;
	}

	@Override
	public boolean mayPickup(Player player) {
		return !blockEntity.isWorking;
	}

	@Override
	public boolean mayPlace(ItemStack stack) {
		return false;
	}
}
