package appeng.api.bootstrap;

import java.util.function.Consumer;

/**
 * Handles initialization components. Throw components at it and it will do the rest.
 *
 * @author Elix_x
 */
public interface InitializationComponentsHandler extends Consumer<InitializationComponent> {

	default void acceptPreInit(InitializationComponent.PreInit component){
		accept(component);
	}

	default void acceptInit(InitializationComponent.Init component){
		accept(component);
	}

	default void acceptPostInit(InitializationComponent.PostInit component){
		accept(component);
	}

}
