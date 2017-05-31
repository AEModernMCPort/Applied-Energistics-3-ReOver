package appeng.api.bootstrap;

/**
 * Throw it at {@linkplain InitializationComponentsHandler} and the methods will be executed in corresponding initialization phases.
 *
 * @author Elix_x
 */
public interface InitializationComponent {

	default void preInit(){
	}

	default void init(){
	}

	default void postInit(){
	}

	@FunctionalInterface
	interface PreInit extends InitializationComponent {

		@Override
		void preInit();

	}

	@FunctionalInterface
	interface Init extends InitializationComponent {

		@Override
		void init();

	}

	@FunctionalInterface
	interface PostInit extends InitializationComponent {

		@Override
		void postInit();
	}

}
