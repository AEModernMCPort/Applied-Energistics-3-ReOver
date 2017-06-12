package appeng.core.core.definitions;

import appeng.api.bootstrap.DefinitionFactory;
import appeng.api.bootstrap.IDefinitionBuilder;
import appeng.api.definitions.IBlockDefinition;
import appeng.core.AppEng;
import appeng.core.api.definitions.ICoreBlockDefinitions;
import appeng.core.core.bootstrap.BlockColorComponent;
import appeng.core.item.DummyBlock;
import appeng.core.lib.definitions.Definitions;
import net.minecraft.block.Block;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;

import java.util.Optional;

public class CoreBlockDefinitions extends Definitions<Block, IBlockDefinition<Block>> implements ICoreBlockDefinitions {

	public CoreBlockDefinitions(DefinitionFactory registry){
		IDefinitionBuilder builder = registry.definitionBuilder(new ResourceLocation(AppEng.MODID,"component_test_block"), ih(new DummyBlock()));
		builder.initializationComponent(Side.CLIENT, new BlockColorComponent(Optional.of((state, world, pos, anInt) -> 0)));
		builder.build();
	}

	private DefinitionFactory.InputHandler<Block, Block> ih(Block block){
		return new DefinitionFactory.InputHandler<Block, Block>(block) {};
	}

}
