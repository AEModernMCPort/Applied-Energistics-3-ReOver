package appeng.core.core.definitions;

import appeng.api.definitions.IBlockDefinition;
import appeng.core.api.definitions.ICoreBlockDefinitions;
import appeng.core.lib.bootstrap_olde.FeatureFactory;
import appeng.core.lib.definitions.Definitions;
import net.minecraft.block.Block;

public class CoreBlockDefinitions extends Definitions<Block, IBlockDefinition<Block>> implements ICoreBlockDefinitions {

	public CoreBlockDefinitions(FeatureFactory registry){
		init();
	}

}
