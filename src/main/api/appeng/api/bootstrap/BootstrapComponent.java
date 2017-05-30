package appeng.api.bootstrap;

/**
 * Throw it at {@linkplain BootstrapComponentsHandler} and the methods will be executed in corresponding initialization phases.
 *
 * @author Elix_x
 */
public interface BootstrapComponent {

	default void preInit(){}

	default void init(){}

	default void postInit(){}

	@FunctionalInterface
	interface PreInit extends BootstrapComponent {

		@Override
		void preInit();

	}

	@FunctionalInterface
	interface Init extends BootstrapComponent {

		@Override
		void init();

	}

	@FunctionalInterface
	interface PostInit extends BootstrapComponent {

		@Override
		void postInit();
	}


}
