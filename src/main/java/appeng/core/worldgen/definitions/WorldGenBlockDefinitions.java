package appeng.core.worldgen.definitions;

import appeng.api.bootstrap.DefinitionFactory;
import appeng.api.definitions.IBlockDefinition;
import appeng.core.lib.definitions.Definitions;
import appeng.core.worldgen.api.definitions.IWorldGenBlockDefinitions;
import net.minecraft.block.Block;

public class WorldGenBlockDefinitions extends Definitions<Block, IBlockDefinition<Block>> implements IWorldGenBlockDefinitions {

	public WorldGenBlockDefinitions(DefinitionFactory registry){
		init();
	}

}
