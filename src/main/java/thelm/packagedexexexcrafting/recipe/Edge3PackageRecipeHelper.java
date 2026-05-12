package thelm.packagedexexexcrafting.recipe;

import java.util.ArrayList;
import java.util.List;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;
import thelm.packagedauto.api.IPackageItem;
import thelm.packagedauto.api.IPackageRecipeInfo;
import thelm.packagedauto.item.PackageItem;
import thelm.packagedauto.recipe.IPositionedProcessingPackageRecipeInfo;
import thelm.packagedauto.recipe.PositionedProcessingPackageRecipeType;

public class Edge3PackageRecipeHelper {

	public static final Edge3PackageRecipeHelper INSTANCE = new Edge3PackageRecipeHelper();

	private static final int EDGE = 3;
	private static final int ENCODER_WIDTH = 9; // 9x9 encoder
	private static final int ENCODER_SIZE = ENCODER_WIDTH * ENCODER_WIDTH; //81
	private static final int MATRIX_WIDTH = 13; // 13x13 outer matrix
	private static final int MATRIX_SIZE = MATRIX_WIDTH * MATRIX_WIDTH; //169
	private static final int NESTED_WIDTH = 9; // nested package matrices are 9x9
	private static final int NESTED_CENTER = 4; // center index (0-based)

	private Edge3PackageRecipeHelper() {}

	public Int2ObjectMap<ItemStack> encoderToMatrix(List<ItemStack> input) {
		Int2ObjectMap<ItemStack> matrix = new Int2ObjectOpenHashMap<>();
		// Ensure counts and bounds
		for(int i = 0; i < Math.min(input.size(), ENCODER_SIZE); ++i) {
			input.get(i).setCount(1);
		}

		// Corners in encoder: (0,0), (0,8), (8,0), (8,8)
		List<ItemStack> cornerNW = itemToCorner(input.get(0), 3);
		putCornerIntoMatrix(matrix, 0, 0, cornerNW);
		List<ItemStack> cornerNE = itemToCorner(input.get(ENCODER_WIDTH - 1), 2);
		putCornerIntoMatrix(matrix, 0, MATRIX_WIDTH - EDGE, cornerNE);
		List<ItemStack> cornerSW = itemToCorner(input.get((ENCODER_WIDTH - 1) * ENCODER_WIDTH), 1);
		putCornerIntoMatrix(matrix, MATRIX_WIDTH - EDGE, 0, cornerSW);
		List<ItemStack> cornerSE = itemToCorner(input.get(ENCODER_SIZE - 1), 0);
		putCornerIntoMatrix(matrix, MATRIX_WIDTH - EDGE, MATRIX_WIDTH - EDGE, cornerSE);

		// Top and bottom edges (between corners): encoder rows 0 and 8, columns 1..7 map to matrix columns EDGE..EDGE+6
		for(int i = 1; i < ENCODER_WIDTH - 1; ++i) {
			List<ItemStack> sideN = itemToVertical(input.get(i), 1);
			putVerticalIntoMatrix(matrix, 0, EDGE + (i - 1), sideN);
		}
		for(int i = 1; i < ENCODER_WIDTH - 1; ++i) {
			List<ItemStack> sideS = itemToVertical(input.get((ENCODER_WIDTH - 1) * ENCODER_WIDTH + i), 0);
			putVerticalIntoMatrix(matrix, MATRIX_WIDTH - EDGE, EDGE + (i - 1), sideS);
		}

		// Left and right edges
		for(int i = 1; i < ENCODER_WIDTH - 1; ++i) {
			List<ItemStack> sideW = itemToHorizontal(input.get(i * ENCODER_WIDTH), 1);
			putHorizontalIntoMatrix(matrix, EDGE + (i - 1), 0, sideW);
		}
		for(int i = 1; i < ENCODER_WIDTH - 1; ++i) {
			List<ItemStack> sideE = itemToHorizontal(input.get((ENCODER_WIDTH - 1) + i * ENCODER_WIDTH), 0);
			putHorizontalIntoMatrix(matrix, EDGE + (i - 1), MATRIX_WIDTH - EDGE, sideE);
		}

		// Center 7x7 area from encoder inner area: encoder rows 1..7 cols 1..7 map to matrix rows EDGE..EDGE+6 cols EDGE..EDGE+6
		for(int i = 1; i < ENCODER_WIDTH - 1; ++i) {
			for(int j = 1; j < ENCODER_WIDTH - 1; ++j) {
				ItemStack s = input.get(i * ENCODER_WIDTH + j);
				if(s != null) matrix.put((EDGE + (i - 1)) * MATRIX_WIDTH + (EDGE + (j - 1)), s.copy());
			}
		}

		// Clean empties
		for(int i = 0; i < MATRIX_SIZE; ++i) {
			ItemStack st = matrix.getOrDefault(i, ItemStack.EMPTY);
			if (st.isEmpty()) matrix.remove(i);
		}
		return matrix;
	}

