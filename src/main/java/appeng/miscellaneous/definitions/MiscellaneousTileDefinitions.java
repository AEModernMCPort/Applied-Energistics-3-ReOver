package appeng.miscellaneous.definitions;

import appeng.api.definitions.ITileDefinition;
import appeng.core.lib.bootstrap_olde.FeatureFactory;
import appeng.core.lib.definitions.Definitions;
import appeng.miscellaneous.api.definitions.IMiscellaneousTileDefinitions;
import net.minecraft.tileentity.TileEntity;

public class MiscellaneousTileDefinitions extends Definitions<Class<TileEntity>, ITileDefinition<TileEntity>>
		implements IMiscellaneousTileDefinitions {

	public MiscellaneousTileDefinitions(FeatureFactory registry){
		init();
	}

}
