package appeng.miscellaneous.definitions;

import appeng.api.bootstrap.DefinitionFactory;
import appeng.api.definitions.ITileDefinition;
import appeng.core.lib.definitions.Definitions;
import appeng.miscellaneous.api.definitions.IMiscellaneousTileDefinitions;
import net.minecraft.tileentity.TileEntity;

public class MiscellaneousTileDefinitions extends Definitions<Class<TileEntity>, ITileDefinition<TileEntity>> implements IMiscellaneousTileDefinitions {

	public MiscellaneousTileDefinitions(DefinitionFactory registry){
		init();
	}

}
