package appeng.api.bootstrap;

import appeng.api.definition.IDefinition;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;

import javax.annotation.Nullable;
import java.util.function.Consumer;

public interface IDefinitionBuilder<T, D extends IDefinition<T>, B extends IDefinitionBuilder<T, D, B>> {

	B setFeature(ResourceLocation feature);

	B setEnabledByDefault(boolean enabled);

	B build(Consumer<D> callback);

	<I extends DefinitionInitializationComponent<T, D>> B initializationComponent(@Nullable Side side, I init);

	default <I extends DefinitionInitializationComponent.PreInit<T, D>> B preinitComponent(@Nullable Side side, I init){
		return initializationComponent(side, init);
	}
	default <I extends DefinitionInitializationComponent.Init<T, D>> B initComponent(@Nullable Side side, I init){
		return initializationComponent(side, init);
	}
	default <I extends DefinitionInitializationComponent.PostInit<T, D>> B postinitComponent(@Nullable Side side, I init){
		return initializationComponent(side, init);
	}

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
