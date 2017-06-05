package appeng.core.staticfire.definitions;

import appeng.api.bootstrap.DefinitionFactory;
import appeng.api.definitions.IBlockDefinition;
import appeng.api.definitions.IDefinitions;
import appeng.core.AppEng;
import appeng.core.lib.definitions.Definitions;
import appeng.core.staticfire.api.definitions.IStaticFireBlockDefinitions;
import appeng.core.staticfire.block.TestBlock;
import net.minecraft.block.Block;
import net.minecraft.util.ResourceLocation;

public class StaticFireBlockDefinitions extends Definitions<Block, IBlockDefinition<Block>> implements IStaticFireBlockDefinitions{

    public StaticFireBlockDefinitions(DefinitionFactory registry){
        init();
        registry.definitionBuilder(new ResourceLocation(AppEng.MODID, "test"), ih(new TestBlock())).build();

        System.out.println("Stuff");


    }

    private DefinitionFactory.InputHandler<Block, Block> ih(Block block){
        return new DefinitionFactory.InputHandler<Block, Block>(block) {};
    }
}