package appeng.core.skyfall.definitions;

import appeng.api.bootstrap.DefinitionFactory;
import appeng.core.lib.definitions.Definitions;
import appeng.core.skyfall.api.definition.ISkyobjectProviderDefinition;
import appeng.core.skyfall.api.definitions.ISkyfallSkyobjectProviderDefinitions;
import appeng.core.skyfall.api.skyobject.SkyobjectProvider;

public class SkyfallSkyobjectProviderDefinitions extends Definitions<SkyobjectProvider, ISkyobjectProviderDefinition<SkyobjectProvider>> implements ISkyfallSkyobjectProviderDefinitions {

	public SkyfallSkyobjectProviderDefinitions(DefinitionFactory registry){

	}

	private DefinitionFactory.InputHandler<SkyobjectProvider, SkyobjectProvider> ih(SkyobjectProvider skyobjectGenerator){
		return new DefinitionFactory.InputHandler<SkyobjectProvider, SkyobjectProvider>(skyobjectGenerator) {};
	}

}
