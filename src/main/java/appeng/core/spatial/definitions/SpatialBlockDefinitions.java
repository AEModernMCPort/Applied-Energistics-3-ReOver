package appeng.core.spatial.definitions;

import appeng.api.definitions.IBlockDefinition;
import appeng.core.lib.bootstrap_olde.FeatureFactory;
import appeng.core.lib.definitions.Definitions;
import appeng.core.spatial.api.definitions.ISpatialBlockDefinitions;
import net.minecraft.block.Block;

public class SpatialBlockDefinitions extends Definitions<Block, IBlockDefinition<Block>>
		implements ISpatialBlockDefinitions {

	public SpatialBlockDefinitions(FeatureFactory registry){
		init();
	}

}
