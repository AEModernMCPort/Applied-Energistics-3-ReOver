package appeng.decorative.definitions;

import appeng.api.bootstrap.DefinitionFactory;
import appeng.api.definitions.IBlockDefinition;
import appeng.core.lib.definitions.Definitions;
import appeng.decorative.api.definitions.IDecorativeBlockDefinitions;
import net.minecraft.block.Block;

public class DecorativeBlockDefinitions extends Definitions<Block, IBlockDefinition<Block>> implements IDecorativeBlockDefinitions {

	public DecorativeBlockDefinitions(DefinitionFactory registry){
		init();
	}

}
