
package appeng.core.spatial.definitions;


import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;

import appeng.api.definitions.ITileDefinition;
import appeng.core.AppEng;
import appeng.core.lib.bootstrap.FeatureFactory;
import appeng.core.lib.definitions.Definitions;
import appeng.core.lib.features.AEFeature;
import appeng.core.spatial.api.definitions.ISpatialTileDefinitions;
import appeng.core.spatial.tile.TileSpatialIOPort;
import appeng.core.spatial.tile.TileSpatialPylon;


public class SpatialTileDefinitions extends Definitions<Class<TileEntity>, ITileDefinition<TileEntity>> implements ISpatialTileDefinitions
{


	public SpatialTileDefinitions( FeatureFactory registry )
	{
		init();
	}

}
