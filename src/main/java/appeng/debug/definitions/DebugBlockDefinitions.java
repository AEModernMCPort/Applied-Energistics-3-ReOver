package appeng.debug.definitions;

import appeng.api.bootstrap.DefinitionFactory;
import appeng.core.core.api.definition.IBlockDefinition;
import appeng.core.AppEng;
import appeng.core.core.api.bootstrap.IBlockBuilder;
import appeng.core.lib.definitions.Definitions;
import appeng.debug.block.TestBlock;
import net.minecraft.block.Block;
import net.minecraft.util.ResourceLocation;

public class DebugBlockDefinitions extends Definitions<Block, IBlockDefinition<Block>> {

	public DebugBlockDefinitions(DefinitionFactory registry){
		registry.<Block, IBlockDefinition<Block>, IBlockBuilder<Block, ?>, Block>definitionBuilder(new ResourceLocation(AppEng.MODID, "test_block"), ih(new TestBlock())).createDefaultItem().build();
	}

	private DefinitionFactory.InputHandler<Block, Block> ih(Block block){
		return new DefinitionFactory.InputHandler<Block, Block>(block) {};
	}

}
