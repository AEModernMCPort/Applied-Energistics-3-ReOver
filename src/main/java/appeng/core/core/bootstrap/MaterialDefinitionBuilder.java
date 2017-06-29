package appeng.core.core.bootstrap;

import appeng.api.bootstrap.DefinitionFactory;
import appeng.api.definitions.IMaterialDefinition;
import appeng.core.api.bootstrap.IMaterialBuilder;
import appeng.core.api.material.Material;
import appeng.core.lib.bootstrap.DefinitionBuilder;
import appeng.core.lib.definitions.MaterialDefinition;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.util.ResourceLocation;

public class MaterialDefinitionBuilder<M extends Material> extends DefinitionBuilder<M, M, IMaterialDefinition<M>, MaterialDefinitionBuilder<M>> implements IMaterialBuilder<M, MaterialDefinitionBuilder<M>> {

	private ResourceLocation model;

	public MaterialDefinitionBuilder(DefinitionFactory factory, ResourceLocation registryName, M material){
		super(factory, registryName, material, "material");
	}

	@Override
	public MaterialDefinitionBuilder<M> model(ResourceLocation model){
		this.model = model;
		return this;
	}

	@Override
	public IMaterialDefinition<M> def(M material){
		material.setUnlocalizedName(registryName.getResourceDomain() + "." + registryName.getResourcePath());
		if(model != null) material.setModel(model);

		return new MaterialDefinition(registryName, material);
	}
}
