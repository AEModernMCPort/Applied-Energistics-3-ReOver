package appeng.core.lib.bootstrap_olde;

import appeng.api.bootstrap.DefinitionBuilder;
import appeng.api.definitions.ITileDefinition;
import net.minecraft.tileentity.TileEntity;

public interface ITileBuilder<T extends TileEntity, TT extends ITileBuilder<T, TT>>
		extends DefinitionBuilder<Class<T>, ITileDefinition<T>, TT> {

}
