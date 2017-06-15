package appeng.core.staticfire.definitions;

import appeng.api.bootstrap.DefinitionFactory;
import appeng.api.definitions.IBlockDefinition;
import appeng.api.definitions.IDefinitions;
import appeng.core.AppEng;
import appeng.core.core.bootstrap.BlockDefinitionBuilder;
import appeng.core.lib.definitions.BlockDefinition;
import appeng.core.lib.definitions.Definitions;
import appeng.core.staticfire.api.definitions.IStaticFireBlockDefinitions;
import appeng.core.staticfire.block.FiberCableMaker;
import appeng.core.staticfire.block.QuantumPillarBase;
import appeng.core.staticfire.block.StaticFireBlockBase;
import appeng.core.staticfire.block.TestBlock;
import appeng.core.staticfire.tesr.FiberCableMakerTESR;
import appeng.core.staticfire.tileEntity.FiberCableMakerTileEntity;
import net.minecraft.block.Block;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.registry.ClientRegistry;

public class StaticFireBlockDefinitions extends Definitions<Block, IBlockDefinition<Block>> implements IStaticFireBlockDefinitions{

    public StaticFireBlockDefinitions(DefinitionFactory registry){


        BlockDefinitionBuilder a = registry.definitionBuilder(new ResourceLocation(AppEng.MODID, "test"), ih(new TestBlock()));
        //a.createDefaultItemBlock();
        a.build();


        Reg(registry, new FiberCableMaker());

        Reg(registry, new QuantumPillarBase());

        // Bind our TESR to our tile entity
        ClientRegistry.bindTileEntitySpecialRenderer(FiberCableMakerTileEntity.class, new FiberCableMakerTESR());

    }

    private DefinitionFactory.InputHandler<Block, Block> ih(Block block){
        return new DefinitionFactory.InputHandler<Block, Block>(block) {};
    }

    public <T extends Block & StaticFireBlockBase> void Reg(DefinitionFactory registry, T item)
    {
        BlockDefinitionBuilder a = registry.definitionBuilder(new ResourceLocation(AppEng.MODID, item.getRegistryNameSF()), ih(item));
        a.createDefaultItem();
        //a.defaultModel(new ResourceLocation(AppEng.MODID, item.getRegistryNameSF()).toString());
        a.build();
    }
}