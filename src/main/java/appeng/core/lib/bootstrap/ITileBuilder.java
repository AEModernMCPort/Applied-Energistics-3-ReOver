package appeng.core.lib.bootstrap;

import appeng.api.definitions.ITileDefinition;
import net.minecraft.tileentity.TileEntity;

public interface ITileBuilder<T extends TileEntity, TT extends ITileBuilder<T, TT>>
		extends IDefinitionBuilder<Class<T>, ITileDefinition<T>, TT> {

}
