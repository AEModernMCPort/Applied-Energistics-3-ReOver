package appeng.core.definitions;

import appeng.api.definitions.ITileDefinition;
import appeng.core.api.definitions.ICoreTileDefinitions;
import appeng.core.lib.bootstrap_olde.FeatureFactory;
import appeng.core.lib.definitions.Definitions;
import net.minecraft.tileentity.TileEntity;

public class CoreTileDefinitions extends Definitions<Class<TileEntity>, ITileDefinition<TileEntity>>
		implements ICoreTileDefinitions {

	public CoreTileDefinitions(FeatureFactory registry){
		init();
	}

}
