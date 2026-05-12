package thelm.packagedexexexcrafting.client.event;

import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import thelm.packagedexexexcrafting.client.screen.LegendaryCrafterScreen;
import thelm.packagedexexexcrafting.menu.LegendaryCrafterMenu;

public class ClientEventHandler {

	public static final ClientEventHandler INSTANCE = new ClientEventHandler();

	public static ClientEventHandler getInstance() {
		return INSTANCE;
	}

	@SuppressWarnings("removal")
	public void onConstruct() {
		FMLJavaModLoadingContext.get().getModEventBus().register(this);
	}

	@SubscribeEvent
	public void onClientSetup(FMLClientSetupEvent event) {
		MenuScreens.register(LegendaryCrafterMenu.TYPE_INSTANCE, LegendaryCrafterScreen::new);
	}
}
