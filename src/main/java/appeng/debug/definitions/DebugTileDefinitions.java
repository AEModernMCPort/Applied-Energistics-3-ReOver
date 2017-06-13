package appeng.debug.definitions;

import appeng.api.bootstrap.DefinitionFactory;
import appeng.api.definitions.ITileDefinition;
import appeng.core.lib.definitions.Definitions;
import net.minecraft.tileentity.TileEntity;

public class DebugTileDefinitions extends Definitions<Class<TileEntity>, ITileDefinition<TileEntity>> {

	public DebugTileDefinitions(DefinitionFactory registry){

	}

	private DefinitionFactory.InputHandler<Class<TileEntity>, Class<TileEntity>> ih(Class tile){
		return new DefinitionFactory.InputHandler<Class<TileEntity>, Class<TileEntity>>(tile) {};
	}

}
