package appeng.api.config;

/**
 * Configuration loader loads configuration - {@linkplain FeaturesManager} and custom configuration data stored as a POJO.
 * @param <C> Type of custom configuration class
 */
public interface ConfigurationLoader<C> {

	/**
	 * Loads configs.
	 *
	 * @param clas Custom config POJO class
	 */
	void load(Class<C> clas);

	/**
	 * Retrieves loaded feature manager
	 *
	 * @return loaded features manager
	 */
	FeaturesManager featuresManager();

	/**
	 * Retireves loaded config
	 *
	 * @return loaded configuration
	 */
	C configuration();

	/**
	 * Saves the config and releases resources
	 */
	void save();

}
