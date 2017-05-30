package appeng.core.lib.proxy;

import appeng.api.bootstrap.InitializationComponentsHandler;
import appeng.api.module.AEStateEvent;
import appeng.core.lib.bootstrap.InitializationComponentsHandlerImpl;
import appeng.core.lib.module.SidedICHProxy;
import net.minecraftforge.fml.relauncher.Side;

public abstract class BaseProxy extends SidedICHProxy {

	public BaseProxy(Side side, InitializationComponentsHandler initCHandler){
		super(side, initCHandler);
	}

	public BaseProxy(Side side){
		this(side, new InitializationComponentsHandlerImpl());
	}

	public void preInit(AEStateEvent.AEPreInitializationEvent event){
		initCHandler.preInit();
	}

	public void init(AEStateEvent.AEInitializationEvent event){
		initCHandler.init();
	}

	public void postInit(AEStateEvent.AEPostInitializationEvent event){
		initCHandler.postInit();
	}

}
