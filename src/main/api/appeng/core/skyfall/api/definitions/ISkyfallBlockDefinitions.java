package appeng.core.skyfall.api.definitions;

import appeng.api.AEModInfo;
import appeng.api.definitions.IDefinitions;
import appeng.core.core.api.definition.IBlockDefinition;
import net.minecraft.block.Block;
import net.minecraft.util.ResourceLocation;

public interface ISkyfallBlockDefinitions extends IDefinitions<Block, IBlockDefinition<Block>> {

	default IBlockDefinition<Block> certusInfused(){
		return get(new ResourceLocation(AEModInfo.MODID, "certus_infused"));
	}

}
