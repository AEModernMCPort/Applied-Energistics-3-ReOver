package code.elix_x.excore.utils.registry;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.registries.IForgeRegistryEntry;

/**
 * Static registration queue. Enqueue things to be registered with forge registries and they will be whenever needed.<br>
 * Automatically registers itself as event listener.
 *
 * @author Elix_x
 */
public class RegistrationQueue {

	private final Multimap<Class, IForgeRegistryEntry> queue = HashMultimap.create();

	public RegistrationQueue(){
		MinecraftForge.EVENT_BUS.register(this);
	}

	public <T> RegistrationQueue enqueue(IForgeRegistryEntry<T> entry){
		queue.put(entry.getRegistryType(), entry);
		return this;
	}

	public <T> RegistrationQueue enqueue(IForgeRegistryEntry<T>... entries){
		for(IForgeRegistryEntry entry : entries) enqueue(entry);
		return this;
	}

	@SubscribeEvent
	public void register(RegistryEvent.Register event){
		if(queue.containsKey(event.getGenericType()))
			queue.get((Class) event.getGenericType()).forEach(entry -> event.getRegistry().register(entry));
	}

}
