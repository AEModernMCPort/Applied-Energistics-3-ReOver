package appeng.core.lib.module;

import appeng.api.module.AEStateEvent;
import net.minecraftforge.fml.common.event.FMLEvent;
import net.minecraftforge.fml.common.event.FMLInterModComms.IMCMessage;
import net.minecraftforge.fml.relauncher.ReflectionHelper;

/**
 * Implementation of {@linkplain AEStateEvent.ModuleIMCMessageEvent}.
 *
 * @author Elix_x
 */
public class ModuleIMCMessageEvent implements AEStateEvent.ModuleIMCMessageEvent {

	private final IMCMessage message;

	public ModuleIMCMessageEvent(IMCMessage message){
		this.message = message;
	}

	public IMCMessage getMessage(){
		return message;
	}

	public <T> T getValue(){
		return ReflectionHelper.getPrivateValue(IMCMessage.class, this.message, "value");
	}

}
