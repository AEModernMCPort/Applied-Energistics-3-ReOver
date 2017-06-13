package appeng.miscellaneous.definitions;

import appeng.api.bootstrap.DefinitionFactory;
import appeng.api.definitions.ITileDefinition;
import appeng.api.entry.TileRegistryEntry;
import appeng.core.lib.definitions.Definitions;
import appeng.miscellaneous.api.definitions.IMiscellaneousTileDefinitions;
import net.minecraft.tileentity.TileEntity;

public class MiscellaneousTileDefinitions extends Definitions<TileRegistryEntry<TileEntity>, ITileDefinition<TileEntity>> implements IMiscellaneousTileDefinitions {

	public MiscellaneousTileDefinitions(DefinitionFactory registry){

	}

	private DefinitionFactory.InputHandler<Class<TileEntity>, TileRegistryEntry<TileEntity>> ih(TileRegistryEntry tile){
		return new DefinitionFactory.InputHandler<Class<TileEntity>, TileRegistryEntry<TileEntity>>(tile) {};
	}

}
