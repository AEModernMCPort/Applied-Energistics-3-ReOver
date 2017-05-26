package appeng.debug.definitions;

import appeng.api.definitions.ITileDefinition;
import appeng.core.lib.bootstrap.FeatureFactory;
import appeng.core.lib.definitions.Definitions;
import net.minecraft.tileentity.TileEntity;

public class DebugTileDefinitions extends Definitions<Class<TileEntity>, ITileDefinition<TileEntity>> {

	public DebugTileDefinitions(FeatureFactory registry){
		init();
	}

}
