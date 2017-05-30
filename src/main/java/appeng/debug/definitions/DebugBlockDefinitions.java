package appeng.debug.definitions;

import appeng.api.definitions.IBlockDefinition;
import appeng.core.lib.definitions.Definitions;
import net.minecraft.block.Block;

public class DebugBlockDefinitions extends Definitions<Block, IBlockDefinition<Block>> {

	public DebugBlockDefinitions(FeatureFactory registry){
		init();
	}

}
