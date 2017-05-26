
package appeng.core.lib.bootstrap;


import net.minecraft.tileentity.TileEntity;

import appeng.api.definitions.ITileDefinition;


public interface ITileBuilder<T extends TileEntity, TT extends ITileBuilder<T, TT>> extends IDefinitionBuilder<Class<T>, ITileDefinition<T>, TT>
{

}
