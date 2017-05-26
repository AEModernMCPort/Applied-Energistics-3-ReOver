
package appeng.core.crafting.definitions;


import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;

import appeng.api.definitions.ITileDefinition;
import appeng.core.AppEng;
import appeng.core.crafting.api.definitions.ICraftingTileDefinitions;
import appeng.core.crafting.tile.TileCraftingMonitorTile;
import appeng.core.crafting.tile.TileCraftingStorageTile;
import appeng.core.crafting.tile.TileCraftingTile;
import appeng.core.crafting.tile.TileMolecularAssembler;
import appeng.core.lib.bootstrap.FeatureFactory;
import appeng.core.lib.definitions.Definitions;
import appeng.core.lib.features.AEFeature;


public class CraftingTileDefinitions extends Definitions<Class<TileEntity>, ITileDefinition<TileEntity>> implements ICraftingTileDefinitions
{

	public CraftingTileDefinitions( FeatureFactory registry )
	{
		init();
	}

}
