
package appeng.core.me.definitions;


import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;

import appeng.api.definitions.IItemDefinition;
import appeng.core.AppEng;
import appeng.core.api.material.Material;
import appeng.core.lib.bootstrap.FeatureFactory;
import appeng.core.lib.definitions.Definitions;
import appeng.core.lib.features.AEFeature;
import appeng.core.me.AppEngME;
import appeng.core.me.api.definitions.IMEItemDefinitions;
import appeng.core.me.item.ItemBasicStorageCell;
import appeng.core.me.item.ItemCard;
import appeng.core.me.item.ItemCard.EnumCardType;
import appeng.core.me.item.ItemCreativeStorageCell;
import appeng.core.me.item.ItemProcessor;
import appeng.core.me.item.ItemProcessor.ProcessorType;
import appeng.core.me.item.ItemSingularity;
import appeng.core.me.item.ItemViewCell;
import appeng.core.me.item.ItemWirelessBooster;
import appeng.core.me.item.ToolBiometricCard;
import appeng.core.me.item.ToolMemoryCard;
import appeng.core.me.item.ToolNetworkTool;
import appeng.core.me.item.ToolPortableCell;
import appeng.core.me.item.ToolWirelessTerminal;
import appeng.tools.AppEngTools;


public class MEItemDefinitions extends Definitions<Item, IItemDefinition<Item>> implements IMEItemDefinitions
{

	public MEItemDefinitions( FeatureFactory registry )
	{
		init( registry.buildDefaultItemBlocks() );
	}

}
