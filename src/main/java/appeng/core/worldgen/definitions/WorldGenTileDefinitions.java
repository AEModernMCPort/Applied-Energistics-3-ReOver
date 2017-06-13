package appeng.core.worldgen.definitions;

import appeng.api.bootstrap.DefinitionFactory;
import appeng.api.definitions.ITileDefinition;
import appeng.core.lib.definitions.Definitions;
import appeng.core.worldgen.api.definitions.IWorldGenTileDefinitions;
import net.minecraft.tileentity.TileEntity;

public class WorldGenTileDefinitions extends Definitions<Class<TileEntity>, ITileDefinition<TileEntity>> implements IWorldGenTileDefinitions {

	public WorldGenTileDefinitions(DefinitionFactory registry){

	}

	private DefinitionFactory.InputHandler<Class<TileEntity>, Class<TileEntity>> ih(Class tile){
		return new DefinitionFactory.InputHandler<Class<TileEntity>, Class<TileEntity>>(tile) {};
	}

}
