package appeng.core.lib.bootstrap_olde;

import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.IStateMapper;
import net.minecraft.client.renderer.color.IBlockColor;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.function.BiFunction;

class BlockRendering implements IBlockRendering {

	@SideOnly(Side.CLIENT)
	private BiFunction<ModelResourceLocation, IBakedModel, IBakedModel> modelCustomizer;

	@SideOnly(Side.CLIENT)
	private IBlockColor blockColor;

	@SideOnly(Side.CLIENT)
	private TileEntitySpecialRenderer<?> tesr;

	@SideOnly(Side.CLIENT)
	private IStateMapper stateMapper;

	@SideOnly(Side.CLIENT)
	public IBlockRendering modelCustomizer(BiFunction<ModelResourceLocation, IBakedModel, IBakedModel> customizer){
		modelCustomizer = customizer;
		return this;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public IBlockRendering blockColor(IBlockColor blockColor){
		this.blockColor = blockColor;
		return this;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public IBlockRendering tesr(TileEntitySpecialRenderer<?> tesr){
		this.tesr = tesr;
		return this;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public IBlockRendering stateMapper(IStateMapper mapper){
		this.stateMapper = mapper;
		return this;
	}

	/*void apply(FeatureFactory registry, Block block, Class<?> tileEntityClass){
		if(tesr != null){
			if(tileEntityClass == null){
				throw new IllegalStateException("Tried to register a TESR for " + block + " even though no tile entity has been specified.");
			}
			registry.addBootstrapComponent(new TesrComponent(tileEntityClass, tesr));
		}

		if(modelCustomizer != null){
			registry.modelOverrideComponent.addOverride(block.getRegistryName().getResourcePath(), modelCustomizer);
		}

		if(blockColor != null){
			registry.addBootstrapComponent(new BlockColorComponent(block, blockColor));
		}

		if(stateMapper != null){
			registry.addBootstrapComponent(new StateMapperComponent(block, stateMapper));
		}
	}*/
}
