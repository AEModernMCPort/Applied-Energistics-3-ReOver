package appeng.core.core.definitions;

import appeng.api.bootstrap.DefinitionFactory;
import appeng.api.definitions.ITileDefinition;
import appeng.api.entry.TileRegistryEntry;
import appeng.core.api.definitions.ICoreTileDefinitions;
import appeng.core.lib.definitions.Definitions;
import net.minecraft.tileentity.TileEntity;

public class CoreTileDefinitions extends Definitions<TileRegistryEntry<TileEntity>, ITileDefinition<TileEntity>> implements ICoreTileDefinitions {

	public CoreTileDefinitions(DefinitionFactory registry){

	}

	private DefinitionFactory.InputHandler<Class<TileEntity>, TileRegistryEntry<TileEntity>> ih(TileRegistryEntry tile){
		return new DefinitionFactory.InputHandler<Class<TileEntity>, TileRegistryEntry<TileEntity>>(tile) {};
	}

}
