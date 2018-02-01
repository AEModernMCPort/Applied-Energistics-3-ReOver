package appeng.core.me.api.network.event;

import javax.annotation.Nonnull;
import java.util.function.Consumer;

public interface NCEventBus<O extends EventBusOwner<O, E>, E extends NCEventBus.Event<O, E>> {

	/**
	 * The owner of this event bus
	 *
	 * @return owner of this event bus
	 */
	@Nonnull
	O getOwner();

	/**
	 * Register event listener
	 *
	 * @param listener event listener
	 */
	void registerListener(@Nonnull Consumer<E> listener);

	/**
	 * Unregister event listener
	 *
	 * @param listener event listener
	 */
	void unregisterListener(@Nonnull Consumer<E> listener);

	/**
	 * Fires an event to all listeners.<br>
	 * <b>To be used by {@linkplain #getOwner() owner} only.</b>
	 *
	 * @param event event
	 */
	void fire(@Nonnull E event);

	interface Event<O extends EventBusOwner<O, E>, E extends NCEventBus.Event<O, E>> {

		NCEventBus<O, E> getEventBus();

	}

}
