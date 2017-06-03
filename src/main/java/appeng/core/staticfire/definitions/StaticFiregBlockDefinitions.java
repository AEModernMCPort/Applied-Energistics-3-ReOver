package appeng.core.staticfire.definitions;

import appeng.api.bootstrap.DefinitionFactory;
import appeng.api.definitions.IBlockDefinition;
import appeng.api.definitions.IDefinitions;
import appeng.core.AppEng;
import appeng.core.lib.definitions.Definitions;
import appeng.core.staticfire.block.TestBlock;
import net.minecraft.block.Block;
import net.minecraft.util.ResourceLocation;

/**
 * Created by Peter on 2017. 06. 03..
 */
public class StaticFiregBlockDefinitions extends Definitions<Block, IBlockDefinition<Block>> implements IDefinitions<Block, IBlockDefinition<Block>> {

    public StaticFiregBlockDefinitions(DefinitionFactory registry){
        init();
        registry.definitionBuilder(new ResourceLocation(AppEng.MODID, "Test"), ih(new TestBlock())).build();
    }

}