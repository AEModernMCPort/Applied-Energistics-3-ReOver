package appeng.core.item;

import appeng.api.block.BlockStackDisplayNameProvider;
import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

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

	@Override
	public String getItemStackDisplayName(ItemStack stack){
		return block instanceof BlockStackDisplayNameProvider ? ((BlockStackDisplayNameProvider) block).getItemStackDisplayName(stack, super.getItemStackDisplayName(stack)) : super.getItemStackDisplayName(stack);
	}
}
