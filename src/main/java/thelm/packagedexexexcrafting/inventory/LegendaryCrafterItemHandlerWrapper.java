package thelm.packagedexexexcrafting.inventory;

import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import thelm.packagedauto.inventory.SidedItemHandlerWrapper;

import java.util.stream.IntStream;

public class LegendaryCrafterItemHandlerWrapper extends SidedItemHandlerWrapper<LegendaryCrafterItemHandler> {

	public static final int[] SLOTS = IntStream.rangeClosed(0, 170).toArray();

	public LegendaryCrafterItemHandlerWrapper(LegendaryCrafterItemHandler itemHandler, Direction direction) {
		super(itemHandler, direction);
	}

	@Override
	public int[] getSlotsForDirection(Direction direction) {
		return SLOTS;
	}

	@Override
	public boolean canInsertItem(int index, ItemStack stack, Direction direction) {
		return false;
	}

	@Override
	public boolean canExtractItem(int index, Direction direction) {
		return !itemHandler.blockEntity.isWorking || index == 169;
	}
}
