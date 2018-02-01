package appeng.core.me.api.network.event;

import javax.annotation.Nonnull;

public interface EventBusOwner<O extends EventBusOwner<O, E>, E extends NCEventBus.Event<O, E>> {

	@Nonnull
	NCEventBus<O, E> getEventBus();

}
