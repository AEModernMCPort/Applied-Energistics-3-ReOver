package appeng.core.core.bootstrap;

import appeng.api.bootstrap.DefinitionFactory;
import appeng.core.core.api.definition.IEntityDefinition;
import appeng.core.core.api.bootstrap.IEntityBuilder;
import appeng.core.lib.bootstrap.DefinitionBuilder;
import appeng.core.core.definition.EntityDefinition;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.EntityEntry;

public class EntityDefinitionBuilder<E extends EntityEntry> extends DefinitionBuilder<E, E, IEntityDefinition<E>, EntityDefinitionBuilder<E>> implements IEntityBuilder<E, EntityDefinitionBuilder<E>> {

	public EntityDefinitionBuilder(DefinitionFactory factory, ResourceLocation registryName, E entity){
		super(factory, registryName, entity, "entity");
	}

	@Override
	protected IEntityDefinition<E> def(E entity){
		if(entity == null) return new EntityDefinition<>(registryName, null);

		return new EntityDefinition<>(registryName, entity);
	}
}
