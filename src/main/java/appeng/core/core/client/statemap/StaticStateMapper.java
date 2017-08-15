package appeng.core.core.client.statemap;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.StateMapperBase;

public class StaticStateMapper extends StateMapperBase {

	private final ModelResourceLocation model;

	public StaticStateMapper(ModelResourceLocation model){
		this.model = model;
	}

	@Override
	protected ModelResourceLocation getModelResourceLocation(IBlockState state){
		return model;
	}

}
