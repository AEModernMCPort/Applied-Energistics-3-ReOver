package code.elix_x.excore.utils.world;

import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Biomes;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.WorldType;
import net.minecraft.world.biome.Biome;

import javax.annotation.Nullable;

public class LimitedBlockAccess implements IBlockAccess {

	private final IBlockAccess delegate;
	private final AxisAlignedBB limits;

	public LimitedBlockAccess(IBlockAccess delegate, AxisAlignedBB limits){
		this.delegate = delegate;
		this.limits = limits;
	}

	public boolean contains(BlockPos pos){
		return limits.intersects(new AxisAlignedBB(pos));
	}

	@Nullable
	@Override
	public TileEntity getTileEntity(BlockPos pos){
		return contains(pos) ? delegate.getTileEntity(pos) : null;
	}

	@Override
	public int getCombinedLight(BlockPos pos, int lightValue){
		return contains(pos) ? delegate.getCombinedLight(pos, lightValue) : lightValue;
	}

	@Override
	public IBlockState getBlockState(BlockPos pos){
		return contains(pos) ? delegate.getBlockState(pos) : Blocks.AIR.getDefaultState();
	}

	@Override
	public boolean isAirBlock(BlockPos pos){
		return contains(pos) ? delegate.isAirBlock(pos) : true;
	}

	@Override
	public Biome getBiome(BlockPos pos){
		return contains(pos) ? delegate.getBiome(pos) : Biomes.PLAINS;
	}

	@Override
	public int getStrongPower(BlockPos pos, EnumFacing direction){
		return contains(pos) ? delegate.getStrongPower(pos, direction) : 0;
	}

	@Override
	public WorldType getWorldType(){
		return delegate.getWorldType();
	}

	@Override
	public boolean isSideSolid(BlockPos pos, EnumFacing side, boolean _default){
		return contains(pos) ? delegate.isSideSolid(pos, side, _default) : false;
	}
}
