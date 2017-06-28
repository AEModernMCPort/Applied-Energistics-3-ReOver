package appeng.core.lib.world;

import appeng.core.skyfall.api.generator.MutableBlockAccess;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldType;
import net.minecraft.world.biome.Biome;

import javax.annotation.Nullable;

public class MutableBlockAccessWorldDelegate implements MutableBlockAccess {

	protected final World delegate;

	public MutableBlockAccessWorldDelegate(World delegate){
		this.delegate = delegate;
	}

	@Override
	public void setBlockState(BlockPos pos, IBlockState state){
		delegate.setBlockState(pos, state);
	}

	@Override
	public void setTileEntity(BlockPos pos, TileEntity tile){
		delegate.setTileEntity(pos, tile);
	}

	@Nullable
	@Override
	public TileEntity getTileEntity(BlockPos pos){
		return delegate.getTileEntity(pos);
	}

	@Override
	public int getCombinedLight(BlockPos pos, int lightValue){
		return delegate.getCombinedLight(pos, lightValue);
	}

	@Override
	public IBlockState getBlockState(BlockPos pos){
		return delegate.getBlockState(pos);
	}

	@Override
	public boolean isAirBlock(BlockPos pos){
		return delegate.isAirBlock(pos);
	}

	@Override
	public Biome getBiome(BlockPos pos){
		return delegate.getBiome(pos);
	}

	@Override
	public int getStrongPower(BlockPos pos, EnumFacing direction){
		return delegate.getStrongPower(pos, direction);
	}

	@Override
	public WorldType getWorldType(){
		return delegate.getWorldType();
	}

	@Override
	public boolean isSideSolid(BlockPos pos, EnumFacing side, boolean _default){
		return delegate.isSideSolid(pos, side, _default);
	}

}
