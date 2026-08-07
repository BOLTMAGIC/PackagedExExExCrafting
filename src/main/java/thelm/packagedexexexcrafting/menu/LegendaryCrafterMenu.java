package thelm.packagedexexexcrafting.menu;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.items.SlotItemHandler;
import thelm.packagedauto.menu.BaseMenu;
import thelm.packagedauto.menu.factory.PositionalBlockEntityMenuFactory;
import thelm.packagedauto.slot.RemoveOnlySlot;
import thelm.packagedexexexcrafting.block.entity.LegendaryCrafterBlockEntity;
import thelm.packagedexexexcrafting.slot.LegendaryCrafterRemoveOnlySlot;

public class LegendaryCrafterMenu extends BaseMenu<LegendaryCrafterBlockEntity> {

	public static final MenuType<LegendaryCrafterMenu> TYPE_INSTANCE = IForgeMenuType.create(new PositionalBlockEntityMenuFactory<>(LegendaryCrafterMenu::new));

	public LegendaryCrafterMenu(int windowId, Inventory inventory, LegendaryCrafterBlockEntity blockEntity) {
		super(TYPE_INSTANCE, windowId, inventory, blockEntity);
		addSlot(new SlotItemHandler(itemHandler, 170, 8, 125));
		for(int i = 0; i < 13; ++i) {
			for(int j = 0; j < 13; ++j) {
				addSlot(new LegendaryCrafterRemoveOnlySlot(blockEntity, i*13+j, 44+j*18, 17+i*18));
			}
		}
		addSlot(new RemoveOnlySlot(itemHandler, 169, 314, 125));
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
