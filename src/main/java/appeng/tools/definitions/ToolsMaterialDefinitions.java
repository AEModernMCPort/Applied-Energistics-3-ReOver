package appeng.tools.definitions;

import appeng.api.bootstrap.DefinitionFactory;
import appeng.api.definitions.IMaterialDefinition;
import appeng.core.api.material.Material;
import appeng.core.lib.definitions.Definitions;
import appeng.tools.api.definitions.IToolsMaterialDefinitions;

public class ToolsMaterialDefinitions extends Definitions<Material, IMaterialDefinition<Material>> implements IToolsMaterialDefinitions {

	private static final String MATERIALSMODELSLOCATION = "material/";
	private static final String MATERIALSMODELSVARIANT = "inventory";

	public ToolsMaterialDefinitions(DefinitionFactory registry){

	}

}
