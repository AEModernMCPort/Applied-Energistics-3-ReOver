package appeng.core.core.crafting.ion.temp;

import appeng.core.AppEng;
import appeng.core.core.api.crafting.ion.IonEnvironmentContext;
import appeng.core.core.api.crafting.ion.IonEnvironmentContextChangeEvent;
import appeng.core.core.api.crafting.ion.NativeEnvironmentChange;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod.EventBusSubscriber(modid = AppEng.MODID)
public class OnTempChangeListener {

	@SubscribeEvent(priority = EventPriority.HIGH)
	public static void destroyInWorldEnv(IonEnvironmentContextChangeEvent event){
		//TODO FX the crap out of this!
		if(event.getChange() == NativeEnvironmentChange.COOLING || event.getChange() == NativeEnvironmentChange.HEATING) if(IonEnvironmentContext.isInWorldEnv(event.getContext())) event.getContext().world().get().setBlockToAir(event.getContext().pos().get());
	}

	//TODO Resolve results & create them

	//TODO Spawn a chilly explosion of positrons or a whirly outbreak of certus vapors

}
