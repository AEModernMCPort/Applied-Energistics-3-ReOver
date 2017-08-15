package appeng.core.me.definitions;

import appeng.api.bootstrap.DefinitionFactory;
import appeng.core.core.api.definition.IBlockDefinition;
import appeng.core.lib.definitions.Definitions;
import appeng.core.me.api.definitions.IMEBlockDefinitions;
import net.minecraft.block.Block;

public class MEBlockDefinitions extends Definitions<Block, IBlockDefinition<Block>> implements IMEBlockDefinitions {

	public MEBlockDefinitions(DefinitionFactory registry){

	}

	private DefinitionFactory.InputHandler<Block, Block> ih(Block block){
		return new DefinitionFactory.InputHandler<Block, Block>(block) {};
	}

}
