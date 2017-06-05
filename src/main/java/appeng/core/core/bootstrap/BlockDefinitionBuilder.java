package appeng.core.core.bootstrap;

import appeng.api.bootstrap.DefinitionFactory;
import appeng.api.definitions.IBlockDefinition;
import appeng.core.api.bootstrap.IBlockBuilder;
import appeng.core.api.bootstrap.IItemBuilder;
import appeng.core.lib.bootstrap.DefinitionBuilder;
import appeng.core.lib.bootstrap_olde.BlockRenderingCustomizer;
import appeng.core.lib.bootstrap_olde.BlockSubDefinition;
import appeng.core.api.bootstrap.ItemBlockCustomizer;
import appeng.core.lib.definitions.BlockDefinition;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;

public class BlockDefinitionBuilder<B extends Block> extends DefinitionBuilder<B, B, IBlockDefinition<B>, BlockDefinitionBuilder<B>> implements IBlockBuilder<B, BlockDefinitionBuilder<B>> {

	//TODO 1.11.2-ReOver - :P
	private CreativeTabs creativeTab = CreativeTabs.REDSTONE;

	private ItemBlockCustomizer itemBlock = null;

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

	public BlockDefinitionBuilder<B> rendering(BlockRenderingCustomizer callback){
		/*if(Platform.isClient()){
			customizeForClient(callback);
		}*/

		return this;
	}

	@Override
	public BlockDefinitionBuilder<B> createDefaultItemBlock(){
		return createItemBlock(new ItemBlockCustomizer<ItemBlock>() {

			@Nonnull
			@Override
			public ItemBlock createItemBlock(Block block){
				return new ItemBlock(block);
			}

			@Nonnull
			@Override
			public IItemBuilder<ItemBlock, ?> customize(@Nonnull IItemBuilder<ItemBlock, ?> builder){
				return builder.setFeature(feature);
			}

		});
	}

	public BlockDefinitionBuilder<B> createItemBlock(ItemBlockCustomizer ib){
		itemBlock = ib;
		return this;
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
		if(!block.getBlockState().getProperties().isEmpty()) definition.setSubDefinition(() -> new BlockSubDefinition<IBlockState, Block>(block.getDefaultState(), definition));

		if(itemBlock != null) factory.addDefault(itemBlock.customize(factory.definitionBuilder(registryName, itemBlockIh(itemBlock.createItemBlock(block)))).setFeature(feature).build());

		return definition;
	}

	public DefinitionFactory.InputHandler<Item, Item> itemBlockIh(ItemBlock item){
		return new DefinitionFactory.InputHandler<Item, Item>(item){};
	}

}
