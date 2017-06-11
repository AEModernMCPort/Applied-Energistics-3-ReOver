package appeng.core.staticfire.definitions;

import appeng.api.bootstrap.DefinitionFactory;
import appeng.api.definitions.IBlockDefinition;
import appeng.api.definitions.IItemDefinition;
import appeng.core.AppEng;
import appeng.core.lib.definitions.Definitions;
import appeng.core.staticfire.api.definitions.IStaticFireBlockDefinitions;
import appeng.core.staticfire.api.definitions.IStaticFireItemDefinitions;
import appeng.core.staticfire.block.TestBlock;
import appeng.core.staticfire.item.ItemTest;
import net.minecraft.block.Block;
import net.minecraft.block.BlockAnvil;
import net.minecraft.block.BlockBone;
import net.minecraft.block.material.Material;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.ResourceLocation;

public class StaticFireItemDefinitions extends Definitions<Item, IItemDefinition<Item>> implements IStaticFireItemDefinitions{

    public StaticFireItemDefinitions(DefinitionFactory registry){

        //registry.definitionBuilder(new ResourceLocation(AppEng.MODID, "test"), ih(new ItemBlock(new TestBlock()))).build();
        registry.definitionBuilder(new ResourceLocation(AppEng.MODID, "itemtest"), ih(new ItemTest())).build();
        //init(registry);
        System.out.println("Stuff");
    }

    private DefinitionFactory.InputHandler<Item, Item> ih(Item item){
        return new DefinitionFactory.InputHandler<Item, Item>(item) {};
    }
}