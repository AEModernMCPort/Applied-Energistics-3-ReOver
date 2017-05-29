package appeng.core.lib.bootstrap_olde;

import appeng.api.bootstrap.IDefinitionBuilder;
import appeng.api.definitions.ITileDefinition;
import net.minecraft.tileentity.TileEntity;

public interface ITileBuilder<T extends TileEntity, TT extends ITileBuilder<T, TT>>
		extends IDefinitionBuilder<Class<T>, ITileDefinition<T>, TT> {

}
