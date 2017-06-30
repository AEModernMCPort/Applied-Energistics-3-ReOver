package appeng.core.item;

import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;

public class ItemBlockGood<B extends Block> extends ItemBlock {

	public ItemBlockGood(B block){
		super(block);
		setMaxDamage(0);
		if(!block.getBlockState().getProperties().isEmpty()) setHasSubtypes(true);
	}

	@Override
	public B getBlock(){
		return (B) super.getBlock();
	}

	@Override
	public int getMetadata(int damage){
		return hasSubtypes ? damage : 0;
	}

}
