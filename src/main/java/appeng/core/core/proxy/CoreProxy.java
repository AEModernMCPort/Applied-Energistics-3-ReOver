package appeng.core.core.proxy;

import appeng.core.lib.proxy.BaseProxy;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.fml.relauncher.Side;

import java.util.function.Consumer;

public class CoreProxy extends BaseProxy {

	public CoreProxy(Side side){
		super(side);
	}

	public void acceptModelRegisterer(Runnable registerer){};

	public void acceptModelCustomizer(Consumer<ModelBakeEvent> customizer){};
}
