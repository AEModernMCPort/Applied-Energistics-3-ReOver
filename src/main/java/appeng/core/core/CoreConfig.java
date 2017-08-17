package appeng.core.core;

import appeng.api.bootstrap.InitializationComponent;
import appeng.api.config.ConfigCompilable;
import appeng.core.core.crafting.ion.IonCraftingConfig;

public class CoreConfig implements ConfigCompilable, InitializationComponent.Init {

	public IonCraftingConfig ionCraftingConfig = new IonCraftingConfig();

	@Override
	public void compile(){
		ionCraftingConfig.compile();
	}

	@Override
	public void init(){
		ionCraftingConfig.init();
	}

	@Override
	public void decompile(){
		ionCraftingConfig.decompile();
	}

	@Override
	public boolean equals(Object o){
		if(this == o) return true;
		if(!(o instanceof CoreConfig)) return false;

		CoreConfig that = (CoreConfig) o;

		return ionCraftingConfig.equals(that.ionCraftingConfig);
	}

	@Override
	public int hashCode(){
		return ionCraftingConfig.hashCode();
	}
}