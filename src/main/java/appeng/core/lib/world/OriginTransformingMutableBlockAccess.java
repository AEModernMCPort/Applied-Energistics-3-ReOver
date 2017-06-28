package appeng.core.lib.world;

import appeng.core.skyfall.api.generator.MutableBlockAccess;
import net.minecraft.util.math.BlockPos;

public class OriginTransformingMutableBlockAccess extends OriginTransformingBlockAccess implements TransformingMutableBlockAccess {

	protected final MutableBlockAccess delegate;

	public OriginTransformingMutableBlockAccess(MutableBlockAccess delegate, BlockPos origin){
		super(delegate, origin);
		this.delegate = delegate;
	}

	@Override
	public MutableBlockAccess delegate(){
		return delegate;
	}

}
