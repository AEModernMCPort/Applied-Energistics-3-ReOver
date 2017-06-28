package appeng.core.lib.world;

import appeng.core.skyfall.api.generator.MutableBlockAccess;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import org.joml.Matrix4f;

public class TransformedMutableBlockAccessM4f extends TransformedBlockAccessM4f implements MutableBlockAccess {

	protected final MutableBlockAccess delegate;

	public TransformedMutableBlockAccessM4f(MutableBlockAccess delegate, Matrix4f transform){
		super(delegate, transform);
		this.delegate = delegate;
	}

	@Override
	public void setBlockState(BlockPos pos, IBlockState state){
		delegate.setBlockState(transform(pos), state);
	}

	@Override
	public void setTileEntity(BlockPos pos, TileEntity tile){
		delegate.setTileEntity(transform(pos), tile);
	}

}
