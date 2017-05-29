package appeng.core.lib.bootstrap_olde;

import appeng.api.bootstrap.DefinitionBuilder;
import appeng.api.definitions.IDimensionTypeDefinition;
import net.minecraft.world.DimensionType;

public interface IDimensionTypeBuilder<D extends DimensionType, DD extends IDimensionTypeBuilder<D, DD>>
		extends DefinitionBuilder<D, IDimensionTypeDefinition<D>, DD> {

}
