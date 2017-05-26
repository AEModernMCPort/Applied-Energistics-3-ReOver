package appeng.core.me.definitions;

import appeng.api.definitions.IItemDefinition;
import appeng.core.lib.bootstrap.FeatureFactory;
import appeng.core.lib.definitions.Definitions;
import appeng.core.me.api.definitions.IMEItemDefinitions;
import net.minecraft.item.Item;

public class MEItemDefinitions extends Definitions<Item, IItemDefinition<Item>> implements IMEItemDefinitions {

	public MEItemDefinitions(FeatureFactory registry){
		init(registry.buildDefaultItemBlocks());
	}

}
