package appeng.core.api.bootstrap;

import appeng.api.bootstrap.IDefinitionBuilder;
import appeng.api.definitions.IItemDefinition;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;

public interface IItemBuilder<I extends Item, II extends IItemBuilder<I, II>> extends IDefinitionBuilder<I, IItemDefinition<I>, II> {

	II creativeTab(CreativeTabs tab);

	II defaultModel();

}
