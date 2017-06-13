package appeng.core.proxy;

public class AppEngServerProxy implements AppEngProxy {

	@Override
	public void moduleLoadingException(String exceptionText, String guiText){
		throw new IllegalStateException(exceptionText);
	}

}
