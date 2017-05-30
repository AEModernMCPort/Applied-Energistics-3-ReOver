package appeng.core.lib.bootstrap_olde.components;

import appeng.api.bootstrap.InitializationComponent;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.statemap.IStateMapper;
import net.minecraft.client.resources.IReloadableResourceManager;
import net.minecraft.client.resources.IResourceManagerReloadListener;
import net.minecraftforge.client.model.ModelLoader;

/**
 * Registers a custom state mapper for a given block.
 */
public class StateMapperComponent implements InitializationComponent.Init {

	private final Block block;

	private final IStateMapper stateMapper;

	public StateMapperComponent(Block block, IStateMapper stateMapper){
		this.block = block;
		this.stateMapper = stateMapper;
	}

	@Override
	public void init(){
		ModelLoader.setCustomStateMapper(block, stateMapper);
		if(stateMapper instanceof IResourceManagerReloadListener){
			((IReloadableResourceManager) Minecraft.getMinecraft().getResourceManager()).registerReloadListener((IResourceManagerReloadListener) stateMapper);
		}
	}
}