	public Int2ObjectMap<ItemStack> matrixToEncoder(Int2ObjectMap<ItemStack> matrix, boolean shapeless) {
		Int2ObjectMap<ItemStack> map = new Int2ObjectOpenHashMap<>();
		if(shapeless && matrix.size() <= ENCODER_SIZE) {
			List<ItemStack> list = new ArrayList<>();
			for(int i = 0; i < MATRIX_SIZE; ++i) {
				ItemStack stack = matrix.getOrDefault(i, ItemStack.EMPTY);
				if (!stack.isEmpty()) list.add(stack);
			}
			for(int i = 0; i < list.size(); ++i) map.put(i, list.get(i));
			return map;
		}

		// corners: read 3x3 blocks from matrix
		ItemStack cornerNW = cornerToItem(readCornerFromMatrix(matrix, 0, 0), 3);
		map.put(0, cornerNW);
		ItemStack cornerNE = cornerToItem(readCornerFromMatrix(matrix, 0, MATRIX_WIDTH - EDGE), 2);
		map.put(ENCODER_WIDTH - 1, cornerNE);
		ItemStack cornerSW = cornerToItem(readCornerFromMatrix(matrix, MATRIX_WIDTH - EDGE, 0), 1);
		map.put((ENCODER_WIDTH - 1) * ENCODER_WIDTH, cornerSW);
		ItemStack cornerSE = cornerToItem(readCornerFromMatrix(matrix, MATRIX_WIDTH - EDGE, MATRIX_WIDTH - EDGE), 0);
		map.put(ENCODER_SIZE - 1, cornerSE);

		// top/bottom edges
		for(int i = 1; i < ENCODER_WIDTH - 1; ++i) {
			ItemStack sideN = verticalToItem(readVerticalFromMatrix(matrix, 0, EDGE + (i - 1)), 1);
			map.put(i, sideN);
		}
		for(int i = 1; i < ENCODER_WIDTH - 1; ++i) {
			ItemStack sideS = verticalToItem(readVerticalFromMatrix(matrix, MATRIX_WIDTH - EDGE, EDGE + (i - 1)), 0);
			map.put((ENCODER_WIDTH - 1) * ENCODER_WIDTH + i, sideS);
		}

		// left/right edges
		for(int i = 1; i < ENCODER_WIDTH - 1; ++i) {
			ItemStack sideW = horizontalToItem(readHorizontalFromMatrix(matrix, EDGE + (i - 1), 0), 1);
			map.put(i * ENCODER_WIDTH, sideW);
		}
		for(int i = 1; i < ENCODER_WIDTH - 1; ++i) {
			ItemStack sideE = horizontalToItem(readHorizontalFromMatrix(matrix, EDGE + (i - 1), MATRIX_WIDTH - EDGE), 0);
			map.put((ENCODER_WIDTH - 1) + i * ENCODER_WIDTH, sideE);
		}

		// center 7x7
		for(int i = 1; i < ENCODER_WIDTH - 1; ++i) {
			for(int j = 1; j < ENCODER_WIDTH - 1; ++j) {
				map.put(i * ENCODER_WIDTH + j, matrix.getOrDefault((EDGE + (i - 1)) * MATRIX_WIDTH + (EDGE + (j - 1)), ItemStack.EMPTY).copy());
			}
		}

		for(int i = 0; i < ENCODER_SIZE; ++i) {
			if(map.getOrDefault(i, ItemStack.EMPTY).isEmpty()) map.remove(i);
		}
		return map;
	}

