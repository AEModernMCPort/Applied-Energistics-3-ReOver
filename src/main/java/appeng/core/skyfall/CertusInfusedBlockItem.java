package appeng.core.skyfall;

import appeng.core.item.ItemBlockGood;
import appeng.core.skyfall.block.CertusInfusedBlock;
import net.minecraft.item.ItemStack;

public class CertusInfusedBlockItem extends ItemBlockGood<CertusInfusedBlock> {

	public CertusInfusedBlockItem(CertusInfusedBlock block){
		super(block);
	}

	@Override
	public String getItemStackDisplayName(ItemStack stack){
		return getBlock().getDisplayName(getBlock().getStateFromMeta(stack.getItemDamage()));
	}
}
