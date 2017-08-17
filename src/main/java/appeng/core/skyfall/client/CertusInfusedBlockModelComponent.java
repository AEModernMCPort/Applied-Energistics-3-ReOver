package appeng.core.skyfall.client;

import appeng.api.bootstrap.IDefinitionBuilder;
import appeng.core.core.api.definition.IBlockDefinition;
import appeng.core.core.client.render.model.ModelRegManagerHelper;
import appeng.core.skyfall.AppEngSkyfall;
import appeng.core.skyfall.block.CertusInfusedBlock;
import com.google.common.collect.Lists;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.model.SimpleBakedModel;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nullable;
import java.util.List;

public class CertusInfusedBlockModelComponent<B extends CertusInfusedBlock> implements IDefinitionBuilder.DefinitionInitializationComponent<B, IBlockDefinition<B>> {

	private boolean first = true;

	@Override
	public void preInit(IBlockDefinition<B> def){
		def.maybe().ifPresent(block -> {
			ResourceLocation infusedRL = new ResourceLocation(def.identifier().getResourceDomain(), AppEngSkyfall.NAME + "/certus_infused");
			ModelResourceLocation infusedOverlay = new ModelResourceLocation(infusedRL, "normal");
			if(first){
				ModelRegManagerHelper.loadAndRegisterModel(infusedOverlay, infusedOverlay);
				first = false;
			}
			ModelRegManagerHelper.acceptBakeEventListener(event -> {
				IBakedModel overlay = event.getModelRegistry().getObject(infusedOverlay);
				IBlockState varState = block.getOriginal();
				event.getModelRegistry().putObject(new ModelResourceLocation(new ResourceLocation(def.identifier().getResourceDomain(), AppEngSkyfall.NAME + "/" + def.identifier().getResourcePath()), "normal"), new SimpleBakedModel(null, null, overlay.isAmbientOcclusion(), overlay.isGui3d(), overlay.getParticleTexture(), overlay.getItemCameraTransforms(), overlay.getOverrides()) {

					IBakedModel infused;

					IBakedModel infused(){
						if(infused == null) infused = Minecraft.getMinecraft().getBlockRendererDispatcher().getBlockModelShapes().getModelForState(varState);
						return infused;
					}

					@Override
					public List<BakedQuad> getQuads(@Nullable IBlockState state, @Nullable EnumFacing side, long rand){
						List<BakedQuad> list = Lists.newArrayList(infused().getQuads(varState, side, rand));
						list.addAll(overlay.getQuads(state, side, rand));
						return list;
					}

				});
			});
		});
	}

}