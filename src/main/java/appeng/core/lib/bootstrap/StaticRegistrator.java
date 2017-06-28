package appeng.core.lib.bootstrap;

import appeng.core.AppEng;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.registries.IForgeRegistryEntry;

@Mod.EventBusSubscriber(modid = AppEng.MODID)
public class StaticRegistrator {

	private static final Multimap<Class, IForgeRegistryEntry> registryQueue = HashMultimap.create();

	@SubscribeEvent
	public static void register(RegistryEvent.Register event){
		if(registryQueue.containsKey(event.getGenericType())) registryQueue.get((Class) event.getGenericType()).forEach(entry -> event.getRegistry().register(entry));
	}

	public static <T> void addToRegistryQueue(IForgeRegistryEntry<T> entry){
		registryQueue.put(entry.getRegistryType(), entry);
	}

}
