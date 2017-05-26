
package appeng.core.lib.bootstrap.components;


import net.minecraftforge.fml.relauncher.Side;

import appeng.core.lib.bootstrap.IBootstrapComponent;


@FunctionalInterface
public interface PostInitComponent extends IBootstrapComponent
{

	@Override
	void postInit( Side side );
}
