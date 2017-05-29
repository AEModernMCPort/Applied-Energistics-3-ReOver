package appeng.core.lib.module;

import appeng.api.module.AEStateEvent;
import net.minecraftforge.fml.common.event.FMLInterModComms;
import net.minecraftforge.fml.relauncher.ReflectionHelper;

/**
 * Implementations of {@linkplain AEStateEvent}s.
 *
 * @author Elix_x
 */
public class AEStateEventImpl implements AEStateEvent {

	public static class AEBootstrapEventImpl extends AEStateEventImpl implements AEStateEvent.AEBootstrapEvent {

	}

	public static class AEPreInitlizationEventImpl extends AEStateEventImpl implements AEStateEvent.AEPreInitlizationEvent {

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
