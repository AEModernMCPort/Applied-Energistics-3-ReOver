package appeng.core.core.definitions;

import appeng.api.bootstrap.DefinitionFactory;
import appeng.api.definitions.IEntityDefinition;
import appeng.core.api.definitions.ICoreEntityDefinitions;
import appeng.core.lib.definitions.Definitions;
import net.minecraftforge.fml.common.registry.EntityEntry;

public class CoreEntityDefinitions extends Definitions<EntityEntry, IEntityDefinition<EntityEntry>>
		implements ICoreEntityDefinitions {

	public CoreEntityDefinitions(DefinitionFactory factory){
		init();
	}

}
