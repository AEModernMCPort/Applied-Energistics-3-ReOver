package appeng.core.core.client.bootstrap;

import appeng.api.bootstrap.IDefinitionBuilder;
import appeng.core.core.api.definition.IItemDefinition;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.item.Item;

import java.util.Optional;
import java.util.function.Supplier;

/**
 * @author Fredi100
 */
public class ItemColorComponent implements IDefinitionBuilder.DefinitionInitializationComponent<Item, IItemDefinition<Item>> {

	private final Supplier<Optional<IItemColor>> itemColor;

	public ItemColorComponent(Supplier<Optional<IItemColor>> itemColor){
		this.itemColor = itemColor;
	}

	@Override
	public void init(IItemDefinition<Item> def){
		itemColor.get().ifPresent(color -> Minecraft.getMinecraft().getItemColors().registerItemColorHandler(color, def.maybe().get()));
	}
}
