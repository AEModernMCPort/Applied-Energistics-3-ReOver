
package appeng.core.api.items;


import net.minecraft.item.Item;

import appeng.api.item.IStateItem;


public interface IItemMaterial<I extends Item & IItemMaterial<I>> extends IStateItem<I>
{

}
