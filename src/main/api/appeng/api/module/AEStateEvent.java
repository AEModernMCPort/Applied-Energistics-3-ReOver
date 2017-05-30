package appeng.api.module;

import appeng.api.bootstrap.DefinitionFactory;
import appeng.api.bootstrap.IDefinitionBuilder;
import appeng.api.bootstrap.InitializationComponentsHandler;
import appeng.api.bootstrap.SidedICHProxy;
import appeng.api.definitions.IDefinition;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.event.FMLInterModComms;

import java.util.function.BiFunction;

/**
 * Parent class of all AE events sent to modules
 *
 * @author Elix_x
 */
public interface AEStateEvent {

	public interface AEBootstrapEvent {

		<T, D extends IDefinition<T>, B extends IDefinitionBuilder<T, D, B>, I> void registerDefinitionBuilderSupplier(Class<T> defType, Class<I> inputType, BiFunction<ResourceLocation, I, B> builderSupplier);

	}

	public interface AEPreInitializationEvent {

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
