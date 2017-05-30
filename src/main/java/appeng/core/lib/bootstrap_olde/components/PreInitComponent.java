package appeng.core.lib.bootstrap_olde.components;

import appeng.api.bootstrap.IBootstrapComponent;
import net.minecraftforge.fml.relauncher.Side;

@FunctionalInterface
public interface PreInitComponent extends IBootstrapComponent {

	@Override
	void preInit(Side side);

}
