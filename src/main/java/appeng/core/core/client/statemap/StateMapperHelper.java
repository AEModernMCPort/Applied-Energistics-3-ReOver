package appeng.core.core.client.statemap;

import code.elix_x.excomms.reflection.ReflectionHelper;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.block.statemap.IStateMapper;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.registries.IRegistryDelegate;

import java.util.Map;
import java.util.Optional;

public class StateMapperHelper {

	/*private static final ReflectionHelper.AField<BlockStateMapper, Map<Block, IStateMapper>> blockStateMap = new ReflectionHelper.AClass<>(BlockStateMapper.class).<Map<Block, IStateMapper>>getDeclaredField("blockStateMap", "").setAccessible(true);
	private static final ReflectionHelper.AField<Minecraft, ModelManager> modelManager = new ReflectionHelper.AClass<>(Minecraft.class).<ModelManager>getDeclaredField("modelManager").setAccessible(true);


	public static Map<Block, IStateMapper> getBlockStateMap(BlockStateMapper stateMapper){
		return blockStateMap.get(stateMapper);
	}

	public static Map<Block, IStateMapper> getBlockStateMap(){
		return getBlockStateMap(modelManager.get(Minecraft.getMinecraft()).getBlockModelShapes().getBlockStateMapper());
	}

	public static IStateMapper getStateMapper(Block block){
		return getBlockStateMap().get(block);
	}*/

	private static final ReflectionHelper.AField<ModelLoader, Map<IRegistryDelegate<Block>, IStateMapper>> customStateMappers = new ReflectionHelper.AClass<>(ModelLoader.class).<Map<IRegistryDelegate<Block>, IStateMapper>>getDeclaredField("customStateMappers").orElseThrow(() -> new IllegalArgumentException("Could not reflect necessary fields for state mapper")).setAccessible(true);

	public static Optional<IStateMapper> getCustomStateMapper(Block block){
		return customStateMappers.get(null).getOpt().map(sm -> sm.get(block.delegate));
	}

}
