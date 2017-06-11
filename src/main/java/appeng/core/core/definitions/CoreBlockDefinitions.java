package appeng.core.core.definitions;

import appeng.api.bootstrap.DefinitionFactory;
import appeng.api.bootstrap.IDefinitionBuilder;
import appeng.api.definitions.IBlockDefinition;
import appeng.core.AppEng;
import appeng.core.api.bootstrap.IBlockBuilder;
import appeng.core.api.definitions.ICoreBlockDefinitions;
import appeng.core.core.bootstrap.BlockColorComponent;
import appeng.core.item.DummyBlock;
import appeng.core.lib.definitions.Definitions;
import net.minecraft.block.Block;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;

public class CoreBlockDefinitions extends Definitions<Block, IBlockDefinition<Block>> implements ICoreBlockDefinitions {

	public CoreBlockDefinitions(DefinitionFactory registry){
		registry.<Block, IBlockDefinition<Block>, IBlockBuilder<Block, ?>, Block>definitionBuilder(new ResourceLocation(AppEng.MODID,"component_test_block"), ih(new DummyBlock()))
				.<IDefinitionBuilder.DefinitionInitializationComponent.Init<Block, IBlockDefinition<Block>>>initializationComponent(Side.CLIENT, def -> new BlockColorComponent(() -> (state, world, pos, anInt) -> 0).init(def))
				.build();
	}

	private DefinitionFactory.InputHandler<Block, Block> ih(Block block){
		return new DefinitionFactory.InputHandler<Block, Block>(block) {};
	}

}
