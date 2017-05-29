package appeng.core.me.definitions;

import appeng.api.definitions.IMaterialDefinition;
import appeng.core.api.material.Material;
import appeng.core.lib.bootstrap_olde.FeatureFactory;
import appeng.core.lib.definitions.Definitions;
import appeng.core.me.api.definitions.IMEMaterialDefinitions;

public class MEMaterialDefinitions extends Definitions<Material, IMaterialDefinition<Material>>
		implements IMEMaterialDefinitions {

	private static final String MATERIALSMODELSLOCATION = "material/";
	private static final String MATERIALSMODELSVARIANT = "inventory";

	public MEMaterialDefinitions(FeatureFactory registry){
		init();
	}

}
