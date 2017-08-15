package appeng.core.core.definitions;

import appeng.api.bootstrap.DefinitionFactory;
import appeng.core.core.api.definition.IMaterialDefinition;
import appeng.core.core.api.definitions.ICoreMaterialDefinitions;
import appeng.core.core.api.material.Material;
import appeng.core.lib.definitions.Definitions;

@Deprecated
public class CoreMaterialDefinitions extends Definitions<Material, IMaterialDefinition<Material>> implements ICoreMaterialDefinitions {

	public CoreMaterialDefinitions(DefinitionFactory registry){

	}

	private DefinitionFactory.InputHandler<Material, Material> ih(Material material){
		return new DefinitionFactory.InputHandler<Material, Material>(material) {};
	}

}
