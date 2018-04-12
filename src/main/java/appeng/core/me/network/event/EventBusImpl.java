package appeng.core.me.network.event;

import appeng.core.me.api.network.event.EventBusInitializeEvent;
import appeng.core.me.api.network.event.EventBusOwner;
import appeng.core.me.api.network.event.NCEventBus;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;

public class EventBusImpl<O extends EventBusOwner<O, E>, E extends NCEventBus.Event<O, E>> implements NCEventBus<O, E> {

	protected final O owner;
	protected final List<Consumer<E>> listeners = new LinkedList<>();

	public EventBusImpl(O owner){
		this.owner = owner;
	}

	public EventBusImpl(O owner, Collection<Consumer<E>> listeners){
		this(owner);
		this.listeners.addAll(listeners);
	}

	public EventBusImpl(EventBusInitializeEvent<O, E> event){
		this(event.getOwner(), event.getListeners());
	}

	@Nonnull
	@Override
	public O getOwner(){
		return owner;
	}

	@Override
	public void registerListener(@Nonnull Consumer<E> listener){
		listeners.add(listener);
	}

	@Override
	public void unregisterListener(@Nonnull Consumer<E> listener){
		listeners.remove(listener);
	}

	@Override
	public void fire(@Nonnull E event){
		listeners.forEach(listener -> listener.accept(event));
	}

}
