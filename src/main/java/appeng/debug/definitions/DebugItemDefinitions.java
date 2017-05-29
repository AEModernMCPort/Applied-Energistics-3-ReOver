package appeng.debug.definitions;

import appeng.api.definitions.IItemDefinition;
import appeng.core.lib.bootstrap_olde.FeatureFactory;
import appeng.core.lib.definitions.Definitions;
import net.minecraft.item.Item;

public class DebugItemDefinitions extends Definitions<Item, IItemDefinition<Item>> {

	public DebugItemDefinitions(FeatureFactory registry){
		init(registry.buildDefaultItemBlocks());
	}

}
