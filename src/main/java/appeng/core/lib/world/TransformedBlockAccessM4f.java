package appeng.core.lib.world;

import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.WorldType;
import net.minecraft.world.biome.Biome;
import org.joml.Matrix4f;
import org.joml.Vector4f;

import javax.annotation.Nullable;

public class TransformedBlockAccessM4f implements IBlockAccess {

	protected final IBlockAccess delegate;
	protected final Matrix4f transform;

	private float w = 1;

	public TransformedBlockAccessM4f(IBlockAccess delegate, Matrix4f transform){
		this.delegate = delegate;
		this.transform = transform;
	}

	public float getW(){
		return w;
	}

	public void setW(float w){
		this.w = w;
	}

	public BlockPos transform(BlockPos pos){
		Vector4f res = new Vector4f(pos.getX(), pos.getY(), pos.getZ(), w).mul(transform);
		return new BlockPos(res.x, res.y, res.z);
	}

	public EnumFacing transform(EnumFacing facing){
		Vector4f res = new Vector4f(facing.getDirectionVec().getX(), facing.getDirectionVec().getY(), facing.getDirectionVec().getZ(), 0).mul(transform);
		return EnumFacing.getFacingFromVector(res.x, res.y, res.z);
	}

	@Nullable
	@Override
	public TileEntity getTileEntity(BlockPos pos){
		return delegate.getTileEntity(transform(pos));
	}

	@Override
	public int getCombinedLight(BlockPos pos, int lightValue){
		return delegate.getCombinedLight(transform(pos), lightValue);
	}

	@Override
	public IBlockState getBlockState(BlockPos pos){
		return delegate.getBlockState(transform(pos));
	}

	@Override
	public boolean isAirBlock(BlockPos pos){
		return delegate.isAirBlock(transform(pos));
	}

	@Override
	public Biome getBiome(BlockPos pos){
		return delegate.getBiome(transform(pos));
	}

	@Override
	public int getStrongPower(BlockPos pos, EnumFacing direction){
		return delegate.getStrongPower(transform(pos), direction);
	}

	@Override
	public WorldType getWorldType(){
		return delegate.getWorldType();
	}

	@Override
	public boolean isSideSolid(BlockPos pos, EnumFacing side, boolean _default){
		return delegate.isSideSolid(transform(pos), side, _default);
	}
}
