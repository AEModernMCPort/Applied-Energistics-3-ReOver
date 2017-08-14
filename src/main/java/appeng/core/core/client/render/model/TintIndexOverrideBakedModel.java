package appeng.core.core.client.render.model;

import com.google.common.collect.Lists;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.util.EnumFacing;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Function;

public class TintIndexOverrideBakedModel extends BakedModelDelegateBase {

	public Function<BakedQuad, Integer> newTintIndex;

	public TintIndexOverrideBakedModel(IBakedModel delegate, Function<BakedQuad, Integer> newTintIndex){
		super(delegate);
		this.newTintIndex = newTintIndex;
	}

	public TintIndexOverrideBakedModel(IBakedModel delegate, int newTintIndex){
		this(delegate, quad -> newTintIndex);
	}

	@Override
	public List<BakedQuad> getQuads(@Nullable IBlockState state, @Nullable EnumFacing side, long rand){
		return Lists.transform(super.getQuads(state, side, rand), quad -> new BakedQuad(quad.getVertexData(), newTintIndex.apply(quad), quad.getFace(), quad.getSprite(), quad.shouldApplyDiffuseLighting(), quad.getFormat()));
	}
}
