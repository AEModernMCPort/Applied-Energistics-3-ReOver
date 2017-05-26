package appeng.api.item;

import appeng.api.item.IStateItemState.Property;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public interface IStateItem<I extends Item & IStateItem<I>> {

	boolean isValid(Property property);

	<V> Property<V> getProperty(String name);

	IStateItemState<I> getState(ItemStack itemstack);

	ItemStack getItemStack(IStateItemState<I> state, int amount);

	IStateItemState<I> getDefaultState();

}
