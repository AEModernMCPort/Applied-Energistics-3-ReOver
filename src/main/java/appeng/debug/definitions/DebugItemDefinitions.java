
package appeng.debug.definitions;


import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;

import appeng.api.definitions.IItemDefinition;
import appeng.core.lib.bootstrap.FeatureFactory;
import appeng.core.lib.definitions.Definitions;
import appeng.core.lib.features.AEFeature;
import appeng.debug.AppEngDebug;

public class DebugItemDefinitions extends Definitions<Item, IItemDefinition<Item>>
{

	public DebugItemDefinitions( FeatureFactory registry )
	{
		init( registry.buildDefaultItemBlocks() );
	}

}
