package appeng.core.spatial.definitions;

import appeng.api.definitions.IMaterialDefinition;
import appeng.core.api.material.Material;
import appeng.core.lib.bootstrap.FeatureFactory;
import appeng.core.lib.definitions.Definitions;
import appeng.core.spatial.api.definitions.ISpatialMaterialDefinitions;

public class SpatialMaterialDefinitions extends Definitions<Material, IMaterialDefinition<Material>>
		implements ISpatialMaterialDefinitions {

	private static final String MATERIALSMODELSLOCATION = "material/";
	private static final String MATERIALSMODELSVARIANT = "inventory";

	public SpatialMaterialDefinitions(FeatureFactory registry){
		init();
	}

}
