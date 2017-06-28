package appeng.core.core.client.bootstrap;

import appeng.api.bootstrap.IDefinitionBuilder;
import appeng.api.definitions.IItemDefinition;
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
public class ItemMeshDefinitionComponent implements IDefinitionBuilder.DefinitionInitializationComponent<Item, IItemDefinition<Item>> {

	private final Supplier<Optional<ItemMeshDefinition>> meshDefinition;

	public ItemMeshDefinitionComponent(@Nonnull Supplier<Optional<ItemMeshDefinition>> meshDefinition){
		this.meshDefinition = meshDefinition;
	}

	@Override
	public void init(IItemDefinition<Item> def){
		meshDefinition.get().ifPresent(itemMeshDefinition -> Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(def.maybe().get(), itemMeshDefinition));
	}

	public static class BlockStateMapper2ItemMeshDefinition extends ItemMeshDefinitionComponent {

		public BlockStateMapper2ItemMeshDefinition(Optional<Block> blocko, Function<ItemStack, IBlockState> stackToState){
			super(() -> blocko.map(block -> {
				Map<IBlockState, ModelResourceLocation> stateModelMap = Minecraft.getMinecraft().getBlockRendererDispatcher().getBlockModelShapes().getBlockStateMapper().getVariants(block);
				return stack -> stateModelMap.get(stackToState.apply(stack));
			}));
		}

		public static BlockStateMapper2ItemMeshDefinition createByMetadata(Block block){
			return new BlockStateMapper2ItemMeshDefinition(Optional.<Block>of(block), itemstack -> block.getStateFromMeta(itemstack.getItemDamage()));
		}

	}

}
