package appeng.core.lib.module;

import appeng.api.bootstrap.InitializationComponent;
import appeng.api.bootstrap.InitializationComponentsHandler;
import net.minecraftforge.fml.relauncher.Side;

public abstract class SidedICHProxy implements appeng.api.bootstrap.SidedICHProxy {

	private final Side side;
	private final InitializationComponentsHandler initCHandler;

	public SidedICHProxy(Side side, InitializationComponentsHandler initCHandler){
		this.side = side;
		this.initCHandler = initCHandler;
	}

	@Override
	public InitializationComponentsHandler client(){
		return side == Side.CLIENT ? initCHandler : EmptyInitializationComponentsHandler.INSTANCE;
	}

	@Override
	public InitializationComponentsHandler server(){
		return side == Side.SERVER ? initCHandler : EmptyInitializationComponentsHandler.INSTANCE;
	}

	public static enum EmptyInitializationComponentsHandler implements InitializationComponentsHandler {

		INSTANCE;

		@Override
		public void accept(InitializationComponent component){

		}

		@Override
		public void preInit(){

		}

		@Override
		public void init(){

		}

		@Override
		public void postInit(){

		}

	}
}
