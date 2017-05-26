package appeng.api.module;

import net.minecraftforge.fml.common.event.FMLEvent;
import net.minecraftforge.fml.common.event.FMLInterModComms.IMCMessage;
import net.minecraftforge.fml.relauncher.ReflectionHelper;

/**
 * Fired to the module when {@linkplain IMCMessage} is fired to AE, with {@linkplain IMCMessage#key} representing name of the module.
 *
 * @author Elix_x
 */
public class ModuleIMCMessageEvent extends FMLEvent {

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
