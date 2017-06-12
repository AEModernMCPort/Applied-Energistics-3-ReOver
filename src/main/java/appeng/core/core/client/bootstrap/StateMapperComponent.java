package appeng.core.core.client.bootstrap;

import appeng.api.bootstrap.IDefinitionBuilder;
import appeng.api.definitions.IBlockDefinition;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.statemap.IStateMapper;
import net.minecraft.client.resources.IReloadableResourceManager;
import net.minecraft.client.resources.IResourceManagerReloadListener;
import net.minecraftforge.client.model.ModelLoader;
import scala.Option;
import scala.tools.cmd.Opt;

import java.util.Optional;
import java.util.function.Supplier;

/**
 * @author Fredi100
 */
public class StateMapperComponent implements IDefinitionBuilder.DefinitionInitializationComponent<Block, IBlockDefinition<Block>> {

	private final Supplier<Optional<IStateMapper>> stateMapper;

	public StateMapperComponent(Supplier<Optional<IStateMapper>> stateMapper){
		this.stateMapper = stateMapper;
	}

	@Override
	public void init(IBlockDefinition<Block> def){
		System.out.println(this);
		ModelLoader.setCustomStateMapper(def.maybe().get(), stateMapper.get().get());
		if(stateMapper instanceof IResourceManagerReloadListener)
			((IReloadableResourceManager) Minecraft.getMinecraft().getResourceManager()).registerReloadListener((IResourceManagerReloadListener) stateMapper);
	}
}
