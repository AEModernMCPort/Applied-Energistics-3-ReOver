package appeng.core.core.bootstrap;

import appeng.api.bootstrap.IDefinitionBuilder;
import appeng.api.definitions.IBlockDefinition;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.statemap.IStateMapper;
import net.minecraft.client.resources.IReloadableResourceManager;
import net.minecraft.client.resources.IResourceManagerReloadListener;
import net.minecraftforge.client.model.ModelLoader;

import java.util.function.Supplier;

/**
 * @author Fredi100
 */
public class StateMapperComponent implements IDefinitionBuilder.DefinitionInitializationComponent<Block, IBlockDefinition<Block>> {

	private final Supplier<IStateMapper> stateMapper;

	public StateMapperComponent(Supplier<IStateMapper> stateMapper){
		this.stateMapper = stateMapper;
	}

	@Override
	public void init(IBlockDefinition<Block> def){
		System.out.println("Initializing StateMapperComponent");
		ModelLoader.setCustomStateMapper(def.maybe().get(), stateMapper.get());
		if(stateMapper instanceof IResourceManagerReloadListener)
			((IReloadableResourceManager) Minecraft.getMinecraft().getResourceManager()).registerReloadListener((IResourceManagerReloadListener) stateMapper);
	}
}
