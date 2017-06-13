package appeng.core.crafting.definitions;

import appeng.api.bootstrap.DefinitionFactory;
import appeng.api.definitions.ITileDefinition;
import appeng.core.crafting.api.definitions.ICraftingTileDefinitions;
import appeng.core.lib.definitions.Definitions;
import net.minecraft.tileentity.TileEntity;

public class CraftingTileDefinitions extends Definitions<Class<TileEntity>, ITileDefinition<TileEntity>> implements ICraftingTileDefinitions {

	public CraftingTileDefinitions(DefinitionFactory registry){

	}

	private DefinitionFactory.InputHandler<Class<TileEntity>, Class<TileEntity>> ih(Class tile){
		return new DefinitionFactory.InputHandler<Class<TileEntity>, Class<TileEntity>>(tile) {};
	}

}
