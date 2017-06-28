package appeng.core.lib.world;

import appeng.core.skyfall.api.generator.MutableBlockAccess;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;

public interface TransformingMutableBlockAccess extends TransformingBlockAccess, MutableBlockAccess{

	@Override
	MutableBlockAccess delegate();

	@Override
	default void setBlockState(BlockPos pos, IBlockState state){
		delegate().setBlockState(transform(pos), state);
	}

	@Override
	default void setTileEntity(BlockPos pos, TileEntity tile){
		delegate().setTileEntity(transform(pos), tile);
	}

}
