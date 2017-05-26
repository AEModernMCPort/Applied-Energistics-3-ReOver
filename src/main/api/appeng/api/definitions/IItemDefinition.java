package appeng.api.definitions;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import java.util.Optional;

public interface IItemDefinition<I extends Item> extends IDefinition<I> {

	/**
	 * @return an {@link ItemStack} with specified quantity of this item.
	 */
	Optional<ItemStack> maybeStack(int stackSize);

	default boolean isSameAs(ItemStack itemstack){
		return isSameAs(itemstack.getItem());
	}

}
