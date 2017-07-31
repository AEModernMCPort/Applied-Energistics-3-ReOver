package appeng.core.me.definitions;

import appeng.api.bootstrap.DefinitionFactory;
import appeng.core.AppEng;
import appeng.core.core.api.bootstrap.IBlockBuilder;
import appeng.core.core.api.definition.IBlockDefinition;
import appeng.core.core.client.bootstrap.ModelOverrideComponent;
import appeng.core.lib.definitions.Definitions;
import appeng.core.me.api.definitions.IMEBlockDefinitions;
import appeng.core.me.block.PartsContainerBlock;
import appeng.core.me.client.model.PartsContainerBakedModel;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;

public class MEBlockDefinitions extends Definitions<Block, IBlockDefinition<Block>> implements IMEBlockDefinitions {

	private final IBlockDefinition partsContainer;

	public MEBlockDefinitions(DefinitionFactory registry){
		partsContainer = registry.<Block, IBlockDefinition<Block>, IBlockBuilder<Block, ?>, Block>definitionBuilder(new ResourceLocation(AppEng.MODID, "parts_container"), ih(new PartsContainerBlock())).setFeature(null).initializationComponent(Side.CLIENT, new ModelOverrideComponent<>(modelBakeEvent -> modelBakeEvent.getModelRegistry().putObject(new ModelResourceLocation(new ResourceLocation(AppEng.MODID, "me/parts_container"), null), new PartsContainerBakedModel()))).build();
	}

	private DefinitionFactory.InputHandler<Block, Block> ih(Block block){
		return new DefinitionFactory.InputHandler<Block, Block>(block) {};
	}

}
