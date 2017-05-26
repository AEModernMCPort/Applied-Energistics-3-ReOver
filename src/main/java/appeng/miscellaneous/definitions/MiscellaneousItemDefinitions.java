package appeng.miscellaneous.definitions;

import appeng.api.definitions.IItemDefinition;
import appeng.core.lib.bootstrap.FeatureFactory;
import appeng.core.lib.definitions.Definitions;
import appeng.miscellaneous.api.definitions.IMiscellaneousItemDefinitions;
import net.minecraft.item.Item;

public class MiscellaneousItemDefinitions extends Definitions<Item, IItemDefinition<Item>>
		implements IMiscellaneousItemDefinitions {

	public MiscellaneousItemDefinitions(FeatureFactory registry){
		init(registry.buildDefaultItemBlocks());
	}

}
