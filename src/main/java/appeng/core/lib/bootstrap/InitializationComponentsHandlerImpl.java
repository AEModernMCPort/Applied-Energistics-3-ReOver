package appeng.core.lib.bootstrap;

import appeng.api.bootstrap.InitializationComponent;
import appeng.api.bootstrap.InitializationComponentsHandler;

import java.util.ArrayList;
import java.util.List;

public class InitializationComponentsHandlerImpl implements InitializationComponentsHandler {

	private List<InitializationComponent> components = new ArrayList<>();

	@Override
	public void accept(InitializationComponent component){
		components.add(component);
	}

	@Override
	public void preInit(){
		components.forEach(InitializationComponent::preInit);
	}

	@Override
	public void init(){
		components.forEach(InitializationComponent::init);
	}

	@Override
	public void postInit(){
		components.forEach(InitializationComponent::postInit);
	}

}
