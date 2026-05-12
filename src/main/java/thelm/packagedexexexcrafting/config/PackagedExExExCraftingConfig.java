package thelm.packagedexexexcrafting.config;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;
import thelm.packagedexexexcrafting.block.entity.LegendaryCrafterBlockEntity;

public class PackagedExExExCraftingConfig {

	private PackagedExExExCraftingConfig() {}

	private static ForgeConfigSpec serverSpec;

	public static ForgeConfigSpec.IntValue legendaryCrafterEnergyCapacity;
	public static ForgeConfigSpec.IntValue legendaryCrafterEnergyReq;
	public static ForgeConfigSpec.IntValue legendaryCrafterEnergyUsage;
	public static ForgeConfigSpec.BooleanValue legendaryCrafterDrawMEEnergy;

	@SuppressWarnings("removal")
	public static void registerConfig() {
		buildConfig();
		ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, serverSpec);
	}

	private static void buildConfig() {
		ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();

		builder.push("legendary_crafter");
		builder.comment("How much FE the Legendary Package Crafter should hold.");
		legendaryCrafterEnergyCapacity = builder.defineInRange("energy_capacity", 20000, 0, Integer.MAX_VALUE);
		builder.comment("How much total FE the Legendary Package Crafter should use per operation.");
		legendaryCrafterEnergyReq = builder.defineInRange("energy_req", 20000, 0, Integer.MAX_VALUE);
		builder.comment("How much FE/t maximum the Legendary Package Crafter can use.");
		legendaryCrafterEnergyUsage = builder.defineInRange("energy_usage", 2000, 0, Integer.MAX_VALUE);
		builder.comment("Should the Legendary Package Crafter draw energy from ME systems.");
		legendaryCrafterDrawMEEnergy = builder.define("draw_me_energy", true);
		builder.pop();

		serverSpec = builder.build();
	}

	public static void reloadServerConfig() {
		LegendaryCrafterBlockEntity.energyCapacity = legendaryCrafterEnergyCapacity.get();
		LegendaryCrafterBlockEntity.energyReq = legendaryCrafterEnergyReq.get();
		LegendaryCrafterBlockEntity.energyUsage = legendaryCrafterEnergyUsage.get();
		LegendaryCrafterBlockEntity.drawMEEnergy = legendaryCrafterDrawMEEnergy.get();
	}
}
