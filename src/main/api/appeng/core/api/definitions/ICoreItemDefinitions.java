
package appeng.core.api.definitions;


import net.minecraft.item.Item;

import appeng.api.definitions.IDefinitions;
import appeng.api.definitions.IItemDefinition;


public interface ICoreItemDefinitions extends IDefinitions<Item, IItemDefinition<Item>>
{

	default IItemDefinition<Item> material()
	{
		return get( "material" );
	}

}
