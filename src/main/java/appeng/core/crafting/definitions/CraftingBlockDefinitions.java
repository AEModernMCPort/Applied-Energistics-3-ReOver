package appeng.core.crafting.definitions;

import appeng.api.definitions.IBlockDefinition;
import appeng.core.crafting.api.definitions.ICraftingBlockDefinitions;
import appeng.core.lib.definitions.Definitions;
import net.minecraft.block.Block;

public class CraftingBlockDefinitions extends Definitions<Block, IBlockDefinition<Block>>
		implements ICraftingBlockDefinitions {

	public CraftingBlockDefinitions(FeatureFactory registry){
		init();
	}

}