	// helpers to read/write into the 13x13 matrix
	private void putCornerIntoMatrix(Int2ObjectMap<ItemStack> matrix, int rowStart, int colStart, List<ItemStack> corner) {
		int idx = 0;
		for(int r = 0; r < EDGE; ++r) {
			for(int c = 0; c < EDGE; ++c) {
				ItemStack s = corner.get(idx++);
				if(s != null) matrix.put((rowStart + r) * MATRIX_WIDTH + (colStart + c), s.copy());
			}
		}
	}

	private void putVerticalIntoMatrix(Int2ObjectMap<ItemStack> matrix, int rowStart, int col, List<ItemStack> vert) {
		for(int r = 0; r < EDGE; ++r) {
			ItemStack s = vert.get(r);
			if(s != null) matrix.put((rowStart + r) * MATRIX_WIDTH + col, s.copy());
		}
	}

	private void putHorizontalIntoMatrix(Int2ObjectMap<ItemStack> matrix, int row, int colStart, List<ItemStack> hor) {
		for(int c = 0; c < EDGE; ++c) {
			ItemStack s = hor.get(c);
			if(s != null) matrix.put(row * MATRIX_WIDTH + (colStart + c), s.copy());
		}
	}

	// read 3x3 corner from matrix (rowStart,colStart)
	private List<ItemStack> readCornerFromMatrix(Int2ObjectMap<ItemStack> matrix, int rowStart, int colStart) {
		List<ItemStack> list = NonNullList.withSize(EDGE * EDGE, ItemStack.EMPTY);
		int idx = 0;
		for(int r = 0; r < EDGE; ++r) {
			for(int c = 0; c < EDGE; ++c) {
				list.set(idx++, matrix.getOrDefault((rowStart + r) * MATRIX_WIDTH + (colStart + c), ItemStack.EMPTY));
			}
		}
		return list;
	}

	// read vertical (3) cells starting at rowStart,col
	private List<ItemStack> readVerticalFromMatrix(Int2ObjectMap<ItemStack> matrix, int rowStart, int col) {
		List<ItemStack> list = NonNullList.withSize(EDGE, ItemStack.EMPTY);
		for(int r = 0; r < EDGE; ++r) {
			list.set(r, matrix.getOrDefault((rowStart + r) * MATRIX_WIDTH + col, ItemStack.EMPTY));
		}
		return list;
	}

	// read horizontal (3) cells starting at row,colStart
	private List<ItemStack> readHorizontalFromMatrix(Int2ObjectMap<ItemStack> matrix, int row, int colStart) {
		List<ItemStack> list = NonNullList.withSize(EDGE, ItemStack.EMPTY);
		for(int c = 0; c < EDGE; ++c) {
			list.set(c, matrix.getOrDefault(row * MATRIX_WIDTH + (colStart + c), ItemStack.EMPTY));
		}
		return list;
	}

