package appeng.core.spatial.definitions;

import appeng.api.bootstrap.DefinitionFactory;
import appeng.core.core.api.definition.IBlockDefinition;
import appeng.core.lib.definitions.Definitions;
import appeng.core.spatial.api.definitions.ISpatialBlockDefinitions;
import net.minecraft.block.Block;

public class SpatialBlockDefinitions extends Definitions<Block, IBlockDefinition<Block>> implements ISpatialBlockDefinitions {

	public SpatialBlockDefinitions(DefinitionFactory registry){

	}

	private DefinitionFactory.InputHandler<Block, Block> ih(Block block){
		return new DefinitionFactory.InputHandler<Block, Block>(block) {};
	}

}
