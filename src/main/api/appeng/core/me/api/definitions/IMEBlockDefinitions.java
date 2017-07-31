package appeng.core.me.api.definitions;

import appeng.api.AEModInfo;
import appeng.core.core.api.definition.IBlockDefinition;
import appeng.api.definitions.IDefinitions;
import net.minecraft.block.Block;
import net.minecraft.util.ResourceLocation;

public interface IMEBlockDefinitions extends IDefinitions<Block, IBlockDefinition<Block>> {

	default IBlockDefinition<Block> partsContainer(){
		return get(new ResourceLocation(AEModInfo.MODID, "parts_container"));
	}
}
