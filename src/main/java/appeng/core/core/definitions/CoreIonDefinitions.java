package appeng.core.core.definitions;

import appeng.api.bootstrap.DefinitionFactory;
import appeng.core.core.api.crafting.ion.Ion;
import appeng.core.core.api.definition.IIonDefinition;
import appeng.core.core.api.definitions.ICoreIonDefinitions;
import appeng.core.lib.definitions.Definitions;

public class CoreIonDefinitions extends Definitions<Ion, IIonDefinition<Ion>> implements ICoreIonDefinitions {

	public CoreIonDefinitions(DefinitionFactory factory){

	}

	private DefinitionFactory.InputHandler<Ion, Ion> ih(Ion entity){
		return new DefinitionFactory.InputHandler<Ion, Ion>(entity) {};
	}

}
