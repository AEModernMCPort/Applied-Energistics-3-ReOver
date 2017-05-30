package appeng.api.bootstrap;

import appeng.api.definitions.IDefinition;
import com.google.common.reflect.TypeToken;
import net.minecraft.util.ResourceLocation;

import java.util.function.Supplier;

public interface DefinitionFactory {

	<T, D extends IDefinition<T>, B extends IDefinitionBuilder<T, D, B>, I> B definitionBuilder(ResourceLocation registryName, InputHandler<T, I> input);

	abstract class InputHandler<T, I> implements Supplier<I> {

		private final I input;

		public InputHandler(I input){
			this.input = input;
		}

		@Override
		public final I get(){
			return input;
		}

		public final Class<? super T> defType(){
			return new TypeToken<T>(getClass()){}.getRawType();
		}

		public final Class<? super I> inputType(){
			return new TypeToken<I>(getClass()){}.getRawType();
		}

	}

}
