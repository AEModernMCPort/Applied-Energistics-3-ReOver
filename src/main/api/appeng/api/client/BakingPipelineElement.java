package appeng.api.client;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.util.EnumFacing;

import java.util.List;

public interface BakingPipelineElement<F, T> {

	public List<T> pipe(List<F> elements, IBakedModel parent, IBlockState state, EnumFacing side, long rand);

}
