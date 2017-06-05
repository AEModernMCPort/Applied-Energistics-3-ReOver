package appeng.core.core.definitions;

import appeng.api.bootstrap.DefinitionFactory;
import appeng.api.definitions.IBlockDefinition;
import appeng.core.AppEng;
import appeng.core.api.bootstrap.IBlockBuilder;
import appeng.core.api.definitions.ICoreBlockDefinitions;
import appeng.core.lib.definitions.Definitions;
import net.minecraft.block.Block;
import net.minecraft.block.BlockGravel;
import net.minecraft.util.ResourceLocation;

public class CoreBlockDefinitions extends Definitions<Block, IBlockDefinition<Block>> implements ICoreBlockDefinitions {

	public CoreBlockDefinitions(DefinitionFactory registry){
		registry.<Block, IBlockDefinition, IBlockBuilder<Block, ?>, Block>definitionBuilder(new ResourceLocation(AppEng.MODID, "testgravel"), ih(new BlockGravel())).createDefaultItemBlock().build();
	}

	private DefinitionFactory.InputHandler<Block, Block> ih(Block block){
		return new DefinitionFactory.InputHandler<Block, Block>(block) {};
	}

}
