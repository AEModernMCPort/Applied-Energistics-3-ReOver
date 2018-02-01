package appeng.core.me.network;

import appeng.core.me.api.network.Network;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

public class NetworkThreadsManager {

	protected final Network network;
	protected List<NetworkThreadImpl> threads = new LinkedList<>();

	public NetworkThreadsManager(Network network){
		this.network = network;
	}

	@Nonnull
	public NetworkThreadImpl requestThread(Runnable operation){
		NetworkThreadImpl thread = new NetworkThreadImpl(new Thread(operation, String.format("Network [%s] thread #~%s executing %s", network.getUUID(), threads.size(), operation.getClass().getName())));
		threads.add(thread);
		return thread;
	}

	@Nonnull
	public Collection<NetworkThreadImpl> getThreads(){
		return threads;
	}

	protected class NetworkThreadImpl implements Network.NetworkThread {

		protected final Thread thread;

		public NetworkThreadImpl(Thread thread){
			this.thread = thread;
		}

		@Override
		public Thread getThread(){
			return thread;
		}

	}

}
