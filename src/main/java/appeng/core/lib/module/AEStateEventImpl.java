package appeng.core.lib.module;

import appeng.api.bootstrap.DefinitionBuilderSupplier;
import appeng.api.bootstrap.IDefinitionBuilder;
import appeng.api.bootstrap.InitializationComponentsHandler;
import appeng.api.bootstrap.SidedICHProxy;
import appeng.api.definitions.IDefinition;
import appeng.api.module.AEStateEvent;
import appeng.core.lib.bootstrap.DefinitionFactory;
import appeng.core.lib.bootstrap.InitializationComponentsHandlerImpl;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.event.FMLInterModComms;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Map;
import java.util.function.BiFunction;

/**
 * Implementations of {@linkplain AEStateEvent}s.
 *
 * @author Elix_x
 */
public class AEStateEventImpl implements AEStateEvent {

	public static class AEBootstrapEventImpl extends AEStateEventImpl implements AEStateEvent.AEBootstrapEvent {

		private Map<Pair<Class, Class>, DefinitionBuilderSupplier> definitionBuilderSuppliers;

		public AEBootstrapEventImpl(Map<Pair<Class, Class>, DefinitionBuilderSupplier> definitionBuilderSuppliers){
			this.definitionBuilderSuppliers = definitionBuilderSuppliers;
		}

		@Override
		public <T, D extends IDefinition<T>, B extends IDefinitionBuilder<T, D, B>, I> void registerDefinitionBuilderSupplier(Class<T> defType, Class<I> inputType, DefinitionBuilderSupplier<T, D, B, I> builderSupplier){
			definitionBuilderSuppliers.put(new ImmutablePair<>(defType, inputType), builderSupplier);
		}

	}

	public static class AEPreInitializationEventImpl extends AEStateEventImpl implements AEPreInitializationEvent {

		private Map<Pair<Class, Class>, DefinitionBuilderSupplier> definitionBuilderSuppliers;

		public AEPreInitializationEventImpl(Map<Pair<Class, Class>, DefinitionBuilderSupplier> definitionBuilderSuppliers){
			this.definitionBuilderSuppliers = definitionBuilderSuppliers;
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
