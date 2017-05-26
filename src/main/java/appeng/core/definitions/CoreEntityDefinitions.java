package appeng.core.definitions;

import appeng.api.definitions.IEntityDefinition;
import appeng.core.api.definitions.ICoreEntityDefinitions;
import appeng.core.lib.bootstrap.FeatureFactory;
import appeng.core.lib.definitions.Definitions;
import net.minecraftforge.fml.common.registry.EntityEntry;

public class CoreEntityDefinitions extends Definitions<EntityEntry, IEntityDefinition<EntityEntry>>
		implements ICoreEntityDefinitions {

	public CoreEntityDefinitions(FeatureFactory factory){
		init();
	}

}
