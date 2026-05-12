package thelm.packagedexexexcrafting.inventory;

import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.items.IItemHandlerModifiable;
import org.jetbrains.annotations.NotNull;
import thelm.packagedauto.inventory.BaseItemHandler;
import thelm.packagedexexexcrafting.block.entity.LegendaryCrafterBlockEntity;

public class LegendaryCrafterItemHandler extends BaseItemHandler<LegendaryCrafterBlockEntity> {

	public LegendaryCrafterItemHandler(LegendaryCrafterBlockEntity blockEntity) {
		super(blockEntity, 171);
	}

	@Override
	public boolean isItemValid(int slot, @NotNull ItemStack stack) {
		if(slot == 170) {
			return stack.getCapability(ForgeCapabilities.ENERGY).isPresent();
		}
		return false;
	}

	@Override
	public IItemHandlerModifiable getWrapperForDirection(Direction side) {
		return wrapperMap.computeIfAbsent(side, s->new LegendaryCrafterItemHandlerWrapper(this, s));
	}

	@Override
	public int get(int id) {
		return switch(id) {
		case 0 -> blockEntity.remainingProgress;
		case 1 -> blockEntity.isWorking ? 1 : 0;
		case 2 -> blockEntity.getEnergyStorage().getEnergyStored();
		default -> 0;
		};
	}

	@Override
	public void set(int id, int value) {
		switch(id) {
		case 0 -> blockEntity.remainingProgress = value;
		case 1 -> blockEntity.isWorking = value != 0;
		case 2 -> blockEntity.getEnergyStorage().setEnergyStored(value);
		}
	}

	@Override
	public int getCount() {
		return 3;
	}
}
