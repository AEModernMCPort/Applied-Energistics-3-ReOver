package appeng.api.bootstrap;

import net.minecraftforge.fml.relauncher.Side;

/**
 * Bootstrap components can be registered to take part in the various initialization phases of Forge.
 */
public interface IBootstrapComponent {

	default void preInit(){}

	default void init(){}

	default void postInit(){}
	
}
