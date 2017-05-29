package appeng.core.crafting.definitions;

import appeng.api.definitions.IItemDefinition;
import appeng.core.crafting.api.definitions.ICraftingItemDefinitions;
import appeng.core.lib.bootstrap_olde.FeatureFactory;
import appeng.core.lib.definitions.Definitions;
import net.minecraft.item.Item;

public class CraftingItemDefinitions extends Definitions<Item, IItemDefinition<Item>>
		implements ICraftingItemDefinitions {

	public CraftingItemDefinitions(FeatureFactory registry){
		init(registry.buildDefaultItemBlocks());
	}

}
