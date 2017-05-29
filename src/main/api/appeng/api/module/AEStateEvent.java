package appeng.api.module;

import net.minecraftforge.fml.common.event.FMLInterModComms;

/**
 * Parent class of all AE events sent to modules
 *
 * @author Elix_x
 */
public interface AEStateEvent {

	public interface AEBootstrapEvent {

	}

	public interface AEPreInitlizationEvent {

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
