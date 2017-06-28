package appeng.core.skyfall.bootstrap;

import appeng.api.bootstrap.DefinitionFactory;
import appeng.core.lib.bootstrap.DefinitionBuilder;
import appeng.core.skyfall.api.bootstrap.ISkyobjectGeneratorBuilder;
import appeng.core.skyfall.api.definition.ISkyobjectGeneratorDefinition;
import appeng.core.skyfall.api.generator.SkyobjectGenerator;
import appeng.core.skyfall.definition.SkyobjectGeneratorDefinition;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nullable;

public class SkyobjectGeneratorDefinitionBuilder<G extends SkyobjectGenerator> extends DefinitionBuilder<G, G, ISkyobjectGeneratorDefinition<G>, SkyobjectGeneratorDefinitionBuilder<G>> implements ISkyobjectGeneratorBuilder<G, SkyobjectGeneratorDefinitionBuilder<G>>{

	public SkyobjectGeneratorDefinitionBuilder(DefinitionFactory factory, ResourceLocation registryName, G instance){
		super(factory, registryName, instance, "skyobject_generator");
	}

	@Override
	protected ISkyobjectGeneratorDefinition<G> def(@Nullable G skyobjectGenerator){
		return new SkyobjectGeneratorDefinition<>(registryName, skyobjectGenerator);
	}

}
