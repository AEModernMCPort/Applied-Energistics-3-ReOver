package appeng.core.skyfall.api.generator;

import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;

public interface MutableBlockAccess extends IBlockAccess {

	void setBlockState(BlockPos pos, IBlockState state);

	void setTileEntity(BlockPos pos, TileEntity tile);

}
