package appeng.core.lib.bootstrap;

import appeng.api.bootstrap.DefinitionBuilder;
import appeng.api.definitions.IDefinition;
import com.google.common.collect.ImmutableMap;
import com.google.common.reflect.TypeToken;
import javafx.util.Pair;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.lang3.tuple.ImmutablePair;

import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Supplier;

public class DefinitionFactory implements appeng.api.bootstrap.DefinitionFactory {

	private ImmutableMap<Pair<Class, Class>, BiFunction> definitionBuilderSuppliers;

	public DefinitionFactory(Map<Pair<Class, Class>, BiFunction<ResourceLocation, ?, DefinitionBuilder>> definitionBuilderSuppliers){
		this.definitionBuilderSuppliers = ImmutableMap.copyOf(definitionBuilderSuppliers);
	}

	private <T, D extends IDefinition<T>, B extends DefinitionBuilder<T, D, B>, I> BiFunction<ResourceLocation, I, B> getBuilderProvider(){
		return definitionBuilderSuppliers.get(new ImmutablePair<>(new TypeToken<T>() {}.getRawType(), new TypeToken<I>(){}.getRawType()));
	}

	@Override
	public <T, D extends IDefinition<T>, B extends DefinitionBuilder<T, D, B>, I> B definitionBuilder(ResourceLocation registryName, I input){
		return (B) getBuilderProvider().apply(registryName, input);
	}
}
