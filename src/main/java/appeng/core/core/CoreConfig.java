package appeng.core.core;

import appeng.api.bootstrap.InitializationComponent;
import appeng.api.config.ConfigCompilable;
import appeng.core.core.crafting.ion.IonCraftingConfig;

public class CoreConfig implements ConfigCompilable, InitializationComponent.Init {

	public IonCraftingConfig ionCraftingConfig = new IonCraftingConfig();

	@Override
	public void compile(){

	}

	@Override
	public void init(){

	}

	@Override
	public void decompile(){

	}
}