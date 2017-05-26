
package appeng.core.definitions;


import net.minecraftforge.fml.common.registry.EntityEntry;

import appeng.api.definitions.IEntityDefinition;
import appeng.core.api.definitions.ICoreEntityDefinitions;
import appeng.core.entity.EntityTinyTNTPrimed;
import appeng.core.lib.bootstrap.FeatureFactory;
import appeng.core.lib.definitions.Definitions;


public class CoreEntityDefinitions extends Definitions<EntityEntry, IEntityDefinition<EntityEntry>> implements ICoreEntityDefinitions
{


	public CoreEntityDefinitions( FeatureFactory factory )
	{
		init();
	}

}
