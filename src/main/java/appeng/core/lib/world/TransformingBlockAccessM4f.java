package appeng.core.lib.world;

import code.elix_x.excore.utils.world.TransformingBlockAccess;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import org.joml.Matrix4f;
import org.joml.Vector4f;

public class TransformingBlockAccessM4f implements TransformingBlockAccess {

	protected final IBlockAccess delegate;
	protected final Matrix4f transform;

	private float w = 1;

	public TransformingBlockAccessM4f(IBlockAccess delegate, Matrix4f transform){
		this.delegate = delegate;
		this.transform = transform;
	}

	public float getW(){
		return w;
	}

	public void setW(float w){
		this.w = w;
	}

	@Override
	public IBlockAccess delegate(){
		return delegate;
	}

	public BlockPos transform(BlockPos pos){
		Vector4f res = new Vector4f(pos.getX(), pos.getY(), pos.getZ(), w).mul(transform);
		return new BlockPos(res.x, res.y, res.z);
	}

	public EnumFacing transform(EnumFacing facing){
		Vector4f res = new Vector4f(facing.getDirectionVec().getX(), facing.getDirectionVec().getY(), facing.getDirectionVec().getZ(), 0).mul(transform);
		return EnumFacing.getFacingFromVector(res.x, res.y, res.z);
	}

}
