package appeng.miscellaneous.definitions;

import appeng.api.definitions.IBlockDefinition;
import appeng.core.lib.bootstrap_olde.FeatureFactory;
import appeng.core.lib.definitions.Definitions;
import appeng.miscellaneous.api.definitions.IMiscellaneousBlockDefinitions;
import net.minecraft.block.Block;

public class MiscellaneousBlockDefinitions extends Definitions<Block, IBlockDefinition<Block>>
		implements IMiscellaneousBlockDefinitions {

	public MiscellaneousBlockDefinitions(FeatureFactory registry){
		init();
	}

}
