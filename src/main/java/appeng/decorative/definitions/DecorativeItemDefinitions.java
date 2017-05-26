
package appeng.decorative.definitions;


import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;

import appeng.api.definitions.IItemDefinition;
import appeng.core.AppEng;
import appeng.core.lib.bootstrap.FeatureFactory;
import appeng.core.lib.definitions.Definitions;
import appeng.core.lib.features.AEFeature;
import appeng.decorative.api.definitions.IDecorativeItemDefinitions;
import appeng.decorative.item.ItemPaintBall;
import appeng.decorative.item.ItemPaintBallRendering;
import appeng.decorative.item.ToolColorApplicator;
import appeng.tools.AppEngTools;


public class DecorativeItemDefinitions extends Definitions<Item, IItemDefinition<Item>> implements IDecorativeItemDefinitions
{

	public DecorativeItemDefinitions( FeatureFactory registry )
	{
		init( registry.buildDefaultItemBlocks() );
	}

}
