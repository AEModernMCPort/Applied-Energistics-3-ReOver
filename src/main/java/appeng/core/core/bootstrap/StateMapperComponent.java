package appeng.core.core.bootstrap;

import appeng.api.bootstrap.IDefinitionBuilder;
import appeng.api.definitions.IBlockDefinition;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.statemap.IStateMapper;
import net.minecraft.client.resources.IReloadableResourceManager;
import net.minecraft.client.resources.IResourceManagerReloadListener;
import net.minecraftforge.client.model.ModelLoader;

/**
 * @author Fredi100
 */
public class StateMapperComponent implements IDefinitionBuilder.DefinitionInitializationComponent<Block, IBlockDefinition<Block>> {

	private final IStateMapper stateMapper;

	public StateMapperComponent(IStateMapper stateMapper){
		this.stateMapper = stateMapper;
	}

	@Override
	public void init(IBlockDefinition<Block> def){
		ModelLoader.setCustomStateMapper(def.maybe().get(), stateMapper);
		if(stateMapper instanceof IResourceManagerReloadListener)
			((IReloadableResourceManager) Minecraft.getMinecraft().getResourceManager()).registerReloadListener((IResourceManagerReloadListener) stateMapper);
	}
}
