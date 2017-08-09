package appeng.core.core.api.bootstrap;

import appeng.api.bootstrap.IDefinitionBuilder;
import appeng.core.core.api.definition.IItemDefinition;
import net.minecraft.item.Item;

public interface IItemBuilder<I extends Item, II extends IItemBuilder<I, II>> extends IDefinitionBuilder<I, IItemDefinition<I>, II> {

	/**
	 * Loads default model for this item
	 * @return <tt>this</tt>
	 */
	II defaultModel();

}
