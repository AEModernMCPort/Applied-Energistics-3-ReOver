package appeng.core.lib.bootstrap.components;

import appeng.core.lib.bootstrap.IBootstrapComponent;
import net.minecraftforge.fml.relauncher.Side;

@FunctionalInterface
public interface PostInitComponent extends IBootstrapComponent {

	@Override
	void postInit(Side side);
}
