package appeng.core.core.bootstrap;

import appeng.api.bootstrap.DefinitionFactory;
import appeng.api.definitions.IBlockDefinition;
import appeng.api.definitions.IItemDefinition;
import appeng.core.AppEng;
import appeng.core.api.bootstrap.BlockItemCustomizer;
import appeng.core.api.bootstrap.IBlockBuilder;
import appeng.core.api.bootstrap.IItemBuilder;
import appeng.core.core.AppEngCore;
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
import net.minecraftforge.fml.relauncher.Side;

import javax.annotation.Nonnull;
import java.util.Optional;
import java.util.function.Function;

public class BlockDefinitionBuilder<B extends Block> extends DefinitionBuilder<B, B, IBlockDefinition<B>, BlockDefinitionBuilder<B>> implements IBlockBuilder<B, BlockDefinitionBuilder<B>> {

	//TODO 1.11.2-ReOver - :P
	private CreativeTabs creativeTab = CreativeTabs.REDSTONE;

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
	public <I extends ItemBlock, C extends BlockItemCustomizer<I>> BlockDefinitionBuilder<B> createItem(@Nonnull C itemBlock){
		return setItem(block -> itemBlock.customize(factory.definitionBuilder(registryName, blockItemIh(itemBlock.createItem(block.maybe().get())))).setFeature(feature).build());
	}

	@Override
	public BlockDefinitionBuilder<B> createDefaultItem(){
		return createItem(new BlockItemCustomizer<ItemBlock>(){

			@Nonnull
			@Override
			public ItemBlock createItem(Block block){
				return new ItemBlock(block);
			}

			@Nonnull
			@Override
			public IItemBuilder<ItemBlock, ?> customize(@Nonnull IItemBuilder<ItemBlock, ?> builder){
				return builder.defaultModel("normal");
			}

		});
	}

	/*@SideOnly(Side.CLIENT)
	private void customizeForClient(BlockRenderingCustomizer callback){
		callback.customize(blockRendering, itemRendering);
	}*/

	@Override
	public BlockDefinitionBuilder<B> mapBlockStateToModuleSubfolder(){
		String module = AppEng.instance().getCurrentName();
		return this.<StateMapperComponent<B>>initializationComponent(Side.CLIENT, new StateMapperComponent<B>(() -> Optional.of(new SubfolderStateMapper(module))));
	}

	@Override
	public IBlockDefinition<B> def(B block){
		if(block == null){
			return new BlockDefinition<B>(registryName, null);
		}

		block.setCreativeTab(creativeTab);
		block.setUnlocalizedName(registryName.getResourceDomain() + "." + registryName.getResourcePath());

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
