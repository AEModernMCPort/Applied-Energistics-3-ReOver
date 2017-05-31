package appeng.api.bootstrap;

import appeng.api.definitions.IDefinition;
import com.google.common.reflect.TypeToken;
import com.sun.istack.internal.Nullable;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;

import java.util.function.Supplier;

public interface DefinitionFactory {

	/**
	 * Retrieve initialization handler for given side, or common handler if side is <tt>null</tt>.
	 *
	 * @param side logical side of the handler, or <tt>null</tt> for common handler
	 * @return initialization handler for given side
	 */
	InitializationComponentsHandler initializationHandler(@Nullable Side side);

	<T, D extends IDefinition<T>, B extends IDefinitionBuilder, I> B definitionBuilder(ResourceLocation registryName, InputHandler<T, I> input);

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
			return new TypeToken<T>(getClass()) {

			}.getRawType();
		}

		public final Class<? super I> inputType(){
			return new TypeToken<I>(getClass()) {

			}.getRawType();
		}

	}

}
