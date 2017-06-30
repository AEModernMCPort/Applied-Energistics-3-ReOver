package appeng.core.item;

import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;

public class ItemBlockGood extends ItemBlock {

	public ItemBlockGood(Block block){
		super(block);
		setMaxDamage(0);
		if(!block.getBlockState().getProperties().isEmpty()) setHasSubtypes(true);
	}

	@Override
	public int getMetadata(int damage){
		return hasSubtypes ? damage : 0;
	}

}
