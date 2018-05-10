package appeng.core.lib.config;

import appeng.api.config.ConfigurationLoader;
import appeng.api.config.FeaturesManager;
import appeng.core.AppEng;
import net.minecraft.util.ResourceLocation;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public abstract class ConfigLoader<C> implements ConfigurationLoader<C> {

	public static Supplier<IllegalArgumentException> configInstantiationFailed(Class<?> config, String cause){
		return () -> new IllegalArgumentException(String.format("Could not instantiate config (%s) - %s", config, cause));
	}

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
	public void load(Class<C> clas) throws IOException{
		featuresFile().createNewFile();
		configFile().createNewFile();

		featuresManager = GlobalFeaturesManager.INSTANCE.get(module);
		if(featuresManager == null)
			GlobalFeaturesManager.INSTANCE.register(module, featuresManager = new ModuleFeaturesManager(module));
	}

	@Override
	public FeaturesManager featuresManager(){
		return featuresManager;
	}

	@Override
	public C configuration(){
		return config;
	}

	protected HierarchicalFeatures managerToHierarchical(){
		HierarchicalFeatures features = new HierarchicalFeatures();
		features.enabled = true;
		featuresManager.getAllFeatures().forEach((location, enabled) -> features.getOrCreateLocate(location.getResourcePath()).enabled = enabled);
		return features;
	}

	protected void hierarchicalToManager(HierarchicalFeatures features){
		if(features == null) return;
		Map<ResourceLocation, Boolean> allFeatures = featuresManager.getAllFeatures();
		if(features.subfeatures != null)
			features.subfeatures.forEach((next, hierarchicalFeatures) -> hierarchicalToManager(next, hierarchicalFeatures, allFeatures));
	}

	protected void hierarchicalToManager(String path, HierarchicalFeatures features, Map<ResourceLocation, Boolean> allFeatures){
		if(features.subfeatures != null)
			features.subfeatures.forEach((next, hierarchicalFeatures) -> hierarchicalToManager(String.join("/", path, next), hierarchicalFeatures, allFeatures));
		allFeatures.put(new ResourceLocation(module, path), features.enabled);
	}

	public static class HierarchicalFeatures {

		public boolean enabled;
		public Map<String, HierarchicalFeatures> subfeatures;

		public HierarchicalFeatures(){
		}

		public HierarchicalFeatures(boolean enabled){
			this.enabled = enabled;
		}

		public HierarchicalFeatures getOrCreate(String loc){
			if(subfeatures == null) subfeatures = new HashMap<>();
			HierarchicalFeatures features = subfeatures.get(loc);
			if(features == null) subfeatures.put(loc, features = new HierarchicalFeatures());
			return features;
		}

		public HierarchicalFeatures getOrCreateLocate(String loc){
			if(loc.contains("/"))
				return getOrCreate(loc.substring(0, loc.indexOf('/'))).getOrCreate(loc.substring(loc.indexOf('/') + 1));
			else return getOrCreate(loc);
		}

	}

}