	// item helpers: extract from nested 9x9 package or fallback
	public List<ItemStack> itemToCorner(ItemStack input, int corner) {
		List<ItemStack> list = NonNullList.withSize(EDGE * EDGE, ItemStack.EMPTY);
		if(input == null || input.isEmpty()) return list;
		if(input.getItem() instanceof IPackageItem packageItem) {
			IPackageRecipeInfo recipe = packageItem.getRecipeInfo(input);
			if(recipe instanceof IPositionedProcessingPackageRecipeInfo positionedRecipe) {
				Int2ObjectMap<ItemStack> matrix = positionedRecipe.getMatrix();
				int start = NESTED_CENTER - (EDGE / 2); // center the EDGE-sized block in 9x9
				int idx = 0;
				for(int r = start; r < start + EDGE; ++r) {
					for(int c = start; c < start + EDGE; ++c) {
						list.set(idx++, matrix.getOrDefault(r * NESTED_WIDTH + c, ItemStack.EMPTY).copy());
					}
				}
				for(ItemStack s : list) s.setCount(1);
				return list;
			}
		}
		// fallback: place into representative corner cell
		int rep;
		switch(corner) {
		case 3 -> rep = EDGE * EDGE - 1; // NW -> bottom-right
		case 2 -> rep = EDGE * (EDGE - 1); // NE -> bottom-left
		case 1 -> rep = EDGE - 1; // SW -> top-right
		default -> rep = 0; // SE -> top-left
		}
		list.set(rep, input);
		return list;
	}

	public ItemStack cornerToItem(List<ItemStack> inputs, int corner) {
		long nonEmptyCount = inputs.stream().filter(s -> s != null && !s.isEmpty()).count();
		if(nonEmptyCount == 0) return ItemStack.EMPTY;
		// if only one and it's a plain item, return it
		if(nonEmptyCount == 1) {
			ItemStack only = inputs.stream().filter(s -> s != null && !s.isEmpty()).findFirst().orElse(ItemStack.EMPTY);
			// if the single non-empty is at the expected corner index and is not a package, return it
			if(corner >= 0 && corner < inputs.size() && !inputs.get(corner).isEmpty()) {
				ItemStack stack = inputs.get(corner);
				if(!(stack.getItem() instanceof IPackageItem pkg && pkg.getRecipeInfo(stack) instanceof IPositionedProcessingPackageRecipeInfo)) return stack;
			}
			if(!(only.getItem() instanceof IPackageItem pkg && pkg.getRecipeInfo(only) instanceof IPositionedProcessingPackageRecipeInfo)) return only;
		}
		List<ItemStack> list = NonNullList.withSize(NESTED_WIDTH * NESTED_WIDTH, ItemStack.EMPTY);
		int start = NESTED_CENTER - (EDGE / 2); // center the EDGE-sized block in 9x9
		int idx = 0;
		for(int r = start; r < start + EDGE; ++r) {
			for(int c = start; c < start + EDGE; ++c) {
				list.set(r * NESTED_WIDTH + c, inputs.get(idx++));
			}
		}
		IPackageRecipeInfo recipe = PositionedProcessingPackageRecipeType.INSTANCE.getNewRecipeInfo();
		recipe.generateFromStacks(list, List.of(), null);
		return PackageItem.makePackage(recipe, 0);
	}

	public List<ItemStack> itemToVertical(ItemStack input, int side) {
		List<ItemStack> list = NonNullList.withSize(EDGE, ItemStack.EMPTY);
		if(input == null || input.isEmpty()) return list;
		if(input.getItem() instanceof IPackageItem packageItem) {
			IPackageRecipeInfo recipe = packageItem.getRecipeInfo(input);
			if (recipe instanceof IPositionedProcessingPackageRecipeInfo positionedRecipe) {
				Int2ObjectMap<ItemStack> matrix = positionedRecipe.getMatrix();
				int start = NESTED_CENTER - (EDGE / 2);
				// center column
				for(int r = 0; r < EDGE; ++r) {
					list.set(r, matrix.getOrDefault((start + r) * NESTED_WIDTH + NESTED_CENTER, ItemStack.EMPTY).copy());
				}
				for(ItemStack s : list) s.setCount(1);
				return list;
			}
		}
		// fallback: top for side==1, bottom for side==0
		if(side == 1) list.set(0, input);
		else list.set(EDGE - 1, input);
		return list;
	}

