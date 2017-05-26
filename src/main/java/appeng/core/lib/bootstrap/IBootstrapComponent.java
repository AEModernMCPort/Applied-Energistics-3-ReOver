
package appeng.core.lib.bootstrap;


import net.minecraftforge.fml.relauncher.Side;


/**
 * Bootstrap components can be registered to take part in the various initialization phases of Forge.
 */
public interface IBootstrapComponent
{

	default void preInit( Side side )
	{
	}

	default void init( Side side )
	{
	}

	default void postInit( Side side )
	{
	}
}
