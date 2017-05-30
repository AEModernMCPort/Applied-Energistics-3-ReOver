package appeng.core.spatial.definitions;

import appeng.api.definitions.ITileDefinition;
import appeng.core.lib.definitions.Definitions;
import appeng.core.spatial.api.definitions.ISpatialTileDefinitions;
import net.minecraft.tileentity.TileEntity;

public class SpatialTileDefinitions extends Definitions<Class<TileEntity>, ITileDefinition<TileEntity>>
		implements ISpatialTileDefinitions {

	public SpatialTileDefinitions(FeatureFactory registry){
		init();
	}

}
