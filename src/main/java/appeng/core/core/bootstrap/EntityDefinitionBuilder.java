package appeng.core.core.bootstrap;

import appeng.api.bootstrap.DefinitionFactory;
import appeng.api.definitions.IEntityDefinition;
import appeng.core.api.bootstrap.IEntityBuilder;
import appeng.core.lib.bootstrap.DefinitionBuilder;
import appeng.core.lib.definitions.EntityDefinition;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.EntityEntry;

public class EntityDefinitionBuilder<E extends EntityEntry> extends DefinitionBuilder<E, E, IEntityDefinition<E>, EntityDefinitionBuilder<E>> implements IEntityBuilder<E, EntityDefinitionBuilder<E>> {

	public EntityDefinitionBuilder(DefinitionFactory factory, ResourceLocation registryName, E entity){
		super(factory, registryName, entity);
	}

	@Override
	protected IEntityDefinition<E> def(E entity){
		return new EntityDefinition<E>(registryName, entity);
	}
}
