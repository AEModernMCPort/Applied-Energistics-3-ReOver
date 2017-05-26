
package appeng.decorative.definitions;


import net.minecraft.block.Block;
import net.minecraft.util.ResourceLocation;

import appeng.api.definitions.IBlockDefinition;
import appeng.core.AppEngCore;
import appeng.core.block.BlockSkyStone;
import appeng.core.block.BlockSkyStone.SkystoneType;
import appeng.core.definitions.CoreBlockDefinitions;
import appeng.core.lib.bootstrap.FeatureFactory;
import appeng.core.lib.definitions.Definitions;
import appeng.core.lib.features.AEFeature;
import appeng.core.worldgen.block.BlockQuartz;
import appeng.decorative.AppEngDecorative;
import appeng.decorative.api.definitions.IDecorativeBlockDefinitions;
import appeng.decorative.block.BlockChiseledQuartz;
import appeng.decorative.block.BlockFluix;
import appeng.decorative.block.BlockPaint;
import appeng.decorative.block.BlockQuartzFixture;
import appeng.decorative.block.BlockQuartzPillar;
import appeng.decorative.block.BlockStairCommon;
import appeng.miscellaneous.AppEngMiscellaneous;


public class DecorativeBlockDefinitions extends Definitions<Block, IBlockDefinition<Block>> implements IDecorativeBlockDefinitions
{

	public DecorativeBlockDefinitions( FeatureFactory registry )
	{
		init();
	}

}
