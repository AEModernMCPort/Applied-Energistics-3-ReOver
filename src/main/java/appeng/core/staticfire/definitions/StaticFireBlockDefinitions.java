package appeng.core.staticfire.definitions;

import appeng.api.bootstrap.DefinitionFactory;
import appeng.api.bootstrap.IDefinitionBuilder;
import appeng.api.definitions.IBlockDefinition;
import appeng.api.definitions.IDefinitions;
import appeng.api.definitions.IItemDefinition;
import appeng.core.AppEng;
import appeng.core.api.bootstrap.BlockItemCustomizer;
import appeng.core.api.bootstrap.IBlockBuilder;
import appeng.core.api.bootstrap.IItemBuilder;
import appeng.core.core.bootstrap.BlockDefinitionBuilder;
import appeng.core.core.client.bootstrap.ItemMeshDefinitionComponent;
import appeng.core.lib.definitions.BlockDefinition;
import appeng.core.lib.definitions.Definitions;
import appeng.core.staticfire.api.IStaticFire;
import appeng.core.staticfire.api.definitions.IStaticFireBlockDefinitions;
import appeng.core.staticfire.block.*;
import appeng.core.staticfire.tesr.FiberCableMakerTESR;
import appeng.core.staticfire.tileEntity.FiberCableMakerTileEntity;
import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.relauncher.Side;

public class StaticFireBlockDefinitions extends Definitions<Block, IBlockDefinition<Block>> implements IStaticFireBlockDefinitions{

    private final IBlockDefinition skyblock


    public StaticFireBlockDefinitions(DefinitionFactory registry){


        BlockDefinitionBuilder a = registry.definitionBuilder(new ResourceLocation(AppEng.MODID, "test"), ih(new TestBlock()));
        //a.createDefaultItemBlock();
        a.build();


        //Reg(registry, new FiberCableMaker());

        //Reg(registry, new QuantumPillarBase());
        //Reg(registry, new CryoPod());
        //Reg(registry, new SkyBlock());

        // Bind our TESR to our tile entity
        ClientRegistry.bindTileEntitySpecialRenderer(FiberCableMakerTileEntity.class, new FiberCableMakerTESR());

        Block item = new SkyBlock();

        skyblock = registry.<Block, IBlockDefinition<Block>, IBlockBuilder<Block, ?>, Block>definitionBuilder(new ResourceLocation(AppEng.MODID, "sky_stone"), ih(item))
                .<ItemBlock, BlockItemCustomizer.UseDefaultItemCustomize>createItem(
                        builder -> ((IItemBuilder<ItemBlock, ?>) builder).<IDefinitionBuilder.DefinitionInitializationComponent.PreInit<ItemBlock, IItemDefinition<ItemBlock>>>initializationComponent(
                                Side.CLIENT, def -> ItemMeshDefinitionComponent.BlockStateMapper2ItemMeshDefinition.createByMetadata(Block.getBlockFromItem(def.maybe().get())))).build();

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