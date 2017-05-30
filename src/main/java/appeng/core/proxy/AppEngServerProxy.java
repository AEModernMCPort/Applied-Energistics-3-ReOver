package appeng.core.proxy;

public abstract class AppEngServerProxy implements AppEngProxy {

	@Override
	public void moduleLoadingException(String exceptionText, String guiText){
		throw new IllegalStateException(exceptionText);
	}

}
