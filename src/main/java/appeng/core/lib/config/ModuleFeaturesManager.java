package appeng.core.lib.config;

import appeng.api.config.FeaturesManager;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.util.ResourceLocation;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class ModuleFeaturesManager implements FeaturesManager {

	public static ResourceLocation parent(ResourceLocation feature){
		return feature.getPath().contains("/") ? new ResourceLocation(feature.getNamespace(), feature.getPath().substring(0, feature.getPath().lastIndexOf('/'))) : null;
	}

	private final String domain;
	private Map<ResourceLocation, Boolean> availability = new HashMap<>();
	private Multimap<ResourceLocation, ResourceLocation> dependencies = HashMultimap.create();

	public ModuleFeaturesManager(String domain){
		this.domain = domain;
	}

	private boolean getOrSetToDefault(ResourceLocation feature, boolean def){
		Boolean b = availability.get(feature);
		if(b == null) availability.put(feature, b = def);
		return b;
	}

	@Override
	public FeaturesManager addFeature(ResourceLocation feature, boolean def, ResourceLocation... deps){
		if(domain.equals(feature.getNamespace())){
			getOrSetToDefault(feature, def);
			return addDependencies(feature, deps);
		} else return GlobalFeaturesManager.INSTANCE.addFeature(feature, def, deps);
	}

	@Override
	public boolean isEnabled(ResourceLocation feature, boolean def){
		if(domain.equals(feature.getNamespace()))
			return feature == null || (getOrSetToDefault(feature, def) && getOrSetToDefault(parent(feature), def) && dependencies.get(feature).stream().map(location -> isEnabled(location, def)).allMatch(enabled -> enabled == true));
		else return GlobalFeaturesManager.INSTANCE.isEnabled(feature, def);
	}

	@Override
	public FeaturesManager addDependencies(ResourceLocation feature, ResourceLocation... deps) throws IllegalStateException{
		if(domain.equals(feature.getNamespace())){
			//TODO 1.11.2-ReOver - Check for circularities!
			dependencies.putAll(feature, Arrays.asList(deps));
			return this;
		} else return GlobalFeaturesManager.INSTANCE.addDependencies(feature, deps);
	}

	@Override
	public Map<ResourceLocation, Boolean> getAllFeatures(){
		return availability;
	}

}
