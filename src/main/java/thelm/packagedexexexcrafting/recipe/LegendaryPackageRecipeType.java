package thelm.packagedexexexcrafting.recipe;

import java.util.List;
import java.util.stream.IntStream;

import com.blakebr0.extendedcrafting.crafting.recipe.ShapelessTableRecipe;
import com.blakebr0.extendedcrafting.init.ModBlocks;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntRBTreeSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import net.minecraft.core.Vec3i;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import thelm.packagedauto.api.IPackageRecipeInfo;
import thelm.packagedauto.api.IPackageRecipeType;
import thelm.packagedauto.api.IRecipeSlotViewWrapper;
import thelm.packagedauto.api.IRecipeSlotsViewWrapper;
import thelm.packagedexcrafting.recipe.UltimatePackageRecipeType;

@SuppressWarnings("deprecation")
public class LegendaryPackageRecipeType implements IPackageRecipeType {

	public static final LegendaryPackageRecipeType INSTANCE = new LegendaryPackageRecipeType();
	public static final ResourceLocation NAME = new ResourceLocation("packagedexexexcrafting:legendary");
	public static final IntSet SLOTS;
	public static final IntSet SLOTS_BORDER;
	public static final List<ResourceLocation> CATEGORIES = List.of(
			new ResourceLocation("extendedcrafting:legendary_crafting"),
			new ResourceLocation("extendedcrafting:epic_crafting"),
			new ResourceLocation("extendedcrafting:ultimate_crafting"),
			new ResourceLocation("extendedcrafting:elite_crafting"),
			new ResourceLocation("extendedcrafting:advanced_crafting"),
			new ResourceLocation("extendedcrafting:basic_crafting"));
	public static final Vec3i COLOR = new Vec3i(139, 139, 139);
	public static final Vec3i COLOR_BORDER = new Vec3i(139, 179, 139);
	public static final Vec3i COLOR_DISABLED = new Vec3i(64, 64, 64);

	static {
		SLOTS = new IntRBTreeSet();
		IntStream.range(0, 81).forEachOrdered(SLOTS::add);
		SLOTS_BORDER = new IntRBTreeSet(SLOTS);
		for(int i = 1; i < 8; ++i) {
			for(int j = 1; j < 8; ++j) {
				SLOTS_BORDER.remove(9*i+j);
			}
		}
	}

	protected LegendaryPackageRecipeType() {}

	@Override
	public ResourceLocation getName() {
		return NAME;
	}

	@Override
	public MutableComponent getDisplayName() {
		return Component.translatable("recipe.packagedexexexcrafting.legendary");
	}

	@Override
	public MutableComponent getShortDisplayName() {
		return Component.translatable("recipe.packagedexexexcrafting.legendary.short");
	}

	@Override
	public IPackageRecipeInfo getNewRecipeInfo() {
		return new LegendaryPackageRecipeInfo();
	}

	@Override
	public IntSet getEnabledSlots() {
		return SLOTS;
	}

	@Override
	public List<ResourceLocation> getJEICategories() {
		return CATEGORIES;
	}

	@Override
	public Int2ObjectMap<ItemStack> getRecipeTransferMap(IRecipeSlotsViewWrapper recipeLayoutWrapper) {
		Int2ObjectMap<ItemStack> map = new Int2ObjectOpenHashMap<>();
		List<IRecipeSlotViewWrapper> slotViews = recipeLayoutWrapper.getRecipeSlotViews();
		int tier = 0;
		boolean shapeless = false;
		if(recipeLayoutWrapper.getRecipe() instanceof ShapelessTableRecipe shapelessRecipe) {
			tier = shapelessRecipe.getTier();
			shapeless = true;
		}
		if(tier == 0 && slotViews.size() == 170 || tier == 6) {
			int index = 0;
			for(IRecipeSlotViewWrapper slotView : slotViews) {
				if(slotView.isInput()) {
					Object displayed = slotView.getDisplayedIngredient().orElse(null);
					if(displayed instanceof ItemStack stack && !stack.isEmpty()) {
						map.put(index, stack);
					}
					++index;
				}
			}
			Int2ObjectMap<ItemStack> matrixPart = new Int2ObjectOpenHashMap<>();
			Int2ObjectMap<ItemStack> extraPart = new Int2ObjectOpenHashMap<>();
			for(int i = 0; i < index; ++i) {
				ItemStack s = map.getOrDefault(i, ItemStack.EMPTY);
				if(i < 169) matrixPart.put(i, s);
				else extraPart.put(i, s);
			}
			Int2ObjectMap<ItemStack> result = Edge3PackageRecipeHelper.INSTANCE.matrixToEncoder(matrixPart, shapeless);
			result.putAll(extraPart);
			return result;
		}
		return UltimatePackageRecipeType.INSTANCE.getRecipeTransferMap(recipeLayoutWrapper);
	}

	@Override
	public Object getRepresentation() {
		return new ItemStack(ModBlocks.LEGENDARY_TABLE.get());
	}

	@Override
	public Vec3i getSlotColor(int slot) {
		if(SLOTS_BORDER.contains(slot)) {
			return COLOR_BORDER;
		}
		if(slot >= 81 && slot != 81 && slot < 90) {
			return COLOR_DISABLED;
		}
		return COLOR;
	}
}
