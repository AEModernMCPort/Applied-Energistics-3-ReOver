package appeng.api.bootstrap;

import com.sun.istack.internal.NotNull;

public interface SidedICHProxy {

	@NotNull
	InitializationComponentsHandler client();

	@NotNull
	InitializationComponentsHandler server();

}
