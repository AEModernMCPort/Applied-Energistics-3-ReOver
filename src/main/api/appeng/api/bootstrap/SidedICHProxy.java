package appeng.api.bootstrap;

import javax.annotation.NotNull;

public interface SidedICHProxy {

	@NotNull
	InitializationComponentsHandler client();

	@NotNull
	InitializationComponentsHandler server();

}
