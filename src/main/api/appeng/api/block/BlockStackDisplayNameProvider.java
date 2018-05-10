package appeng.api.block;

import net.minecraft.item.ItemStack;

public interface BlockStackDisplayNameProvider {

	String getItemStackDisplayName(ItemStack stack, String def);

}
