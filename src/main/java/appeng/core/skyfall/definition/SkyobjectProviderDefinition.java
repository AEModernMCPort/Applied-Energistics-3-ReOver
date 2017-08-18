package appeng.core.skyfall.definition;

import appeng.core.lib.definition.Definition;
import appeng.core.skyfall.api.definition.ISkyobjectProviderDefinition;
import appeng.core.skyfall.api.skyobject.SkyobjectProvider;
import net.minecraft.util.ResourceLocation;

public class SkyobjectProviderDefinition<G extends SkyobjectProvider> extends Definition<G> implements ISkyobjectProviderDefinition<G> {

	public SkyobjectProviderDefinition(ResourceLocation identifier, G g){
		super(identifier, g);
	}

}
