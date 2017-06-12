package appeng.core.core.definitions;

import appeng.api.bootstrap.DefinitionFactory;
import appeng.api.definitions.IBlockDefinition;
import appeng.core.AppEng;
import appeng.core.api.definitions.ICoreBlockDefinitions;
import appeng.core.core.client.bootstrap.BlockColorComponent;
import appeng.core.core.client.bootstrap.StateMapperComponent;
import appeng.core.item.DummyBlock;
import appeng.core.lib.definitions.Definitions;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.block.statemap.DefaultStateMapper;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;

import java.util.Optional;

public class CoreBlockDefinitions extends Definitions<Block, IBlockDefinition<Block>> implements ICoreBlockDefinitions {

	public CoreBlockDefinitions(DefinitionFactory registry){
		registry.definitionBuilder(new ResourceLocation(AppEng.MODID, "component_test_block"), ih(new DummyBlock()))
				.initializationComponent(Side.CLIENT, new BlockColorComponent(() -> Optional.of((state, world, pos, anInt) -> 0)))
				.initializationComponent(Side.CLIENT, new StateMapperComponent(() -> Optional.of(new DefaultStateMapper())))
				.build();
	}

	private DefinitionFactory.InputHandler<Block, Block> ih(Block block){
		return new DefinitionFactory.InputHandler<Block, Block>(block) {};
	}
}
