package appeng.core.core.tick;

import appeng.core.AppEng;
import appeng.core.core.AppEngCore;
import appeng.core.core.api.tick.ChildrenTickable;
import appeng.core.core.api.tick.IHasChildrenTickables;
import appeng.core.core.api.tick.Tickables;
import appeng.core.lib.capability.SingleCapabilityProvider;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.List;

@Mod.EventBusSubscriber(modid = AppEng.MODID)
public class TickablesImpl<T> implements Tickables<T> {

	@SubscribeEvent(priority = EventPriority.HIGH)
	public static void attachCaps(AttachCapabilitiesEvent event){
		if(event.getObject() instanceof IHasChildrenTickables) event.addCapability(new ResourceLocation(AppEng.MODID, "tickables"), new SingleCapabilityProvider<>(AppEngCore.tickablesCapability, new TickablesImpl<>()));
	}

	public List<ChildrenTickable<T>> tickables;

	@Override
	public void accept(ChildrenTickable<T> tickable){
		tickables.add(tickable);
	}

	public void tick(T parent){
		tickables.forEach(tickable -> tickable.tick(parent));
	}

}
