package appeng.api.bootstrap;

import java.util.function.Consumer;

/**
 * Handles bootstrap components. Throw components at it and it will do the rest.
 *
 * @author Elix_x
 */
public interface BootstrapComponentsHandler extends Consumer<BootstrapComponent> {

	default void acceptPreInit(BootstrapComponent.PreInit component){
		accept(component);
	}

	default void acceptInit(BootstrapComponent.Init component){
		accept(component);
	}

	default void acceptPostInit(BootstrapComponent.PostInit component){
		accept(component);
	}

}
