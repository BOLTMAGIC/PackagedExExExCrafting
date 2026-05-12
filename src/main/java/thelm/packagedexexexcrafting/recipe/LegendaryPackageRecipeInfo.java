package thelm.packagedexexexcrafting.recipe;

import com.blakebr0.extendedcrafting.api.crafting.ITableRecipe;
import com.blakebr0.extendedcrafting.init.ModRecipeTypes;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.Level;
import thelm.packagedauto.api.IPackagePattern;
import thelm.packagedauto.api.IPackageRecipeInfo;
import thelm.packagedauto.api.IPackageRecipeType;
import thelm.packagedauto.recipe.ProcessingPackageRecipeType;
import thelm.packagedauto.util.MiscHelper;
import thelm.packagedauto.util.PackagePattern;
import thelm.packagedexcrafting.recipe.ITablePackageRecipeInfo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class LegendaryPackageRecipeInfo implements ITablePackageRecipeInfo {

	ITableRecipe recipe;
	List<ItemStack> input = new ArrayList<>();
	Container matrix = new SimpleContainer(169);
	ItemStack output;
	List<IPackagePattern> patterns = new ArrayList<>();
	List<IPackagePattern> extraPatterns = new ArrayList<>();

	@Override
	public void load(CompoundTag nbt) {
		input.clear();
		output = ItemStack.EMPTY;
		patterns.clear();
		extraPatterns.clear();
		Recipe<?> recipe = MiscHelper.INSTANCE.getRecipeManager().byKey(new ResourceLocation(nbt.getString("Recipe"))).orElse(null);
		List<ItemStack> matrixList = new ArrayList<>();
		MiscHelper.INSTANCE.loadAllItems(nbt.getList("Matrix", 10), matrixList);
		for(int i = 0; i < 169 && i < matrixList.size(); ++i) {
			matrix.setItem(i, matrixList.get(i));
		}
		if(recipe instanceof ITableRecipe tableRecipe) {
			this.recipe = tableRecipe;
			output = this.recipe.assemble(matrix, MiscHelper.INSTANCE.getRegistryAccess()).copy();
		}
		List<ItemStack> actualInput = MiscHelper.INSTANCE.condenseStacks(matrix);
		if(actualInput.size() <= 81) {
			input.addAll(actualInput);
		}
		else for(int i = 0; i*9 < actualInput.size(); ++i) {
			IPackageRecipeInfo subRecipe = ProcessingPackageRecipeType.INSTANCE.getNewRecipeInfo();
			subRecipe.generateFromStacks(actualInput.subList(i*9, Math.min(9+i*9, actualInput.size())), List.of(), null);
			IPackagePattern subPattern = subRecipe.getPatterns().get(0);
			extraPatterns.add(subPattern);
			input.add(subPattern.getOutput());
		}
		for(int i = 0; i*9 < input.size(); ++i) {
			patterns.add(new PackagePattern(this, i));
		}
	}

	@Override
	public void save(CompoundTag nbt) {
		if(recipe != null) {
			nbt.putString("Recipe", recipe.getId().toString());
		}
		List<ItemStack> matrixList = new ArrayList<>();
		for(int i = 0; i < 169; ++i) {
			matrixList.add(matrix.getItem(i));
		}
		ListTag matrixTag = MiscHelper.INSTANCE.saveAllItems(new ListTag(), matrixList);
		nbt.put("Matrix", matrixTag);
	}

	@Override
	public IPackageRecipeType getRecipeType() {
		return LegendaryPackageRecipeType.INSTANCE;
	}

	@Override
	public int getTier() {
		return 6;
	}

	@Override
	public boolean isValid() {
		return recipe != null;
	}

	@Override
	public List<IPackagePattern> getPatterns() {
		return Collections.unmodifiableList(patterns);
	}

	@Override
	public List<IPackagePattern> getExtraPatterns() {
		return Collections.unmodifiableList(extraPatterns);
	}

	@Override
	public List<ItemStack> getInputs() {
		return Collections.unmodifiableList(input);
	}

	@Override
	public ItemStack getOutput() {
		return output.copy();
	}

	@Override
	public ITableRecipe getRecipe() {
		return recipe;
	}

	@Override
	public Container getMatrix() {
		return matrix;
	}

	@Override
	public List<ItemStack> getRemainingItems() {
		return recipe.getRemainingItems(matrix);
	}

	@Override
	public void generateFromStacks(List<ItemStack> input, List<ItemStack> output, Level level) {
		recipe = null;
		this.input.clear();
		patterns.clear();
		extraPatterns.clear();
		Int2ObjectMap<ItemStack> matrixMap = Edge3PackageRecipeHelper.INSTANCE.encoderToMatrix(input);
		matrixMap.forEach(matrix::setItem);
		ITableRecipe recipe = MiscHelper.INSTANCE.getRecipeManager().getRecipeFor(ModRecipeTypes.TABLE.get(), matrix, level).orElse(null);
		if(recipe != null) {
			this.recipe = recipe;
			List<ItemStack> actualInput = MiscHelper.INSTANCE.condenseStacks(matrix);
			if(actualInput.size() <= 81) {
				this.input.addAll(actualInput);
			}
			else for(int i = 0; i*9 < actualInput.size(); ++i) {
				IPackageRecipeInfo subRecipe = ProcessingPackageRecipeType.INSTANCE.getNewRecipeInfo();
				subRecipe.generateFromStacks(actualInput.subList(i*9, Math.min(9+i*9, actualInput.size())), List.of(), null);
				IPackagePattern subPattern = subRecipe.getPatterns().get(0);
				extraPatterns.add(subPattern);
				this.input.add(subPattern.getOutput());
			}
			this.output = recipe.assemble(matrix, MiscHelper.INSTANCE.getRegistryAccess()).copy();
			for(int i = 0; i*9 < this.input.size(); ++i) {
				patterns.add(new PackagePattern(this, i));
			}
			return;
		}
		matrix.clearContent();
	}

	@Override
	public Int2ObjectMap<ItemStack> getEncoderStacks() {
		Int2ObjectMap<ItemStack> map = new Int2ObjectOpenHashMap<>();
		for(int i = 0; i < 169; ++i) {
			map.put(i, matrix.getItem(i));
		}
		return Edge3PackageRecipeHelper.INSTANCE.matrixToEncoder(map, false);
	}

	@Override
	public boolean equals(Object obj) {
		if(obj instanceof LegendaryPackageRecipeInfo other) {
			return MiscHelper.INSTANCE.recipeEquals(this, recipe, other, other.recipe);
		}
		return false;
	}

	@Override
	public int hashCode() {
		return MiscHelper.INSTANCE.recipeHashCode(this, recipe);
	}
}
