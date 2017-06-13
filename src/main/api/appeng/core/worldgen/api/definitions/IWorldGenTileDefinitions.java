package appeng.core.worldgen.api.definitions;

import appeng.api.definitions.IDefinitions;
import appeng.api.definitions.ITileDefinition;
import appeng.api.entry.TileRegistryEntry;
import net.minecraft.tileentity.TileEntity;

public interface IWorldGenTileDefinitions extends IDefinitions<TileRegistryEntry<TileEntity>, ITileDefinition<TileEntity>> {

}
