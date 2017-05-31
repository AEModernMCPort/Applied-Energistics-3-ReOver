package appeng.core.api.bootstrap;

import appeng.api.bootstrap.IDefinitionBuilder;
import appeng.api.definitions.IDimensionTypeDefinition;
import net.minecraft.world.DimensionType;

public interface IDimensionTypeBuilder<D extends DimensionType, DD extends IDimensionTypeBuilder<D, DD>> extends IDefinitionBuilder<D, IDimensionTypeDefinition<D>, DD> {

}
