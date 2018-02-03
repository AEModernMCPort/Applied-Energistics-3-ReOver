package appeng.core.me.network;

import appeng.core.me.api.network.Network;

import javax.annotation.Nonnull;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class NetworkThreadsManager {

	protected final Network network;
	protected List<NetworkThread> threads = new LinkedList<>();

	protected boolean startNewThreadsImmediately = false;

	public NetworkThreadsManager(Network network){
		this.network = network;
	}

	@Nonnull
	public void requestThread(Network.NetworkThreadInfo info){
		NetworkThread thread = new NetworkThread(info, String.format("Network [%s] thread #~%s executing %s", network.getUUID(), threads.size(), info.getClass().getName()));
		threads.add(thread);
		if(startNewThreadsImmediately) thread.start();
	}

	public void startThreads(){
		startNewThreadsImmediately = true;
		threads.forEach(NetworkThread::start);
	}

	public Runnable suspendThreads(){
		startNewThreadsImmediately = false;
		List<Runnable> resume = threads.stream().map(NetworkThread::suspend).collect(Collectors.toList());
		return () -> {
			startNewThreadsImmediately = true;
			resume.forEach(Runnable::run);
		};
	}

	protected class NetworkThread {

		protected final Network.NetworkThreadInfo info;
		protected final Thread thread;
		protected volatile boolean started = false;

		public NetworkThread(Network.NetworkThreadInfo info, String name){
			this.info = info;
			this.thread = new Thread(info, name);
		}

		public Thread getThread(){
			return thread;
		}

		public void start(){
			if(!started){
				started = true;
				thread.start();
			}
		}

		public Runnable suspend(){
			return started ? info.suspend() : this::start;
		}

	}

}
