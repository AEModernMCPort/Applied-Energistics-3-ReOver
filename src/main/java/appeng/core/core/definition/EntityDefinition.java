package appeng.core.core.definition;

import appeng.core.core.api.definition.IEntityDefinition;
import appeng.core.lib.definition.Definition;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.EntityEntry;

public class EntityDefinition<E extends EntityEntry> extends Definition<E> implements IEntityDefinition<E> {

	public EntityDefinition(ResourceLocation identifier, E entity){
		super(identifier, entity);
	}

	@Override
	public boolean isSameAs(Object other){
		if(super.isSameAs(other)){
			return true;
		} else {
			if(isEnabled()){
				if(other instanceof Entity){
					return other.getClass() == maybe().get().getEntityClass();
				}
			}
			return false;
		}
	}

}
