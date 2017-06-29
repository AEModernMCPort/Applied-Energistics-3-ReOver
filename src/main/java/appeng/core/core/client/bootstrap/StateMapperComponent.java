package appeng.core.core.client.bootstrap;

import appeng.api.bootstrap.IDefinitionBuilder;
import appeng.api.definitions.IBlockDefinition;
import appeng.core.core.AppEngCore;
import appeng.core.core.client.statemap.StateMapperHelper;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.block.statemap.IStateMapper;
import net.minecraftforge.client.model.ModelLoader;

import java.util.Optional;
import java.util.function.Function;

/**
 * @author Fredi100
 */
public class StateMapperComponent<B extends Block> implements IDefinitionBuilder.DefinitionInitializationComponent<B, IBlockDefinition<B>> {

	private final Function<Optional<IStateMapper>, Optional<IStateMapper>> stateMapper;

	public StateMapperComponent(Function<Optional<IStateMapper>, Optional<IStateMapper>> stateMapper){
		this.stateMapper = stateMapper;
	}

	@Override
	public void preInit(IBlockDefinition<B> def){
		AppEngCore.proxy.acceptModelRegisterer(() -> stateMapper.apply(StateMapperHelper.getCustomStateMapper(def.maybe().get())).ifPresent(mapper -> ModelLoader.setCustomStateMapper(def.maybe().get(), mapper)));
	}
}
