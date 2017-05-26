
package appeng.core.lib.bootstrap;


import net.minecraft.world.DimensionType;

import appeng.api.definitions.IDimensionTypeDefinition;


public interface IDimensionTypeBuilder<D extends DimensionType, DD extends IDimensionTypeBuilder<D, DD>> extends IDefinitionBuilder<D, IDimensionTypeDefinition<D>, DD>
{

}
