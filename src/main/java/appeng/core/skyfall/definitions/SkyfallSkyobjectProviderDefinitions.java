package appeng.core.skyfall.definitions;

import appeng.api.bootstrap.DefinitionFactory;
import appeng.core.AppEng;
import appeng.core.lib.definitions.Definitions;
import appeng.core.skyfall.api.bootstrap.ISkyobjectGeneratorBuilder;
import appeng.core.skyfall.api.definition.ISkyobjectProviderDefinition;
import appeng.core.skyfall.api.definitions.ISkyfallSkyobjectProviderDefinitions;
import appeng.core.skyfall.api.skyobject.SkyobjectProvider;
import appeng.core.skyfall.skyobject.objects.MeteoriteProvider;
import net.minecraft.util.ResourceLocation;

public class SkyfallSkyobjectProviderDefinitions extends Definitions<SkyobjectProvider, ISkyobjectProviderDefinition<SkyobjectProvider>> implements ISkyfallSkyobjectProviderDefinitions {

	private final ISkyobjectProviderDefinition meteorite;

	public SkyfallSkyobjectProviderDefinitions(DefinitionFactory registry){
		meteorite = registry.<MeteoriteProvider, ISkyobjectProviderDefinition<MeteoriteProvider>, ISkyobjectGeneratorBuilder<MeteoriteProvider, ?>, MeteoriteProvider>definitionBuilder(new ResourceLocation(AppEng.MODID, "meteorite"), ih(new MeteoriteProvider(23))).build();
	}

	private DefinitionFactory.InputHandler<SkyobjectProvider, SkyobjectProvider> ih(SkyobjectProvider skyobjectGenerator){
		return new DefinitionFactory.InputHandler<SkyobjectProvider, SkyobjectProvider>(skyobjectGenerator) {};
	}

}
