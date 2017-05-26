
package appeng.core.lib.bootstrap;


import net.minecraftforge.fml.common.registry.EntityEntry;

import appeng.api.definitions.IEntityDefinition;


public interface IEntityBuilder<E extends EntityEntry, EE extends IEntityBuilder<E, EE>> extends IDefinitionBuilder<E, IEntityDefinition<E>, EE>
{

}
