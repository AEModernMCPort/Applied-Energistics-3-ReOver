package appeng.core.lib.config;

import appeng.api.config.FeaturesManager;
import com.google.common.collect.ImmutableMap;
import net.minecraft.util.ResourceLocation;

import java.util.HashMap;
import java.util.Map;

public enum GlobalFeaturesManager implements FeaturesManager {

	INSTANCE;

	private Map<String, FeaturesManager> managers = new HashMap<>();

	public void register(String domain, FeaturesManager manager){
		if(manager == this) throw new IllegalArgumentException("Cannot register global features manager to itself!");
		managers.put(domain, manager);
	}

	public FeaturesManager get(String domain){
		return managers.get(domain);
	}

	@Override
	public FeaturesManager addFeature(ResourceLocation feature, boolean def, ResourceLocation... deps){
		return managers.get(feature.getNamespace()).addFeature(feature, def, deps);
	}

	@Override
	public boolean isEnabled(ResourceLocation feature, boolean def){
		return feature == null || managers.get(feature.getNamespace()).isEnabled(feature, def);
	}

	@Override
	public FeaturesManager addDependencies(ResourceLocation feature, ResourceLocation... deps) throws IllegalStateException{
		return managers.get(feature.getNamespace()).addDependencies(feature, deps);
	}

	@Override
	public Map<ResourceLocation, Boolean> getAllFeatures(){
		return ImmutableMap.of();
	}
}
