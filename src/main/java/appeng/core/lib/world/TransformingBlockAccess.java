package appeng.core.lib.world;

import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.WorldType;
import net.minecraft.world.biome.Biome;

import javax.annotation.Nullable;

public interface TransformingBlockAccess extends IBlockAccess {

	IBlockAccess delegate();
	
	BlockPos transform(BlockPos pos);

	EnumFacing transform(EnumFacing facing);

	@Nullable
	@Override
	default TileEntity getTileEntity(BlockPos pos){
		return delegate().getTileEntity(transform(pos));
	}

	@Override
	default int getCombinedLight(BlockPos pos, int lightValue){
		return delegate().getCombinedLight(transform(pos), lightValue);
	}

	@Override
	default IBlockState getBlockState(BlockPos pos){
		return delegate().getBlockState(transform(pos));
	}

	@Override
	default boolean isAirBlock(BlockPos pos){
		return delegate().isAirBlock(transform(pos));
	}

	@Override
	default Biome getBiome(BlockPos pos){
		return delegate().getBiome(transform(pos));
	}

	@Override
	default int getStrongPower(BlockPos pos, EnumFacing direction){
		return delegate().getStrongPower(transform(pos), transform(direction));
	}

	@Override
	default WorldType getWorldType(){
		return delegate().getWorldType();
	}

	@Override
	default boolean isSideSolid(BlockPos pos, EnumFacing side, boolean _default){
		return delegate().isSideSolid(transform(pos), transform(side), _default);
	}
	
}
