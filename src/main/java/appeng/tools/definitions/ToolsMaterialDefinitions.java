package appeng.tools.definitions;

import appeng.api.bootstrap.DefinitionFactory;
import appeng.core.core.api.definition.IMaterialDefinition;
import appeng.core.core.api.material.Material;
import appeng.core.lib.definitions.Definitions;
import appeng.tools.api.definitions.IToolsMaterialDefinitions;

@Deprecated
public class ToolsMaterialDefinitions extends Definitions<Material, IMaterialDefinition<Material>> implements IToolsMaterialDefinitions {

	private static final String MATERIALSMODELSLOCATION = "material/";
	private static final String MATERIALSMODELSVARIANT = "inventory";

	public ToolsMaterialDefinitions(DefinitionFactory registry){

	}

	private DefinitionFactory.InputHandler<Material, Material> ih(Material material){
		return new DefinitionFactory.InputHandler<Material, Material>(material) {};
	}

}
