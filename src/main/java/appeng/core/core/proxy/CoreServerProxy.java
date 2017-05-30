package appeng.core.core.proxy;

import appeng.api.bootstrap.InitializationComponentsHandler;
import net.minecraftforge.fml.relauncher.Side;

public class CoreServerProxy extends CoreProxy {

	public CoreServerProxy(){
		super(Side.CLIENT);
	}
}
