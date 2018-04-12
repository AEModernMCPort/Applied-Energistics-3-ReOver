package appeng.core.me.api.network.device.behavior;

import appeng.core.me.api.network.NetDevice;
import appeng.core.me.api.network.PhysicalDevice;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public interface BehaviorDriven<B> {

	enum BehaviorOperationResult {

		/**
		 * Execution of the behavior is successful, and iteration should stop
		 */
		SUCCESS(true), /**
		 * Execution of the behavior is not successful, but iteration should stop
		 */
		FAIL(true), /**
		 * Whatever the result of the behavior, the iteration should continue
		 */
		PASS(false);

		public final boolean stopIt;

		BehaviorOperationResult(boolean stopIt){
			this.stopIt = stopIt;
		}

		public boolean stopIt(){
			return stopIt;
		}

	}

	/**
	 * Fired whenever a behavior driven device is initialized, after both capabilities and event initialization.<br>
	 * Due to type loss and forge not liking wild cards, the event is a wrapper for type-safe accessor. Event subscriber's device type and wrapped accessor's device type are guaranteed to match.
	 *
	 * @param <N> device type
	 */
	class AttachDeviceBehaviorEvent<N extends NetDevice & BehaviorDriven> extends net.minecraftforge.fml.common.eventhandler.Event {

		private final Event event;

		public AttachDeviceBehaviorEvent(N device){
			this.event = new Event(device);
		}

		public <N extends NetDevice<N, P> & BehaviorDriven<B>, P extends PhysicalDevice<N, P>, B> Optional<Event<N, P, B>> event(Class<? super N> filter){
			return filter.isInstance(event.device) ? Optional.of(event) : Optional.empty();
		}

		public <B> List<Pair<B, Double>> getBehaviors(){
			return Collections.unmodifiableList(event.behaviors);
		}

		/**
		 * {@link AttachDeviceBehaviorEvent} unwrapped with usable types.
		 *
		 * @param <N> network device type
		 * @param <P> physical device type
		 * @param <B> behavior type
		 */
		public static class Event<N extends NetDevice<N, P> & BehaviorDriven<B>, P extends PhysicalDevice<N, P>, B> {

			private final N device;
			private final List<Pair<B, Double>> behaviors = new ArrayList<>();

			private Event(N device){
				this.device = device;
			}

			public N getDevice(){
				return device;
			}

			public void addBehavior(B behavior, double priority){
				behaviors.add(new ImmutablePair<>(behavior, priority));
			}

		}

	}

}
