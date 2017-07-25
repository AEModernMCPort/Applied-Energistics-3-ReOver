package appeng.core.me.definitions;

import appeng.api.bootstrap.DefinitionFactory;
import appeng.core.core.api.definition.IEntityDefinition;
import appeng.core.lib.definitions.Definitions;
import appeng.core.me.api.definitions.IMEEntityDefinitions;
import net.minecraftforge.fml.common.registry.EntityEntry;

public class MEEntityDefinitions extends Definitions<EntityEntry, IEntityDefinition<EntityEntry>> implements IMEEntityDefinitions {

	public MEEntityDefinitions(DefinitionFactory factory){

	}

	private DefinitionFactory.InputHandler<EntityEntry, EntityEntry> ih(EntityEntry entity){
		return new DefinitionFactory.InputHandler<EntityEntry, EntityEntry>(entity) {};
	}

}
