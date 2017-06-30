package appeng.core.skyfall.definitions;

import appeng.api.bootstrap.DefinitionFactory;
import appeng.api.definitions.IBlockDefinition;
import appeng.core.AppEng;
import appeng.core.api.bootstrap.BlockItemCustomizer;
import appeng.core.api.bootstrap.IBlockBuilder;
import appeng.core.api.bootstrap.IItemBuilder;
import appeng.core.api.definitions.ICoreBlockDefinitions;
import appeng.core.core.client.bootstrap.ItemMeshDefinitionComponent;
import appeng.core.core.client.bootstrap.StateMapperComponent;
import appeng.core.lib.definitions.Definitions;
import appeng.core.skyfall.CertusInfusedBlockItem;
import appeng.core.skyfall.block.CertusInfusedBlock;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.block.statemap.DefaultStateMapper;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;

import javax.annotation.Nonnull;
import java.util.Optional;

public class SkyfallBlockDefinitions extends Definitions<Block, IBlockDefinition<Block>> implements ICoreBlockDefinitions {

	private final IBlockDefinition<Block> certusInfused;

	public SkyfallBlockDefinitions(DefinitionFactory registry){
		certusInfused = registry.<Block, IBlockDefinition<Block>, IBlockBuilder<Block, ?>, Block>definitionBuilder(new ResourceLocation(AppEng.MODID, "certus_infused"), ih(new CertusInfusedBlock())).createItem(new BlockItemCustomizer<Block, ItemBlock>() {

			@Nonnull
			@Override
			public ItemBlock createItem(IBlockDefinition<Block> block){
				return new CertusInfusedBlockItem((CertusInfusedBlock) block.maybe().get());
			}

			@Nonnull
			@Override
			public IItemBuilder<ItemBlock, ?> customize(@Nonnull IItemBuilder<ItemBlock, ?> builder, @Nonnull IBlockDefinition<Block> block){
				return builder.<ItemMeshDefinitionComponent.BlockStateMapper2ItemMeshDefinition<ItemBlock>>initializationComponent(Side.CLIENT, ItemMeshDefinitionComponent.BlockStateMapper2ItemMeshDefinition.createByMetadata(block.maybe().get()));
			}

		}).initializationComponent(Side.CLIENT, new StateMapperComponent<>(iStateMapper -> Optional.of(new DefaultStateMapper()))).build();
	}

	private DefinitionFactory.InputHandler<Block, Block> ih(Block block){
		return new DefinitionFactory.InputHandler<Block, Block>(block) {};
	}
}
