package appeng.core.api.bootstrap;

import appeng.api.bootstrap.IDefinitionBuilder;
import appeng.api.definitions.ITileDefinition;
import appeng.api.entry.TileRegistryEntry;
import net.minecraft.tileentity.TileEntity;

public interface ITileBuilder<T extends TileEntity, TT extends ITileBuilder<T, TT>> extends IDefinitionBuilder<TileRegistryEntry<T>, ITileDefinition<T>, TT> {

}
