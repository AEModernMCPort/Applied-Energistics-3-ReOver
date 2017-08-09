package appeng.core.core.api.item;

import appeng.api.item.IStateItem;
import appeng.api.item.IStateItemState;
import appeng.core.core.api.material.Material;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public interface IItemMaterial<I extends Item & IItemMaterial<I>> extends IStateItem<I> {

	Material getMaterial(IStateItemState<I> state);

	default Material getMaterial(ItemStack stack){
		return getMaterial(getState(stack));
	}

}
