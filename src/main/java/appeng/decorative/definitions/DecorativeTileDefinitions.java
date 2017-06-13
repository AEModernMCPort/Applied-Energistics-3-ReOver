package appeng.decorative.definitions;

import appeng.api.bootstrap.DefinitionFactory;
import appeng.api.definitions.ITileDefinition;
import appeng.api.entry.TileRegistryEntry;
import appeng.core.lib.definitions.Definitions;
import appeng.decorative.api.definitions.IDecorativeTileDefinitions;
import net.minecraft.tileentity.TileEntity;

public class DecorativeTileDefinitions extends Definitions<TileRegistryEntry<TileEntity>, ITileDefinition<TileEntity>> implements IDecorativeTileDefinitions {

	public DecorativeTileDefinitions(DefinitionFactory registry){

	}

	private DefinitionFactory.InputHandler<Class<TileEntity>, TileRegistryEntry<TileEntity>> ih(TileRegistryEntry tile){
		return new DefinitionFactory.InputHandler<Class<TileEntity>, TileRegistryEntry<TileEntity>>(tile) {};
	}

}
