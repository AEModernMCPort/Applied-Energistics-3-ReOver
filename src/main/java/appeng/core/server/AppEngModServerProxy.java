package appeng.core.server;

import appeng.core.AppEngModProxy;

public abstract class AppEngModServerProxy implements AppEngModProxy {

	@Override
	public void moduleLoadingException(String exceptionText, String guiText){
		throw new IllegalStateException(exceptionText);
	}

}
