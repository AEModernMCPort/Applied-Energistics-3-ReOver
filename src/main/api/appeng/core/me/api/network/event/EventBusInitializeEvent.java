package appeng.core.me.api.network.event;

import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.fml.common.eventhandler.Event;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

/**
 * Posted whenever an event bus is initialized. If the owner is also a {@linkplain ICapabilityProvider capabilities provider}, the capabilities are initialized first.<br>
 * Note: you can register/unregister listeners at runtime (including after the bus has been initialized). This event is just for convenience for "always there" buses.
 *
 * @param <O> owner type
 * @param <E> event type
 * @author Elix_x
 */
public class EventBusInitializeEvent<O extends EventBusOwner<O, E>, E extends NCEventBus.Event<O, E>> extends Event {

	private final O owner;
	private final List<Consumer<E>> listeners = new ArrayList<>();

	public EventBusInitializeEvent(O owner){
		this.owner = owner;
	}

	@Nonnull
	public O getOwner(){
		return owner;
	}

	public void registerListener(@Nonnull Consumer<E> listener){
		listeners.add(listener);
	}

	public List<Consumer<E>> getListeners(){
		return Collections.unmodifiableList(listeners);
	}

}
