package appeng.core.core.bootstrap;

import appeng.api.bootstrap.IDefinitionBuilder;
import appeng.api.definitions.IItemDefinition;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.item.Item;

/**
 * @author Fredi100
 */
public class ItemColorComponent implements IDefinitionBuilder.DefinitionInitializationComponent<Item, IItemDefinition<Item>>{

    private final IItemColor itemColor;

    public ItemColorComponent(IItemColor itemColor){
        this.itemColor = itemColor;
    }

    @Override
    public void init(IItemDefinition<Item> def){
        Minecraft.getMinecraft().getItemColors().registerItemColorHandler(itemColor, def.maybe().get());
    }
}
