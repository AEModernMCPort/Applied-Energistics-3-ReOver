package appeng.core.lib.bootstrap;

import appeng.api.bootstrap.IDefinitionBuilder;
import appeng.api.definitions.IDefinition;
import com.google.common.collect.ImmutableMap;
import com.google.common.reflect.TypeToken;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Map;
import java.util.function.BiFunction;

public class DefinitionFactory implements appeng.api.bootstrap.DefinitionFactory {

	private ImmutableMap<Pair<Class, Class>, BiFunction> definitionBuilderSuppliers;

	public DefinitionFactory(Map<Pair<Class, Class>, BiFunction> definitionBuilderSuppliers){
		this.definitionBuilderSuppliers = ImmutableMap.copyOf(definitionBuilderSuppliers);
	}

	private <T, D extends IDefinition<T>, B extends IDefinitionBuilder<T, D, B>, I> BiFunction<ResourceLocation, I, B> get(Pair<Class, Class> key){
		return definitionBuilderSuppliers.get(key);
	}

	@Override
	public <T, D extends IDefinition<T>, B extends IDefinitionBuilder<T, D, B>, I> B definitionBuilder(ResourceLocation registryName, InputHandler<T, I> input){
		return this.<T, D, B, I>get(new ImmutablePair<>(input.defType(), input.inputType())).apply(registryName, input.get());
	}
}
