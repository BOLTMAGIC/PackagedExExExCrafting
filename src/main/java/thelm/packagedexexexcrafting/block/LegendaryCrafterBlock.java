package thelm.packagedexexexcrafting.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import thelm.packagedauto.block.BaseBlock;
import thelm.packagedauto.block.entity.BaseBlockEntity;
import thelm.packagedexexexcrafting.block.entity.LegendaryCrafterBlockEntity;

public class LegendaryCrafterBlock extends BaseBlock {

	public static final LegendaryCrafterBlock INSTANCE = new LegendaryCrafterBlock();
	public static final Item ITEM_INSTANCE = new BlockItem(INSTANCE, new Item.Properties());

	public LegendaryCrafterBlock() {
		super(Properties.of().strength(10F, 15F).mapColor(MapColor.METAL).sound(SoundType.METAL));
	}

	@Override
	public LegendaryCrafterBlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return LegendaryCrafterBlockEntity.TYPE_INSTANCE.create(pos, state);
	}

	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> blockEntityType) {
		return BaseBlockEntity::tick;
	}
}
