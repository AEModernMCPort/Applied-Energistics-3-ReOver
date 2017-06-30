package appeng.core.api.definitions;

import appeng.api.AEModInfo;
import appeng.api.definitions.IBlockDefinition;
import appeng.api.definitions.IDefinitions;
import net.minecraft.block.Block;
import net.minecraft.util.ResourceLocation;

public interface ICoreBlockDefinitions extends IDefinitions<Block, IBlockDefinition<Block>> {

	default IBlockDefinition<Block> skystone(){
		return get(new ResourceLocation(AEModInfo.MODID, "skystone"));
	}

}
