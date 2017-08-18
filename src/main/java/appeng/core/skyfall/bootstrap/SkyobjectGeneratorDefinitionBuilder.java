package appeng.core.skyfall.bootstrap;

import appeng.api.bootstrap.DefinitionFactory;
import appeng.core.lib.bootstrap.DefinitionBuilder;
import appeng.core.skyfall.api.bootstrap.ISkyobjectGeneratorBuilder;
import appeng.core.skyfall.api.definition.ISkyobjectProviderDefinition;
import appeng.core.skyfall.api.skyobject.SkyobjectProvider;
import appeng.core.skyfall.definition.SkyobjectProviderDefinition;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nullable;

public class SkyobjectGeneratorDefinitionBuilder<G extends SkyobjectProvider> extends DefinitionBuilder<G, G, ISkyobjectProviderDefinition<G>, SkyobjectGeneratorDefinitionBuilder<G>> implements ISkyobjectGeneratorBuilder<G, SkyobjectGeneratorDefinitionBuilder<G>>{

	public SkyobjectGeneratorDefinitionBuilder(DefinitionFactory factory, ResourceLocation registryName, G instance){
		super(factory, registryName, instance, "skyobject_generator");
	}

	@Override
	protected ISkyobjectProviderDefinition<G> def(@Nullable G skyobjectGenerator){
		return new SkyobjectProviderDefinition<>(registryName, skyobjectGenerator);
	}

}
