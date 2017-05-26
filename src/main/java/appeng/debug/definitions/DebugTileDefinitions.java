
package appeng.debug.definitions;


import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;

import appeng.api.definitions.ITileDefinition;
import appeng.core.lib.bootstrap.FeatureFactory;
import appeng.core.lib.definitions.Definitions;
import appeng.core.lib.features.AEFeature;
import appeng.debug.AppEngDebug;

public class DebugTileDefinitions extends Definitions<Class<TileEntity>, ITileDefinition<TileEntity>>
{

	public DebugTileDefinitions( FeatureFactory registry )
	{
		init();
	}

}
