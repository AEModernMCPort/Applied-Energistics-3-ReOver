package appeng.core.core.client.render.model;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.block.model.ItemOverrideList;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.EnumFacing;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nullable;
import javax.vecmath.Matrix4f;
import java.util.List;

public class BakedModelDelegateBase implements IBakedModel {

	protected final IBakedModel delegate;

	public BakedModelDelegateBase(IBakedModel delegate){
		this.delegate = delegate;
	}

	@Override
	public List<BakedQuad> getQuads(@Nullable IBlockState state, @Nullable EnumFacing side, long rand){
		return delegate.getQuads(state, side, rand);
	}

	@Override
	public boolean isAmbientOcclusion(){
		return delegate.isAmbientOcclusion();
	}

	@Override
	public boolean isGui3d(){
		return delegate.isGui3d();
	}

	@Override
	public boolean isBuiltInRenderer(){
		return delegate.isBuiltInRenderer();
	}

	@Override
	public TextureAtlasSprite getParticleTexture(){
		return delegate.getParticleTexture();
	}

	@Override
	public ItemOverrideList getOverrides(){
		return delegate.getOverrides();
	}

	@Override
	public ItemCameraTransforms getItemCameraTransforms(){
		return delegate.getItemCameraTransforms();
	}

	@Override
	public Pair<? extends IBakedModel, Matrix4f> handlePerspective(ItemCameraTransforms.TransformType cameraTransformType){
		return delegate.handlePerspective(cameraTransformType);
	}

}
