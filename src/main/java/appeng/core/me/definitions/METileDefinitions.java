
package appeng.core.me.definitions;


import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;

import appeng.api.definitions.ITileDefinition;
import appeng.core.AppEng;
import appeng.core.lib.bootstrap.FeatureFactory;
import appeng.core.lib.definitions.Definitions;
import appeng.core.lib.features.AEFeature;
import appeng.core.me.api.definitions.IMETileDefinitions;
import appeng.core.me.tile.TileCableBus;
import appeng.core.me.tile.TileCellWorkbench;
import appeng.core.me.tile.TileChest;
import appeng.core.me.tile.TileCondenser;
import appeng.core.me.tile.TileController;
import appeng.core.me.tile.TileDrive;
import appeng.core.me.tile.TileIOPort;
import appeng.core.me.tile.TileInscriber;
import appeng.core.me.tile.TileInterface;
import appeng.core.me.tile.TileQuantumBridge;
import appeng.core.me.tile.TileSecurity;
import appeng.core.me.tile.TileWireless;


public class METileDefinitions extends Definitions<Class<TileEntity>, ITileDefinition<TileEntity>> implements IMETileDefinitions
{

	public METileDefinitions( FeatureFactory registry )
	{
		init();
	}

}
