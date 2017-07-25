package appeng.api.module;

import appeng.api.bootstrap.*;
import appeng.api.config.ConfigurationLoader;
import appeng.api.config.FeaturesManager;
import appeng.api.definition.IDefinition;
import net.minecraftforge.fml.common.event.FMLInterModComms;

import java.util.function.Function;

/**
 * Parent class of all AE events sent to modules
 *
 * @author Elix_x
 */
public interface AEStateEvent {

	public interface AEBootstrapEvent {

		/**
		 * Registers configuration loader provider for the given format.
		 *
		 * @param format     a format (just an identifier to differentiate different loaders)
		 * @param clProvider provides {@linkplain ConfigurationLoader} based on module's name, all calls with the same module name should return the same {@linkplain ConfigurationLoader} instance
		 */
		void registerConfigurationLoaderProvider(String format, Function<String, ConfigurationLoader> clProvider);

		/**
		 * Registers definition builder supplier for new input/definition type pair.
		 *
		 * @param defType         Definition type
		 * @param inputType       Input type
		 * @param builderSupplier Builder supplier transforming input and regsitry name into definition builder
		 * @param <T>             Type held by definition
		 * @param <D>             Type of definition
		 * @param <B>             Definition Builder
		 * @param <I>             Definition builder input type
		 */
		<T, D extends IDefinition<T>, B extends IDefinitionBuilder<T, D, B>, I> void registerDefinitionBuilderSupplier(Class<T> defType, Class<I> inputType, DefinitionBuilderSupplier<T, D, B, I> builderSupplier);

	}

	public interface AEPreInitializationEvent {

		<C> ConfigurationLoader<C> configurationLoader();

		void registerCustomFeatureManager(FeaturesManager manager);

		FeaturesManager globalFeaturesManager();

		InitializationComponentsHandler defaultICHandler();

		DefinitionFactory factory(InitializationComponentsHandler commonInitHandler, SidedICHProxy sidedInitHandler);

	}

	public interface AEInitializationEvent {

	}

	public interface AEPostInitializationEvent {

	}

	public interface AELoadCompleteEvent {

	}

	/**
	 * Fired to the module when {@linkplain FMLInterModComms.IMCMessage} is fired to AE, with {@linkplain FMLInterModComms.IMCMessage#key} representing name of the module.
	 *
	 * @author Elix_x
	 */
	public interface ModuleIMCMessageEvent {

		FMLInterModComms.IMCMessage getMessage();

		<T> T getValue();

	}

}
