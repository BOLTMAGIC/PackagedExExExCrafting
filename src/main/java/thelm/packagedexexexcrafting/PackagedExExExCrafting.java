package thelm.packagedexexexcrafting;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import thelm.packagedexexexcrafting.client.event.ClientEventHandler;
import thelm.packagedexexexcrafting.event.CommonEventHandler;

@Mod(PackagedExExExCrafting.MOD_ID)
public class PackagedExExExCrafting {

	public static final String MOD_ID = "packagedexexexcrafting";

	public PackagedExExExCrafting() {
		CommonEventHandler.getInstance().onConstruct();
		DistExecutor.unsafeRunWhenOn(Dist.CLIENT, ()->()-> ClientEventHandler.getInstance().onConstruct());
	}
}
