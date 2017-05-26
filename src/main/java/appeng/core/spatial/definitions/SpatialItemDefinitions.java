
package appeng.core.spatial.definitions;


import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;

import appeng.api.definitions.IItemDefinition;
import appeng.core.AppEng;
import appeng.core.lib.bootstrap.FeatureFactory;
import appeng.core.lib.definitions.Definitions;
import appeng.core.lib.features.AEFeature;
import appeng.core.spatial.api.definitions.ISpatialItemDefinitions;
import appeng.core.spatial.item.ItemSpatialStorageCell;


public class SpatialItemDefinitions extends Definitions<Item, IItemDefinition<Item>> implements ISpatialItemDefinitions
{
	
	public SpatialItemDefinitions( FeatureFactory registry )
	{
		init( registry.buildDefaultItemBlocks() );
	}

}
