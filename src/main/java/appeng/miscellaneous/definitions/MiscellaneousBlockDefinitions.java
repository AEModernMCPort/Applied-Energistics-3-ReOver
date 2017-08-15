package appeng.miscellaneous.definitions;

import appeng.api.bootstrap.DefinitionFactory;
import appeng.core.core.api.definition.IBlockDefinition;
import appeng.core.lib.definitions.Definitions;
import appeng.miscellaneous.api.definitions.IMiscellaneousBlockDefinitions;
import net.minecraft.block.Block;

public class MiscellaneousBlockDefinitions extends Definitions<Block, IBlockDefinition<Block>> implements IMiscellaneousBlockDefinitions {

	public MiscellaneousBlockDefinitions(DefinitionFactory registry){

	}

	private DefinitionFactory.InputHandler<Block, Block> ih(Block block){
		return new DefinitionFactory.InputHandler<Block, Block>(block) {};
	}

}
