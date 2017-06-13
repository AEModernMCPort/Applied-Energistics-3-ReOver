package appeng.core.worldgen.definitions;

import appeng.api.bootstrap.DefinitionFactory;
import appeng.api.definitions.ITileDefinition;
import appeng.api.entry.TileRegistryEntry;
import appeng.core.lib.definitions.Definitions;
import appeng.core.worldgen.api.definitions.IWorldGenTileDefinitions;
import net.minecraft.tileentity.TileEntity;

public class WorldGenTileDefinitions extends Definitions<TileRegistryEntry<TileEntity>, ITileDefinition<TileEntity>> implements IWorldGenTileDefinitions {

	public WorldGenTileDefinitions(DefinitionFactory registry){

	}

	private DefinitionFactory.InputHandler<TileRegistryEntry<TileEntity>, Class<TileEntity>> ih(Class tile){
		return new DefinitionFactory.InputHandler<TileRegistryEntry<TileEntity>, Class<TileEntity>>(tile) {};
	}

}
