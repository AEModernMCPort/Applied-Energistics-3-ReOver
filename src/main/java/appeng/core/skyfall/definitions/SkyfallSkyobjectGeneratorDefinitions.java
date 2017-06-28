package appeng.core.skyfall.definitions;

import appeng.api.bootstrap.DefinitionFactory;
import appeng.core.lib.definitions.Definitions;
import appeng.core.skyfall.api.definition.ISkyobjectGeneratorDefinition;
import appeng.core.skyfall.api.definitions.ISkyfallSkyobjectGeneratorDefinitions;
import appeng.core.skyfall.api.generator.SkyobjectGenerator;

public class SkyfallSkyobjectGeneratorDefinitions extends Definitions<SkyobjectGenerator, ISkyobjectGeneratorDefinition<SkyobjectGenerator>> implements ISkyfallSkyobjectGeneratorDefinitions {

	public SkyfallSkyobjectGeneratorDefinitions(DefinitionFactory registry){

	}

	private DefinitionFactory.InputHandler<SkyobjectGenerator, SkyobjectGenerator> ih(SkyobjectGenerator skyobjectGenerator){
		return new DefinitionFactory.InputHandler<SkyobjectGenerator, SkyobjectGenerator>(skyobjectGenerator) {};
	}

}
