package appeng.core.lib.bootstrap_olde;

import appeng.api.bootstrap.DefinitionBuilder;
import appeng.api.definitions.IEntityDefinition;
import net.minecraftforge.fml.common.registry.EntityEntry;

public interface IEntityBuilder<E extends EntityEntry, EE extends IEntityBuilder<E, EE>>
		extends DefinitionBuilder<E, IEntityDefinition<E>, EE> {

}
