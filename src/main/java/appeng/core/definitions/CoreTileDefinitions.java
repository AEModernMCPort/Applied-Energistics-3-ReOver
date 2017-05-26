
package appeng.core.definitions;


import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;

import appeng.api.definitions.ITileDefinition;
import appeng.core.AppEng;
import appeng.core.api.definitions.ICoreTileDefinitions;
import appeng.core.lib.bootstrap.FeatureFactory;
import appeng.core.lib.definitions.Definitions;
import appeng.core.lib.features.AEFeature;
import appeng.core.tile.TileCharger;
import appeng.core.tile.TileCrank;
import appeng.core.tile.TileCreativeEnergyCell;
import appeng.core.tile.TileCrystalGrowthAccelerator;
import appeng.core.tile.TileDenseEnergyCell;
import appeng.core.tile.TileEnergyAcceptor;
import appeng.core.tile.TileEnergyCell;
import appeng.core.tile.TileGrinder;
import appeng.core.tile.TileVibrationChamber;
import appeng.miscellaneous.AppEngMiscellaneous;


public class CoreTileDefinitions extends Definitions<Class<TileEntity>, ITileDefinition<TileEntity>> implements ICoreTileDefinitions
{

	public CoreTileDefinitions( FeatureFactory registry )
	{
		init();
	}

}
