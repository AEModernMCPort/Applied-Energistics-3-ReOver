
package appeng.core.me.bootstrap;


import net.minecraft.util.ResourceLocation;

import appeng.core.lib.bootstrap.DefinitionBuilder;
import appeng.core.lib.bootstrap.FeatureFactory;
import appeng.core.me.api.definitions.IPartDefinition;
import appeng.core.me.api.part.PartRegistryEntry;
import appeng.core.me.definitions.PartDefinition;


public class PartDefinitionBuilder<P extends PartRegistryEntry> extends DefinitionBuilder<P, P, IPartDefinition<P>, PartDefinitionBuilder<P>> implements IPartBuilder<P, PartDefinitionBuilder<P>>
{

	PartDefinitionBuilder( FeatureFactory factory, ResourceLocation registryName, P part )
	{
		super( factory, registryName, part );
	}

	@Override
	public IPartDefinition<P> def( PartRegistryEntry part )
	{
		if( part == null )
		{
			return new PartDefinition( registryName, null, null );
		}
		return new PartDefinition( registryName, part, ( (MEFeatureFactory) factory ).defaultItemPartTemp( part ) );
	}
}
