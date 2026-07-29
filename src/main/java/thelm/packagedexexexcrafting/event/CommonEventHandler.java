package thelm.packagedexexexcrafting.event;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import thelm.packagedauto.util.ApiImpl;
import thelm.packagedexexexcrafting.block.LegendaryCrafterBlock;
import thelm.packagedexexexcrafting.block.entity.LegendaryCrafterBlockEntity;
import thelm.packagedexexexcrafting.config.PackagedExExExCraftingConfig;
import thelm.packagedexexexcrafting.menu.LegendaryCrafterMenu;
import thelm.packagedexexexcrafting.recipe.LegendaryPackageRecipeType;
import thelm.packagedexexexcrafting.network.Networking;

import java.util.Objects;

public class CommonEventHandler {

	public static final CommonEventHandler INSTANCE = new CommonEventHandler();

	public static CommonEventHandler getInstance() {
		return INSTANCE;
	}

	public void onConstruct() {
		@SuppressWarnings("removal")
		IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
		modEventBus.register(this);
		PackagedExExExCraftingConfig.registerConfig();

		DeferredRegister<Block> blockRegister = DeferredRegister.create(Registries.BLOCK, "packagedexexexcrafting");
		blockRegister.register("legendary_crafter", ()->LegendaryCrafterBlock.INSTANCE);
		blockRegister.register(modEventBus);

		DeferredRegister<Item> itemRegister = DeferredRegister.create(Registries.ITEM, "packagedexexexcrafting");
		itemRegister.register("legendary_crafter", ()-> LegendaryCrafterBlock.ITEM_INSTANCE);
		itemRegister.register(modEventBus);

		DeferredRegister<BlockEntityType<?>> blockEntityRegister = DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, "packagedexexexcrafting");
		blockEntityRegister.register("legendary_crafter", ()-> LegendaryCrafterBlockEntity.TYPE_INSTANCE);
		blockEntityRegister.register(modEventBus);

		DeferredRegister<MenuType<?>> menuRegister = DeferredRegister.create(Registries.MENU, "packagedexexexcrafting");
		menuRegister.register("legendary_crafter", ()->LegendaryCrafterMenu.TYPE_INSTANCE);
		menuRegister.register(modEventBus);

		DeferredRegister<CreativeModeTab> creativeTabRegister = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, "packagedexexexcrafting");
		creativeTabRegister.register(modEventBus);
		creativeTabRegister.register("tab",
				()->CreativeModeTab.builder().
				title(Component.translatable("itemGroup.packagedexexexcrafting")).
				icon(()->new ItemStack(LegendaryCrafterBlock.ITEM_INSTANCE)).
					displayItems((parameters, output)-> output.accept(LegendaryCrafterBlock.ITEM_INSTANCE)).
				build());
	}

	@SuppressWarnings("unused")
    @SubscribeEvent
	public void onCommonSetup(FMLCommonSetupEvent event) {
		ApiImpl.INSTANCE.registerRecipeType(LegendaryPackageRecipeType.INSTANCE);
		// register custom networking
		Networking.register();
	}

	@SuppressWarnings("unused")
    @SubscribeEvent
	public void onModConfig(ModConfigEvent event) {
        if (Objects.requireNonNull(event.getConfig().getType()) == ModConfig.Type.SERVER) {
			PackagedExExExCraftingConfig.reloadServerConfig();
        }
	}
}
