package appeng.core.api.bootstrap;

import appeng.api.bootstrap.IDefinitionBuilder;
import appeng.api.definitions.IMaterialDefinition;
import appeng.core.api.material.Material;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;

public interface IMaterialBuilder<M extends Material, MM extends IMaterialBuilder<M, MM>> extends IDefinitionBuilder<M, IMaterialDefinition<M>, MM> {

	MM model(ModelResourceLocation model);

}
