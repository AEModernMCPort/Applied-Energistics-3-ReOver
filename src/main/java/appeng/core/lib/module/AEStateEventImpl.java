package appeng.core.lib.module;

import appeng.api.bootstrap.DefinitionBuilderSupplier;
import appeng.api.bootstrap.IDefinitionBuilder;
import appeng.api.bootstrap.InitializationComponentsHandler;
import appeng.api.bootstrap.SidedICHProxy;
import appeng.api.config.ConfigurationLoader;
import appeng.api.config.FeaturesManager;
import appeng.api.definition.IDefinition;
import appeng.api.module.AEStateEvent;
import appeng.core.AppEng;
import appeng.core.lib.bootstrap.DefinitionFactory;
import appeng.core.lib.bootstrap.InitializationComponentsHandlerImpl;
import appeng.core.lib.config.GlobalFeaturesManager;
import net.minecraft.command.ICommand;
import net.minecraftforge.fml.common.event.FMLInterModComms;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nonnull;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Consumer;

/**
 * Implementations of {@linkplain AEStateEvent}s.
 *
 * @author Elix_x
 */
public class AEStateEventImpl implements AEStateEvent {

	public static class AEBootstrapEventImpl extends AEStateEventImpl implements AEStateEvent.AEBootstrapEvent {

		private Map<String, BiFunction<String, Boolean, ConfigurationLoader>> configurationLoaderProviders;
		private Map<Pair<Class, Class>, DefinitionBuilderSupplier> definitionBuilderSuppliers;

		public AEBootstrapEventImpl(Map<String, BiFunction<String, Boolean, ConfigurationLoader>> configurationLoaderProviders, Map<Pair<Class, Class>, DefinitionBuilderSupplier> definitionBuilderSuppliers){
			this.configurationLoaderProviders = configurationLoaderProviders;
			this.definitionBuilderSuppliers = definitionBuilderSuppliers;
		}

		@Override
		public void registerConfigurationLoaderProvider(@Nonnull String format, @Nonnull BiFunction<String, Boolean, ConfigurationLoader> clProvider){
			configurationLoaderProviders.put(format, clProvider);
		}

		@Override
		public <T, D extends IDefinition<T>, B extends IDefinitionBuilder, I> void registerDefinitionBuilderSupplier(Class<T> defType, Class<I> inputType, DefinitionBuilderSupplier<T, D, B, I> builderSupplier){
			definitionBuilderSuppliers.put(new ImmutablePair<>(defType, inputType), builderSupplier);
		}

	}

	public static class AEPreInitializationEventImpl extends AEStateEventImpl implements AEPreInitializationEvent {

		private BiFunction<String, Boolean, ConfigurationLoader> configurationLoaderProvider;
		private boolean dynamicDefaults;
		private Map<Pair<Class, Class>, DefinitionBuilderSupplier> definitionBuilderSuppliers;

		public AEPreInitializationEventImpl(BiFunction<String, Boolean, ConfigurationLoader> configurationLoaderProvider, boolean dynamicDefaults, Map<Pair<Class, Class>, DefinitionBuilderSupplier> definitionBuilderSuppliers){
			this.configurationLoaderProvider = configurationLoaderProvider;
			this.dynamicDefaults = dynamicDefaults;
			this.definitionBuilderSuppliers = definitionBuilderSuppliers;
		}

		@Override
		public <C> ConfigurationLoader<C> configurationLoader(){
			return configurationLoaderProvider.apply(AppEng.instance().getCurrentName(), dynamicDefaults);
		}

		@Override
		public void registerCustomFeatureManager(FeaturesManager manager){
			GlobalFeaturesManager.INSTANCE.register(AppEng.instance().getCurrentName(), manager);
		}

		@Override
		public FeaturesManager globalFeaturesManager(){
			return GlobalFeaturesManager.INSTANCE;
		}

		@Override
		public InitializationComponentsHandlerImpl defaultICHandler(){
			return new InitializationComponentsHandlerImpl();
		}

		@Override
		public DefinitionFactory factory(InitializationComponentsHandler commonInitHandler, SidedICHProxy sidedInitHandler){
			return new DefinitionFactory(commonInitHandler, sidedInitHandler, definitionBuilderSuppliers);
		}

	}

	public static class AEInitializationEventImpl extends AEStateEventImpl implements AEStateEvent.AEInitializationEvent {

	}

	public static class AEPostInitializationEventImpl extends AEStateEventImpl implements AEStateEvent.AEPostInitializationEvent {

	}

	public static class AELoadCompleteEventImpl extends AEStateEventImpl implements AEStateEvent.AELoadCompleteEvent {

	}

	public static class AEServerAboutToStartEventImpl extends AEStateEventImpl implements AEStateEvent.AEServerAboutToStartEvent {

	}

	public static class AEServerStartingEventImpl extends AEStateEventImpl implements AEStateEvent.AEServerStartingEvent {

		private Consumer<ICommand> serverCommandConsumer;
		private Consumer<ICommand> subcommandConsumer;

		public AEServerStartingEventImpl(Consumer<ICommand> serverCommandConsumer, Consumer<ICommand> subcommandConsumer){
			this.serverCommandConsumer = serverCommandConsumer;
			this.subcommandConsumer = subcommandConsumer;
		}

		@Override
		public void registerServerCommand(ICommand command){
			serverCommandConsumer.accept(command);
		}

		@Override
		public void registerModuleSubcommand(ICommand command){
			subcommandConsumer.accept(command);
		}

	}

	public static class AEServerStoppingEventImpl extends AEStateEventImpl implements AEStateEvent.AEServerStoppingEvent {

	}

	public static class AEServerStoppedEventImpl extends AEStateEventImpl implements AEStateEvent.AEServerStoppedEvent {

	}

	/**
	 * Implementation of {@linkplain AEStateEvent.ModuleIMCMessageEvent}.
	 *
	 * @author Elix_x
	 */
	public static class ModuleIMCMessageEventImpl extends AEStateEventImpl implements AEStateEvent.ModuleIMCMessageEvent {

		private final FMLInterModComms.IMCMessage message;

		public ModuleIMCMessageEventImpl(FMLInterModComms.IMCMessage message){
			this.message = message;
		}

		public FMLInterModComms.IMCMessage getMessage(){
			return message;
		}

		public <T> T getValue(){
			return ReflectionHelper.getPrivateValue(FMLInterModComms.IMCMessage.class, this.message, "value");
		}

	}
}
