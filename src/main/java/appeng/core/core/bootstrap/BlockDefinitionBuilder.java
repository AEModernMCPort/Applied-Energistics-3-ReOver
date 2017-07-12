package appeng.core.core.bootstrap;

import appeng.api.bootstrap.DefinitionFactory;
import appeng.api.definitions.IBlockDefinition;
import appeng.api.definitions.IItemDefinition;
import appeng.core.AppEng;
import appeng.core.api.bootstrap.BlockItemCustomizer;
import appeng.core.api.bootstrap.IBlockBuilder;
import appeng.core.api.bootstrap.IItemBuilder;
import appeng.core.core.AppEngCore;
import appeng.core.core.client.bootstrap.ItemMeshDefinitionComponent;
import appeng.core.core.client.bootstrap.StateMapperComponent;
import appeng.core.core.client.statemap.SubfolderStateMapper;
import appeng.core.lib.bootstrap.DefinitionBuilder;
import appeng.core.lib.definitions.BlockDefinition;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.relauncher.Side;

import javax.annotation.Nonnull;
import java.util.Optional;
import java.util.function.Function;

public class BlockDefinitionBuilder<B extends Block> extends DefinitionBuilder<B, B, IBlockDefinition<B>, BlockDefinitionBuilder<B>> implements IBlockBuilder<B, BlockDefinitionBuilder<B>> {

	private Function<IBlockDefinition<B>, IItemDefinition<ItemBlock>> item = def -> null;

	//TODO 1.11.2-ReOver - Be back?
/*	@SideOnly(Side.CLIENT)
	private BlockRendering blockRendering;

	@SideOnly(Side.CLIENT)
	private ItemRendering itemRendering;*/

	public BlockDefinitionBuilder(DefinitionFactory factory, ResourceLocation id, B block){
		super(factory, id, block, "block");

		/*if(Platform.isClient()){
			blockRendering = new BlockRendering();
			itemRendering = new ItemRendering();
		}*/
	}

	@Override
	public <I extends ItemBlock> BlockDefinitionBuilder<B> setItem(@Nonnull Function<IBlockDefinition<B>, IItemDefinition<I>> item){
		this.item = (Function) item;
		return this;
	}

	@Override
	public <I extends ItemBlock, C extends BlockItemCustomizer<B, I>> BlockDefinitionBuilder<B> createItem(@Nonnull C itemBlock){
		return setItem(block -> itemBlock.customize(factory.definitionBuilder(registryName, blockItemIh(itemBlock.createItem(block))), block).setFeature(feature).build());
	}

	@Override
	public BlockDefinitionBuilder<B> createDefaultItem(){
		return this.<ItemBlock, BlockItemCustomizer.UseDefaultItemCustomize<B>>createItem((builder, block) -> builder.<ItemMeshDefinitionComponent.BlockStateMapper2ItemMeshDefinition<ItemBlock>>initializationComponent(Side.CLIENT, ItemMeshDefinitionComponent.BlockStateMapper2ItemMeshDefinition.createByMetadata(block.maybe().get())));
	}

	/*@SideOnly(Side.CLIENT)
	private void customizeForClient(BlockRenderingCustomizer callback){
		callback.customize(blockRendering, itemRendering);
	}*/

	@Override
	public IBlockDefinition<B> def(B block){
		if(block == null){
			return new BlockDefinition<B>(registryName, null);
		}

		if(block.getUnlocalizedName().equals("tile.null")) block.setUnlocalizedName(registryName.getResourceDomain() + "." + registryName.getResourcePath());

		if(Loader.instance().activeModContainer().getModId().equals(AppEng.MODID)){
			String module = AppEng.instance().getCurrentName();
			initializationComponent(Side.CLIENT, new StateMapperComponent<>(old -> Optional.of(new SubfolderStateMapper(old, module))));
		}

		/*if(Platform.isClient()){
			if(block instanceof AEBaseTileBlock){
				AEBaseTileBlock tileBlock = (AEBaseTileBlock) block;
				blockRendering.apply(factory, block, tileBlock.getTileEntityClass());
			} else {
				blockRendering.apply(factory, block, null);
			}
		}*/

		BlockDefinition definition = new BlockDefinition<B>(registryName, block);
		if(!block.getBlockState().getProperties().isEmpty())
			definition.setSubDefinition(() -> new BlockSubDefinition<IBlockState, Block>(block.getDefaultState(), definition));

		IItemDefinition<ItemBlock> item = this.item.apply(definition);
		definition.setItem(item);
		if(item != null) factory.addDefault(item);

		return definition;
	}

	public DefinitionFactory.InputHandler<Item, Item> blockItemIh(ItemBlock item){
		return new DefinitionFactory.InputHandler<Item, Item>(item) {};
	}

}
