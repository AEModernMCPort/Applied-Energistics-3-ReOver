
package appeng.debug.definitions;


import net.minecraft.block.Block;
import net.minecraft.util.ResourceLocation;

import appeng.api.definitions.IBlockDefinition;
import appeng.core.lib.bootstrap.FeatureFactory;
import appeng.core.lib.definitions.Definitions;
import appeng.core.lib.features.AEFeature;
import appeng.debug.AppEngDebug;

public class DebugBlockDefinitions extends Definitions<Block, IBlockDefinition<Block>>
{

	public DebugBlockDefinitions( FeatureFactory registry )
	{
		init();
	}

}
