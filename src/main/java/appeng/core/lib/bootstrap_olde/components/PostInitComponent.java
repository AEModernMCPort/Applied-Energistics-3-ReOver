package appeng.core.lib.bootstrap_olde.components;

import appeng.api.bootstrap.IBootstrapComponent;
import net.minecraftforge.fml.relauncher.Side;

@FunctionalInterface
public interface PostInitComponent extends IBootstrapComponent {

	@Override
	void postInit(Side side);
}
