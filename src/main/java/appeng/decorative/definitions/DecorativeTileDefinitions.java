package appeng.decorative.definitions;

import appeng.api.bootstrap.DefinitionFactory;
import appeng.api.definitions.ITileDefinition;
import appeng.core.lib.definitions.Definitions;
import appeng.decorative.api.definitions.IDecorativeTileDefinitions;
import net.minecraft.tileentity.TileEntity;

public class DecorativeTileDefinitions extends Definitions<Class<TileEntity>, ITileDefinition<TileEntity>> implements IDecorativeTileDefinitions {

	public DecorativeTileDefinitions(DefinitionFactory registry){

	}

	private DefinitionFactory.InputHandler<Class<TileEntity>, Class<TileEntity>> ih(Class tile){
		return new DefinitionFactory.InputHandler<Class<TileEntity>, Class<TileEntity>>(tile) {};
	}

}
