package appeng.core.api.item;

import appeng.api.item.IStateItem;
import net.minecraft.item.Item;

public interface IItemMaterial<I extends Item & IItemMaterial<I>> extends IStateItem<I> {

}
