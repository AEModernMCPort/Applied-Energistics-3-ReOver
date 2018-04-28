package appeng.core.core.definitions;

import appeng.api.bootstrap.DefinitionFactory;
import appeng.core.AppEng;
import appeng.core.core.api.bootstrap.IBlockBuilder;
import appeng.core.core.api.definition.IBlockDefinition;
import appeng.core.core.api.definitions.ICoreBlockDefinitions;
import appeng.core.core.block.SkystoneBlock;
import appeng.core.lib.definitions.Definitions;
import net.minecraft.block.Block;
import net.minecraft.util.ResourceLocation;

public class CoreBlockDefinitions extends Definitions<Block, IBlockDefinition<Block>> implements ICoreBlockDefinitions {

	public CoreBlockDefinitions(DefinitionFactory registry){
		for(SkystoneBlock.Variant variant : SkystoneBlock.Variant.values()) dynamicallyCompiled(registry.<Block, IBlockDefinition<Block>, IBlockBuilder<Block, ?>, Block>definitionBuilder(new ResourceLocation(AppEng.MODID, "skystone_" + variant.getName()), ih(new SkystoneBlock(variant))).createDefaultItem().build());
	}

	private DefinitionFactory.InputHandler<Block, Block> ih(Block block){
		return new DefinitionFactory.InputHandler<Block, Block>(block) {};
	}
}
