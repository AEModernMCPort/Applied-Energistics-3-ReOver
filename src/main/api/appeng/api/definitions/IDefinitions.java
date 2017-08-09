package appeng.api.definitions;

import appeng.api.definition.IDefinition;
import com.google.common.reflect.TypeToken;
import net.minecraft.util.ResourceLocation;

public interface IDefinitions<T, D extends IDefinition<T>> {

	D get(ResourceLocation identifier);

	default D get(String identifier){
		return get(new ResourceLocation("appliedenergistics3", identifier));
	}

	default <T2 extends T, D2 extends IDefinition<T2>> D2 getUncheked(ResourceLocation identifier){
		return (D2) get(identifier);
	}

	default <T2 extends T, D2 extends IDefinition<T2>> D2 getUncheked(String identifier){
		return getUncheked(new ResourceLocation("appliedenergistics3", identifier));
	}

	default TypeToken<D> definitionType(){
		return new TypeToken<D>(getClass()) {};
	}

}
