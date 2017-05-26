
package appeng.core.lib.bootstrap;


import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.EntityEntry;

import appeng.api.definitions.IEntityDefinition;
import appeng.core.lib.definitions.EntityDefinition;


public class EntityDefinitionBuilder<E extends EntityEntry> extends DefinitionBuilder<E, E, IEntityDefinition<E>, EntityDefinitionBuilder<E>> implements IEntityBuilder<E, EntityDefinitionBuilder<E>>
{

	EntityDefinitionBuilder( FeatureFactory factory, ResourceLocation registryName, E entity )
	{
		super( factory, registryName, entity );
	}

	@Override
	protected IEntityDefinition<E> def( E entity )
	{
		return new EntityDefinition<E>( registryName, entity );
	}
}
