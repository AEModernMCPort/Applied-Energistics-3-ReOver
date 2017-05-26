
package appeng.core.worldgen.definitions;


import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.oredict.OreDictionary;

import appeng.api.definitions.IBlockDefinition;
import appeng.core.lib.bootstrap.FeatureFactory;
import appeng.core.lib.definitions.Definitions;
import appeng.core.lib.features.AEFeature;
import appeng.core.worldgen.api.definitions.IWorldGenBlockDefinitions;
import appeng.core.worldgen.block.BlockChargedQuartzOre;
import appeng.core.worldgen.block.BlockQuartzOre;
import appeng.core.worldgen.block.BlockSkyChest;
import appeng.core.worldgen.block.BlockSkyChest.SkyChestType;
import appeng.core.worldgen.block.BlockSkyCompass;
import appeng.core.worldgen.client.render.SkyChestRenderingCustomizer;
import appeng.miscellaneous.AppEngMiscellaneous;


public class WorldGenBlockDefinitions extends Definitions<Block, IBlockDefinition<Block>> implements IWorldGenBlockDefinitions
{
	
	public WorldGenBlockDefinitions( FeatureFactory registry )
	{
		init();
	}

}
