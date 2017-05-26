
package appeng.core.definitions;


import net.minecraft.block.Block;
import net.minecraft.block.BlockDispenser;
import net.minecraft.util.ResourceLocation;

import appeng.api.definitions.IBlockDefinition;
import appeng.core.AppEng;
import appeng.core.api.definitions.ICoreBlockDefinitions;
import appeng.core.block.BlockCharger;
import appeng.core.block.BlockCrank;
import appeng.core.block.BlockCreativeEnergyCell;
import appeng.core.block.BlockCrystalGrowthAccelerator;
import appeng.core.block.BlockDenseEnergyCell;
import appeng.core.block.BlockEnergyAcceptor;
import appeng.core.block.BlockEnergyCell;
import appeng.core.block.BlockEnergyCellRendering;
import appeng.core.block.BlockGrinder;
import appeng.core.block.BlockQuartzGlass;
import appeng.core.block.BlockSkyStone;
import appeng.core.block.BlockSkyStone.SkystoneType;
import appeng.core.block.BlockTinyTNT;
import appeng.core.block.BlockVibrantQuartzGlass;
import appeng.core.block.BlockVibrationChamber;
import appeng.core.lib.bootstrap.BlockRenderingCustomizer;
import appeng.core.lib.bootstrap.FeatureFactory;
import appeng.core.lib.bootstrap.IBlockRendering;
import appeng.core.lib.bootstrap.IItemRendering;
import appeng.core.lib.definitions.Definitions;
import appeng.core.lib.features.AEFeature;
import appeng.core.lib.item.AEBaseItemBlockChargeable;
import appeng.decorative.AppEngDecorative;
import appeng.miscellaneous.AppEngMiscellaneous;
import appeng.tools.hooks.DispenserBehaviorTinyTNT;


public class CoreBlockDefinitions extends Definitions<Block, IBlockDefinition<Block>> implements ICoreBlockDefinitions
{

	public CoreBlockDefinitions( FeatureFactory registry )
	{
		init();
	}

}
