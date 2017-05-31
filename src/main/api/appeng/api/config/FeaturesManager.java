package appeng.api.config;

import net.minecraft.util.ResourceLocation;

import java.util.Map;

/**
 * This class manages features availability - only enable/disable per individual feature. Features are represented with {@linkplain ResourceLocation}, {@linkplain ResourceLocation#resourceDomain} being the module id/name and {@linkplain ResourceLocation#resourcePath} being the feature path - file like path (separated with <tt>/</tt>) sequence of feature hierarchy.<br>
 * A feature can have additional dependencies, but its' parent is always one of dependencies.<br>
 * A feature is enabled if and only if all the features it depends on are enabled AND this feature itself is enabled.
 */
public interface FeaturesManager {

	/**
	 * Adds a feature to the features manager without returning its' value.
	 *
	 * @param feature The feature
	 * @param def default value
	 * @param deps Additional feature dependencies
	 * @return <tt>this</tt>
	 */
	FeaturesManager addFeature(ResourceLocation feature, boolean def, ResourceLocation... deps);

	/**
	 * A feature is enabled if and only if all the features it depends on are enabled AND this feature itself is enabled.
	 *
	 * @param feature The feature
	 * @param def default value
	 * @return Whether the feature is enabled
	 */
	boolean isEnabled(ResourceLocation feature, boolean def);

	/**
	 * Adds dependencies to the feature.
	 *
	 * @param feature The feature
	 * @param deps Additional feature dependencies
	 * @return <tt>this</tt>
	 * @throws IllegalStateException if addition of given dependencies creates circularity
	 */
	FeaturesManager addDependencies(ResourceLocation feature, ResourceLocation... deps) throws IllegalStateException;

	/**
	 * <b>Mutable</b> map of al features required for saving & loading to/from config
	 *
	 * @return all the features this features manager holds
	 */
	Map<ResourceLocation, Boolean> getAllFeatures();

}
