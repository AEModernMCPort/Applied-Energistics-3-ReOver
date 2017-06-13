package appeng.core.spatial.definitions;

import appeng.api.bootstrap.DefinitionFactory;
import appeng.api.definitions.ITileDefinition;
import appeng.api.entry.TileRegistryEntry;
import appeng.core.lib.definitions.Definitions;
import appeng.core.spatial.api.definitions.ISpatialTileDefinitions;
import net.minecraft.tileentity.TileEntity;

public class SpatialTileDefinitions extends Definitions<TileRegistryEntry<TileEntity>, ITileDefinition<TileEntity>> implements ISpatialTileDefinitions {

	public SpatialTileDefinitions(DefinitionFactory registry){

	}

	private DefinitionFactory.InputHandler<TileRegistryEntry<TileEntity>, Class<TileEntity>> ih(Class tile){
		return new DefinitionFactory.InputHandler<TileRegistryEntry<TileEntity>, Class<TileEntity>>(tile) {};
	}

}
