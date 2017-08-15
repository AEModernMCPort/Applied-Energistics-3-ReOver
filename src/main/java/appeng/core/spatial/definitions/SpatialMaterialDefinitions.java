package appeng.core.spatial.definitions;

import appeng.api.bootstrap.DefinitionFactory;
import appeng.core.core.api.definition.IMaterialDefinition;
import appeng.core.core.api.material.Material;
import appeng.core.lib.definitions.Definitions;
import appeng.core.spatial.api.definitions.ISpatialMaterialDefinitions;

@Deprecated
public class SpatialMaterialDefinitions extends Definitions<Material, IMaterialDefinition<Material>> implements ISpatialMaterialDefinitions {

	private static final String MATERIALSMODELSLOCATION = "material/";
	private static final String MATERIALSMODELSVARIANT = "inventory";

	public SpatialMaterialDefinitions(DefinitionFactory registry){

	}

	private DefinitionFactory.InputHandler<Material, Material> ih(Material material){
		return new DefinitionFactory.InputHandler<Material, Material>(material) {};
	}

}
