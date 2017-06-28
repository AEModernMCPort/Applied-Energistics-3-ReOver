package appeng.core.skyfall.definition;

import appeng.core.lib.definitions.Definition;
import appeng.core.skyfall.api.definition.ISkyobjectGeneratorDefinition;
import appeng.core.skyfall.api.generator.SkyobjectGenerator;
import net.minecraft.util.ResourceLocation;

public class SkyobjectGeneratorDefinition<G extends SkyobjectGenerator> extends Definition<G> implements ISkyobjectGeneratorDefinition<G> {

	public SkyobjectGeneratorDefinition(ResourceLocation identifier, G g){
		super(identifier, g);
	}

}
