package appeng.core.me.definitions;

import appeng.api.definitions.IBlockDefinition;
import appeng.core.lib.bootstrap.FeatureFactory;
import appeng.core.lib.definitions.Definitions;
import appeng.core.me.api.definitions.IMEBlockDefinitions;
import net.minecraft.block.Block;

public class MEBlockDefinitions extends Definitions<Block, IBlockDefinition<Block>> implements IMEBlockDefinitions {

	public MEBlockDefinitions(FeatureFactory registry){
		init();
	}

}
