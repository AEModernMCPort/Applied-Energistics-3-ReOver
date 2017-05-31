package appeng.core.lib.config;

import appeng.api.config.ConfigurationLoader;
import appeng.api.config.FeaturesManager;
import appeng.core.AppEng;

import java.io.File;
import java.io.IOException;

public abstract class ConfigLoader<C> implements ConfigurationLoader<C> {

	protected final String module;
	protected final String extension;
	protected FeaturesManager featuresManager;
	protected C config;

	public ConfigLoader(String module, String extension){
		this.module = module;
		this.extension = extension;
	}

	protected File featuresFile(){
		return new File(AppEng.instance().getConfigDirectory(), module + ".features." + extension);
	}

	protected File configFile(){
		return new File(AppEng.instance().getConfigDirectory(), module + ".config." + extension);
	}
	
	@Override
	public void load(Class<C> clas){
		try{
			featuresFile().createNewFile();
			configFile().createNewFile();
		} catch(IOException e){
			//TODO 1.11.2-ReOver :(
		}
		featuresManager = GlobalFeaturesManager.INSTANCE.get(module);
		if(featuresManager == null) GlobalFeaturesManager.INSTANCE.register(module, featuresManager = new ModuleFeaturesManager(module));
	}

	@Override
	public FeaturesManager featuresManager(){
		return featuresManager;
	}

	@Override
	public C configuration(){
		return config;
	}

}
