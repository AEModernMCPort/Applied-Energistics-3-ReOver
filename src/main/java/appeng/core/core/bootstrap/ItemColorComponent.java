package appeng.core.core.bootstrap;

import appeng.api.bootstrap.IDefinitionBuilder;
import appeng.api.definitions.IItemDefinition;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.item.Item;

import java.util.function.Supplier;

/**
 * @author Fredi100
 */
public class ItemColorComponent implements IDefinitionBuilder.DefinitionInitializationComponent<Item, IItemDefinition<Item>> {

	private final Supplier<IItemColor> itemColor;

	public ItemColorComponent(Supplier<IItemColor> itemColor){
		this.itemColor = itemColor;
	}

	@Override
	public void init(IItemDefinition<Item> def){
		System.out.println(this);
		Minecraft.getMinecraft().getItemColors().registerItemColorHandler(itemColor.get(), def.maybe().get());
	}
}
