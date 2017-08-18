package appeng.core.skyfall.api.bootstrap;

import appeng.api.bootstrap.IDefinitionBuilder;
import appeng.core.skyfall.api.definition.ISkyobjectProviderDefinition;
import appeng.core.skyfall.api.skyobject.SkyobjectProvider;

public interface ISkyobjectGeneratorBuilder<G extends SkyobjectProvider, GG extends ISkyobjectGeneratorBuilder<G, GG>> extends IDefinitionBuilder<G, ISkyobjectProviderDefinition<G>, GG> {}
