package appeng.core.core.client.bootstrap;

import appeng.api.bootstrap.IDefinitionBuilder;
import appeng.core.core.api.definition.IItemDefinition;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemMeshDefinition;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * @author Fredi100
 */
public class ItemMeshDefinitionComponent<I extends Item> implements IDefinitionBuilder.DefinitionInitializationComponent<I, IItemDefinition<I>> {

	private final Supplier<Optional<ItemMeshDefinition>> meshDefinition;

	public ItemMeshDefinitionComponent(@Nonnull Supplier<Optional<ItemMeshDefinition>> meshDefinition){
		this.meshDefinition = meshDefinition;
	}

	@Override
	public void init(IItemDefinition<I> def){
		meshDefinition.get().ifPresent(itemMeshDefinition -> Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(def.maybe().get(), itemMeshDefinition));
	}

	public static class BlockStateMapper2ItemMeshDefinition<I extends Item> extends ItemMeshDefinitionComponent<I> {

		public BlockStateMapper2ItemMeshDefinition(Optional<Block> blocko, Function<ItemStack, IBlockState> stackToState){
			super(() -> blocko.map(block -> {
				Map<IBlockState, ModelResourceLocation> stateModelMap = Minecraft.getMinecraft().getBlockRendererDispatcher().getBlockModelShapes().getBlockStateMapper().getVariants(block);
				return stack -> stateModelMap.get(stackToState.apply(stack));
			}));
		}

		public static <I extends Item> BlockStateMapper2ItemMeshDefinition<I> createByMetadata(Block block){
			return new BlockStateMapper2ItemMeshDefinition<>(Optional.<Block>of(block), itemstack -> block.getStateFromMeta(itemstack.getItemDamage()));
		}

	}

}
