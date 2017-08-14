package appeng.core.lib.bootstrap;

import appeng.api.bootstrap.DefinitionBuilderSupplier;
import appeng.api.bootstrap.IDefinitionBuilder;
import appeng.api.bootstrap.InitializationComponentsHandler;
import appeng.api.bootstrap.SidedICHProxy;
import appeng.api.definition.IDefinition;
import com.google.common.collect.ImmutableMap;
import com.google.common.reflect.TypeToken;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public class DefinitionFactory implements appeng.api.bootstrap.DefinitionFactory {

	private InitializationComponentsHandler commonInitHandler;
	private SidedICHProxy sidedInitHandlers;
	private ImmutableMap<Pair<Class, Class>, DefinitionBuilderSupplier> definitionBuilderSuppliers;
	private List<IDefinition> defaults = new ArrayList<>();

	public DefinitionFactory(InitializationComponentsHandler commonInitHandler, SidedICHProxy sidedInitHandlers, Map<Pair<Class, Class>, DefinitionBuilderSupplier> definitionBuilderSuppliers){
		this.commonInitHandler = commonInitHandler;
		this.sidedInitHandlers = sidedInitHandlers;
		this.definitionBuilderSuppliers = ImmutableMap.copyOf(definitionBuilderSuppliers);
	}

	@Override
	public InitializationComponentsHandler initializationHandler(Side side){
		return side == Side.CLIENT ? sidedInitHandlers.client() : side == Side.SERVER ? sidedInitHandlers.server() : commonInitHandler;
	}

	private <T, D extends IDefinition<T>, B extends IDefinitionBuilder<T, D, B>, I> DefinitionBuilderSupplier<T, D, B, I> get(Pair<Class, Class> key){
		return definitionBuilderSuppliers.get(key);
	}

	@Override
	public <T, D extends IDefinition<T>, B extends IDefinitionBuilder, I> B definitionBuilder(ResourceLocation registryName, InputHandler<? super T, ? super I> input){
		return this.<T, D, B, I>get(new ImmutablePair<>(input.defType(), input.inputType())).apply(this, registryName, (I) input.get());
	}

	@Override
	public <T, D extends IDefinition<T>> void addDefault(D def){
		defaults.add(def);
	}

	@Override
	public <T, D extends IDefinition<T>> Stream<D> getDefaults(TypeToken<D> type){
		return (Stream) defaults.stream().filter(def -> type.getRawType().isInstance(def));
	}
}
