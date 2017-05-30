package appeng.api.bootstrap;

import net.minecraftforge.fml.relauncher.Side;

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
