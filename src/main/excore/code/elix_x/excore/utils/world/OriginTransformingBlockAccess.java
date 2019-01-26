package code.elix_x.excore.utils.world;

import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;

public class OriginTransformingBlockAccess implements TransformingBlockAccess {

	protected final IBlockAccess delegate;
	protected final BlockPos origin;

	public OriginTransformingBlockAccess(IBlockAccess delegate, BlockPos origin){
		this.delegate = delegate;
		this.origin = origin;
	}

	@Override
	public IBlockAccess delegate(){
		return delegate;
	}

	@Override
	public BlockPos transform(BlockPos pos){
		return pos.add(origin);
	}

	@Override
	public EnumFacing transform(EnumFacing facing){
		return facing;
	}
}
