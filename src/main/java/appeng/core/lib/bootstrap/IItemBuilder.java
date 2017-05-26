
package appeng.core.lib.bootstrap;


import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;

import appeng.api.definitions.IItemDefinition;


/**
 * Allows an item to be defined and registered with the game.
 * The item is only registered once build is called.
 */
public interface IItemBuilder<I extends Item, II extends IItemBuilder<I, II>> extends IDefinitionBuilder<I, IItemDefinition<I>, II>
{

	II creativeTab( CreativeTabs tab );

	II rendering( ItemRenderingCustomizer callback );

}
