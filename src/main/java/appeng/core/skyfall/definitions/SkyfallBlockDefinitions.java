package appeng.core.skyfall.definitions;

import appeng.api.bootstrap.DefinitionFactory;
import appeng.api.definitions.IBlockDefinition;
import appeng.core.AppEng;
import appeng.core.api.bootstrap.IBlockBuilder;
import appeng.core.api.definitions.ICoreBlockDefinitions;
import appeng.core.core.block.SkystoneBlock;
import appeng.core.lib.definitions.Definitions;
import appeng.core.skyfall.block.CertusInfusedBlock;
import net.minecraft.block.Block;
import net.minecraft.util.ResourceLocation;

public class SkyfallBlockDefinitions extends Definitions<Block, IBlockDefinition<Block>> implements ICoreBlockDefinitions {

	private final IBlockDefinition<Block> certusInfused;

	public SkyfallBlockDefinitions(DefinitionFactory registry){
		certusInfused = registry.<Block, IBlockDefinition<Block>, IBlockBuilder<Block, ?>, Block>definitionBuilder(new ResourceLocation(AppEng.MODID, "certus_infused"), ih(new CertusInfusedBlock())).createDefaultItem().build();
	}

	private DefinitionFactory.InputHandler<Block, Block> ih(Block block){
		return new DefinitionFactory.InputHandler<Block, Block>(block) {};
	}
}
