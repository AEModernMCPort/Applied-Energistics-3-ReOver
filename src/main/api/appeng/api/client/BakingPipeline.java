package appeng.api.client;


import java.util.List;

import com.google.common.collect.ImmutableList;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.util.EnumFacing;


public class BakingPipeline<F, T> implements BakingPipelineElement<F, T>
{

	private final ImmutableList<BakingPipelineElement<?, ?>> pipeline;

	public BakingPipeline( BakingPipelineElement<?, ?>... pipeline )
	{
		this.pipeline = ImmutableList.copyOf( pipeline );
	}

	public List pipe( List things, IBakedModel parent, IBlockState state, EnumFacing side, long rand )
	{
		for( BakingPipelineElement pipe : pipeline )
		{
			things = pipe.pipe( things, parent, state, side, rand );
		}
		return things;
	}

}
