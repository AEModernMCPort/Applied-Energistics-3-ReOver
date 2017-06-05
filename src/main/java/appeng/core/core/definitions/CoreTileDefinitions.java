package appeng.core.core.definitions;

import appeng.api.bootstrap.DefinitionFactory;
import appeng.api.definitions.ITileDefinition;
import appeng.core.api.definitions.ICoreTileDefinitions;
import appeng.core.lib.definitions.Definitions;
import net.minecraft.tileentity.TileEntity;

public class CoreTileDefinitions extends Definitions<Class<TileEntity>, ITileDefinition<TileEntity>> implements ICoreTileDefinitions {

	public CoreTileDefinitions(DefinitionFactory registry){

	}

	private DefinitionFactory.InputHandler<Class<TileEntity>, Class<TileEntity>> ih(Class<TileEntity> tile){
		return new DefinitionFactory.InputHandler<Class<TileEntity>, Class<TileEntity>>(tile) {};
	}

}
