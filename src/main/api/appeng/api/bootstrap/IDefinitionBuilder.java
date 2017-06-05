package appeng.api.bootstrap;

import appeng.api.definitions.IDefinition;
import net.minecraftforge.fml.relauncher.Side;

import javax.annotation.Nullable;
import java.util.function.Consumer;

public interface IDefinitionBuilder<T, D extends IDefinition<T>, B extends IDefinitionBuilder<T, D, B>> {

	B build(Consumer<D> callback);

	<I extends DefinitionInitializationComponent<T, D>> B initializationComponent(@Nullable Side side, I init);

	D build();

	interface DefinitionInitializationComponent<T, D extends IDefinition<T>> {

		default void preInit(D def){
		}

		default void init(D def){
		}

		default void postInit(D def){
		}

		@FunctionalInterface
		interface PreInit<T, D extends IDefinition<T>> extends DefinitionInitializationComponent<T, D> {

			@Override
			void preInit(D def);

		}

		@FunctionalInterface
		interface Init<T, D extends IDefinition<T>> extends DefinitionInitializationComponent<T, D> {

			@Override
			void init(D def);

		}

		@FunctionalInterface
		interface PostInit<T, D extends IDefinition<T>> extends DefinitionInitializationComponent<T, D> {

			@Override
			void postInit(D def);
		}

	}


}
