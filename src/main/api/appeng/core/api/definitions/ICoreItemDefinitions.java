package appeng.core.api.definitions;

import appeng.api.definitions.IDefinitions;
import appeng.api.definitions.IItemDefinition;
import net.minecraft.item.Item;

public interface ICoreItemDefinitions extends IDefinitions<Item, IItemDefinition<Item>> {

	default IItemDefinition<Item> material(){
		return get("material");
	}

}
