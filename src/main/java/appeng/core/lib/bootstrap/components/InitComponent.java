package appeng.core.lib.bootstrap.components;

import appeng.core.lib.bootstrap.IBootstrapComponent;
import net.minecraftforge.fml.relauncher.Side;

@FunctionalInterface
public interface InitComponent extends IBootstrapComponent {

	@Override
	void init(Side side);

}
