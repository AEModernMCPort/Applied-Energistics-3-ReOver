package appeng.core.lib.bootstrap_olde.components;

import appeng.api.bootstrap.IBootstrapComponent;
import net.minecraftforge.fml.relauncher.Side;

@FunctionalInterface
public interface InitComponent extends IBootstrapComponent {

	@Override
	void init(Side side);

}