	public ItemStack verticalToItem(List<ItemStack> inputs, int side) {
		long nonEmptyCount = inputs.stream().filter(s -> s != null && !s.isEmpty()).count();
		if(nonEmptyCount == 0) return ItemStack.EMPTY;
		if(nonEmptyCount == 1) {
			ItemStack only = inputs.stream().filter(s -> s != null && !s.isEmpty()).findFirst().orElse(ItemStack.EMPTY);
			if(side >= 0 && side < inputs.size() && !inputs.get(side).isEmpty()) {
				ItemStack stack = inputs.get(side);
				if(!(stack.getItem() instanceof IPackageItem pkg && pkg.getRecipeInfo(stack) instanceof IPositionedProcessingPackageRecipeInfo)) return stack;
			}
			if(!(only.getItem() instanceof IPackageItem pkg && pkg.getRecipeInfo(only) instanceof IPositionedProcessingPackageRecipeInfo)) return only;
		}
		List<ItemStack> list = NonNullList.withSize(NESTED_WIDTH * NESTED_WIDTH, ItemStack.EMPTY);
		int start = NESTED_CENTER - (EDGE / 2);
		for(int r = 0; r < EDGE; ++r) {
			list.set((start + r) * NESTED_WIDTH + NESTED_CENTER, inputs.get(r));
		}
		IPackageRecipeInfo recipe = PositionedProcessingPackageRecipeType.INSTANCE.getNewRecipeInfo();
		recipe.generateFromStacks(list, List.of(), null);
		return PackageItem.makePackage(recipe, 0);
	}

	public List<ItemStack> itemToHorizontal(ItemStack input, int side) {
		List<ItemStack> list = NonNullList.withSize(EDGE, ItemStack.EMPTY);
		if(input == null || input.isEmpty()) return list;
		if(input.getItem() instanceof IPackageItem packageItem) {
			IPackageRecipeInfo recipe = packageItem.getRecipeInfo(input);
			if(recipe instanceof IPositionedProcessingPackageRecipeInfo positionedRecipe) {
				Int2ObjectMap<ItemStack> matrix = positionedRecipe.getMatrix();
				int start = NESTED_CENTER - (EDGE / 2);
				for(int c = 0; c < EDGE; ++c) {
					list.set(c, matrix.getOrDefault(NESTED_CENTER * NESTED_WIDTH + (start + c), ItemStack.EMPTY).copy());
				}
				for(ItemStack s : list) s.setCount(1);
				return list;
			}
		}
		if(side == 1) list.set(0, input);
		else list.set(EDGE - 1, input);
		return list;
	}

	public ItemStack horizontalToItem(List<ItemStack> inputs, int side) {
		long nonEmptyCount = inputs.stream().filter(s -> s != null && !s.isEmpty()).count();
		if(nonEmptyCount == 0) return ItemStack.EMPTY;
		if(nonEmptyCount == 1) {
			ItemStack only = inputs.stream().filter(s -> s != null && !s.isEmpty()).findFirst().orElse(ItemStack.EMPTY);
			if(side >= 0 && side < inputs.size() && !inputs.get(side).isEmpty()) {
				ItemStack stack = inputs.get(side);
				if(!(stack.getItem() instanceof IPackageItem pkg && pkg.getRecipeInfo(stack) instanceof IPositionedProcessingPackageRecipeInfo)) return stack;
			}
			if(!(only.getItem() instanceof IPackageItem pkg && pkg.getRecipeInfo(only) instanceof IPositionedProcessingPackageRecipeInfo)) return only;
		}
		List<ItemStack> list = NonNullList.withSize(NESTED_WIDTH * NESTED_WIDTH, ItemStack.EMPTY);
		int start = NESTED_CENTER - (EDGE / 2);
		for(int c = 0; c < EDGE; ++c) {
			list.set(NESTED_CENTER * NESTED_WIDTH + (start + c), inputs.get(c));
		}
		IPackageRecipeInfo recipe = PositionedProcessingPackageRecipeType.INSTANCE.getNewRecipeInfo();
		recipe.generateFromStacks(list, List.of(), null);
		return PackageItem.makePackage(recipe, 0);
	}
}

