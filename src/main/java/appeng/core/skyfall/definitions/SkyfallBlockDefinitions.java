package appeng.core.skyfall.definitions;

import appeng.api.bootstrap.DefinitionFactory;
import appeng.core.core.api.bootstrap.IBlockBuilder;
import appeng.core.core.api.definition.IBlockDefinition;
import appeng.core.lib.definitions.Definitions;
import appeng.core.skyfall.AppEngSkyfall;
import appeng.core.skyfall.api.definitions.ISkyfallBlockDefinitions;
import appeng.core.skyfall.block.CertusInfusedBlock;
import appeng.core.skyfall.client.CertusInfusedBlockModelComponent;
import net.minecraft.block.Block;
import net.minecraftforge.fml.relauncher.Side;

public class SkyfallBlockDefinitions extends Definitions<Block, IBlockDefinition<Block>> implements ISkyfallBlockDefinitions {

	public SkyfallBlockDefinitions(DefinitionFactory registry){
		AppEngSkyfall.INSTANCE.config.meteorite.allowedBlocks.forEach(blockState -> registry.<CertusInfusedBlock, IBlockDefinition<CertusInfusedBlock>, IBlockBuilder<CertusInfusedBlock, ?>, Block>definitionBuilder(CertusInfusedBlock.formatToInfused(blockState), ih(new CertusInfusedBlock(blockState))).createDefaultItem().initializationComponent(Side.CLIENT, new CertusInfusedBlockModelComponent<>()).build());
	}

	private DefinitionFactory.InputHandler<Block, Block> ih(Block block){
		return new DefinitionFactory.InputHandler<Block, Block>(block) {};
	}
}
