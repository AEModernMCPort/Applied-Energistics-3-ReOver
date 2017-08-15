package appeng.core.me.definitions;

import appeng.api.bootstrap.DefinitionFactory;
import appeng.core.core.api.definition.IMaterialDefinition;
import appeng.core.core.api.material.Material;
import appeng.core.lib.definitions.Definitions;
import appeng.core.me.api.definitions.IMEMaterialDefinitions;
@Deprecated
public class MEMaterialDefinitions extends Definitions<Material, IMaterialDefinition<Material>> implements IMEMaterialDefinitions {

	private static final String MATERIALSMODELSLOCATION = "material/";
	private static final String MATERIALSMODELSVARIANT = "inventory";

	public MEMaterialDefinitions(DefinitionFactory registry){

	}

	private DefinitionFactory.InputHandler<Material, Material> ih(Material material){
		return new DefinitionFactory.InputHandler<Material, Material>(material) {};
	}

}
