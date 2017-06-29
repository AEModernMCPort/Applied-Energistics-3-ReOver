package appeng.core.core.client.bootstrap;

import appeng.api.bootstrap.IDefinitionBuilder;
import appeng.api.definitions.IBlockDefinition;
import appeng.core.core.AppEngCore;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.statemap.IStateMapper;
import net.minecraft.client.resources.IReloadableResourceManager;
import net.minecraft.client.resources.IResourceManagerReloadListener;
import net.minecraftforge.client.model.ModelLoader;

import java.util.Optional;
import java.util.function.Supplier;

/**
 * @author Fredi100
 */
public class StateMapperComponent<B extends Block> implements IDefinitionBuilder.DefinitionInitializationComponent<B, IBlockDefinition<B>> {

	private final Supplier<Optional<IStateMapper>> stateMapper;

	public StateMapperComponent(Supplier<Optional<IStateMapper>> stateMapper){
		this.stateMapper = stateMapper;
	}

	@Override
	public void preInit(IBlockDefinition<B> def){
		AppEngCore.proxy.acceptModelRegisterer(() -> ModelLoader.setCustomStateMapper(def.maybe().get(), stateMapper.get().get()));
		if(stateMapper instanceof IResourceManagerReloadListener) ((IReloadableResourceManager) Minecraft.getMinecraft().getResourceManager()).registerReloadListener((IResourceManagerReloadListener) stateMapper);
	}
}
