
package appeng.api.definitions.sub;


import net.minecraft.item.Item;

import appeng.api.item.IStateItem;


/**
 * Default interface for item sub-definitions. For use with type parameters. All AE item sub definitions implement this. And all AE items with states implement {@link IStateItem}.
 * 
 * @author Elix_x
 *
 * @param <S> Item State
 * @param <I> Item
 */
public interface IItemSubDefinition<S extends IStateItem.State<I>, I extends Item & IStateItem<I>> extends ISubDefinition<S, I, IItemSubDefinition<S, I>>
{

}
