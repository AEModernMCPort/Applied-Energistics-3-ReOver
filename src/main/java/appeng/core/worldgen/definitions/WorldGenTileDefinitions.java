package appeng.core.worldgen.definitions;

import appeng.api.definitions.ITileDefinition;
import appeng.core.lib.bootstrap_olde.FeatureFactory;
import appeng.core.lib.definitions.Definitions;
import appeng.core.worldgen.api.definitions.IWorldGenTileDefinitions;
import net.minecraft.tileentity.TileEntity;

public class WorldGenTileDefinitions extends Definitions<Class<TileEntity>, ITileDefinition<TileEntity>>
		implements IWorldGenTileDefinitions {

	public WorldGenTileDefinitions(FeatureFactory registry){
		init();
	}

}
