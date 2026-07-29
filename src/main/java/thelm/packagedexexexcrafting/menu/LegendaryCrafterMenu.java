package thelm.packagedexexexcrafting.menu;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.items.SlotItemHandler;
import thelm.packagedauto.menu.BaseMenu;
import thelm.packagedauto.menu.factory.PositionalBlockEntityMenuFactory;
import thelm.packagedauto.slot.RemoveOnlySlot;
import thelm.packagedexexexcrafting.block.entity.LegendaryCrafterBlockEntity;

public class LegendaryCrafterMenu extends BaseMenu<LegendaryCrafterBlockEntity> {

	public static final MenuType<LegendaryCrafterMenu> TYPE_INSTANCE = IForgeMenuType.create(new PositionalBlockEntityMenuFactory<>(LegendaryCrafterMenu::new));

	public LegendaryCrafterMenu(int windowId, Inventory inventory, LegendaryCrafterBlockEntity blockEntity) {
		super(TYPE_INSTANCE, windowId, inventory, blockEntity);
		// Keep only essential container slots (energy + output). The 13x13 grid is kept server-side
		// and will be synced to the client via custom packets/NBT. This avoids registering 169
		// container slots which would exceed the network slot index limit.
		addSlot(new SlotItemHandler(itemHandler, 170, 8, 125)); // energy slot
		addSlot(new RemoveOnlySlot(itemHandler, 169, 314, 125)); // output slot
		setupPlayerInventory();
	}

	@Override
	public int getPlayerInvX() {
		return 79;
	}

	@Override
	public int getPlayerInvY() {
		return 264;
	}
}
