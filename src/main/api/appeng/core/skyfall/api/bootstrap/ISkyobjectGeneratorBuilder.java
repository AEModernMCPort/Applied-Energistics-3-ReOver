package appeng.core.skyfall.api.bootstrap;

import appeng.api.bootstrap.IDefinitionBuilder;
import appeng.core.skyfall.api.definition.ISkyobjectGeneratorDefinition;
import appeng.core.skyfall.api.generator.SkyobjectGenerator;

public interface ISkyobjectGeneratorBuilder<G extends SkyobjectGenerator, GG extends ISkyobjectGeneratorBuilder<G, GG>> extends IDefinitionBuilder<G, ISkyobjectGeneratorDefinition<G>, GG> {}
