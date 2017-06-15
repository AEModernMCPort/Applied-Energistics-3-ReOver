package appeng.core.spatial.definitions;

import appeng.api.bootstrap.DefinitionFactory;
import appeng.api.definitions.IMaterialDefinition;
import appeng.core.api.material.Material;
import appeng.core.lib.definitions.Definitions;
import appeng.core.spatial.api.definitions.ISpatialMaterialDefinitions;

public class SpatialMaterialDefinitions extends Definitions<Material, IMaterialDefinition<Material>> implements ISpatialMaterialDefinitions {

	private static final String MATERIALSMODELSLOCATION = "material/";
	private static final String MATERIALSMODELSVARIANT = "inventory";

	public SpatialMaterialDefinitions(DefinitionFactory registry){

	}

	private DefinitionFactory.InputHandler<Material, Material> ih(Material material){
		return new DefinitionFactory.InputHandler<Material, Material>(material) {};
	}

}
