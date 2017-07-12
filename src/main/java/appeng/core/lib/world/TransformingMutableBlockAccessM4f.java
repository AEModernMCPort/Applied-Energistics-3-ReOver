package appeng.core.lib.world;

import code.elix_x.excore.utils.world.MutableBlockAccess;
import code.elix_x.excore.utils.world.TransformingMutableBlockAccess;
import org.joml.Matrix4f;

public class TransformingMutableBlockAccessM4f extends TransformingBlockAccessM4f implements TransformingMutableBlockAccess {

	protected final MutableBlockAccess delegate;

	public TransformingMutableBlockAccessM4f(MutableBlockAccess delegate, Matrix4f transform){
		super(delegate, transform);
		this.delegate = delegate;
	}

	@Override
	public MutableBlockAccess delegate(){
		return delegate;
	}

}
