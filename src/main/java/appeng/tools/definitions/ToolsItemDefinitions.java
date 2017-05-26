
package appeng.tools.definitions;


import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;

import appeng.api.definitions.IItemDefinition;
import appeng.core.lib.bootstrap.FeatureFactory;
import appeng.core.lib.definitions.Definitions;
import appeng.core.lib.features.AEFeature;
import appeng.tools.AppEngTools;
import appeng.tools.api.definitions.IToolsItemDefinitions;
import appeng.tools.item.ToolChargedStaff;
import appeng.tools.item.ToolEntropyManipulator;
import appeng.tools.item.ToolMatterCannon;
import appeng.tools.item.ToolQuartzAxe;
import appeng.tools.item.ToolQuartzHoe;
import appeng.tools.item.ToolQuartzPickaxe;
import appeng.tools.item.ToolQuartzSpade;
import appeng.tools.item.ToolQuartzSword;


public class ToolsItemDefinitions extends Definitions<Item, IItemDefinition<Item>> implements IToolsItemDefinitions
{

	public ToolsItemDefinitions( FeatureFactory registry )
	{
		init( registry.buildDefaultItemBlocks() ); //Just in case
	}

}
