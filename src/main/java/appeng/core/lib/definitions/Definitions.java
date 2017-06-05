package appeng.core.lib.definitions;

import appeng.api.definitions.IDefinition;
import appeng.api.definitions.IDefinitions;
import com.google.common.collect.ImmutableMap;
import net.minecraft.util.ResourceLocation;

import java.lang.reflect.Field;
import java.util.stream.Stream;

public class Definitions<T, D extends IDefinition<T>> implements IDefinitions<T, D> {

	private ImmutableMap<ResourceLocation, D> map;

	public final void init(Stream<D> defaults){
		assert map == null;
		ImmutableMap.Builder builder = ImmutableMap.builder();
		for(Field field : this.getClass().getDeclaredFields()){
			if(field.getType().isAssignableFrom(IDefinition.class)){
				try{
					field.setAccessible(true);
					IDefinition<T> def = (IDefinition<T>) field.get(this);
					builder.put(def.identifier(), def);
				} catch(ReflectiveOperationException e){
					// ;(
				}
			}
		}
		defaults.forEach(d -> builder.put(d.identifier(), d));
		map = builder.build();
	}

	@Override
	public D get(ResourceLocation identifier){
		return map.get(identifier);
	}

}
