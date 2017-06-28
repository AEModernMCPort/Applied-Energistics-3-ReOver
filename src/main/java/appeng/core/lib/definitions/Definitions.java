package appeng.core.lib.definitions;

import appeng.api.bootstrap.DefinitionFactory;
import appeng.api.definitions.IDefinition;
import appeng.api.definitions.IDefinitions;
import code.elix_x.excomms.reflection.ReflectionHelper;
import com.google.common.collect.ImmutableMap;
import net.minecraft.util.ResourceLocation;

import java.lang.reflect.Field;
import java.util.LinkedList;
import java.util.Queue;

public abstract class Definitions<T, D extends IDefinition<T>> implements IDefinitions<T, D> {

	private Queue<D> dynamicallyCompiled = new LinkedList<>();
	private ImmutableMap<ResourceLocation, D> map;

	public final void init(DefinitionFactory factory){
		assert map == null;
		ImmutableMap.Builder<ResourceLocation, D> builder = ImmutableMap.builder();
		new ReflectionHelper.AClass<>((Class<Definitions>) this.getClass()).getDeclaredFields().stream().filter(field -> field.get().getType().isAssignableFrom(IDefinition.class)).map(aField -> (D) aField.setAccessible(true).get(Definitions.this)).forEach(d -> builder.put(d.identifier(), d));
		dynamicallyCompiled.forEach(def -> builder.put(def.identifier(), def));
		dynamicallyCompiled = null;
		factory.getDefaults(definitionType()).forEach(d -> builder.put(d.identifier(), d));
		map = builder.build();
	}

	protected final void dynamicallyCompiled(D def){
		dynamicallyCompiled.add(def);
	}

	@Override
	public D get(ResourceLocation identifier){
		return map.get(identifier);
	}

}
