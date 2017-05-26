
package appeng.core.crafting.definitions;


import net.minecraft.block.Block;
import net.minecraft.util.ResourceLocation;

import appeng.api.definitions.IBlockDefinition;
import appeng.core.AppEng;
import appeng.core.crafting.api.definitions.ICraftingBlockDefinitions;
import appeng.core.crafting.block.BlockCraftingMonitor;
import appeng.core.crafting.block.BlockCraftingStorage;
import appeng.core.crafting.block.BlockCraftingUnit;
import appeng.core.crafting.block.BlockMolecularAssembler;
import appeng.core.crafting.item.ItemCraftingStorage;
import appeng.core.lib.bootstrap.FeatureFactory;
import appeng.core.lib.definitions.Definitions;
import appeng.core.lib.features.AEFeature;


public class CraftingBlockDefinitions extends Definitions<Block, IBlockDefinition<Block>> implements ICraftingBlockDefinitions
{

	public CraftingBlockDefinitions( FeatureFactory registry )
	{
		init();
	}

}
