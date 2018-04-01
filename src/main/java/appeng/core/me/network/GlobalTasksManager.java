package appeng.core.me.network;

import appeng.core.AppEng;
import appeng.core.me.api.network.Network;
import appeng.core.me.api.network.TasksManager;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ITickable;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Mod.EventBusSubscriber(modid = AppEng.MODID)
public class GlobalTasksManager {

	public static final int OFFLOADLOW = 128, OFFLOADHIGH = 160;
	public static final int THREADLIM = 512;

	protected static final ListeningExecutorService executorService = MoreExecutors.listeningDecorator(Executors.newCachedThreadPool(new ThreadFactoryBuilder().setNameFormat("AE3 GTM Thread %s").build()));

	@SubscribeEvent
	public static void worldTick(TickEvent.ServerTickEvent event){
		if(event.phase == TickEvent.Phase.END) GlobalNBDManagerImpl.getInstance().globalTasksManager.serverTick();
	}

	protected GlobalTasksManager(){

	}

	protected TasksManager requestTasksManager(Network network){
		return new LocalTasksManager(network);
	}

	/*
	 * Scheduled
	 */

	protected Collection<Set<ITickable>> scheduled = new ArrayList<>();
	protected Collection<ScheduledTasksExecutor> scheduledTasksExecutors = new ArrayList<>();
	protected AtomicInteger totalScheduled = new AtomicInteger();

	protected void schedule(ITickable task){
		Set<ITickable> acceptor = scheduled.stream().unordered().filter(t -> t.size() < THREADLIM).findAny().orElse(null);
		if(acceptor == null){
			scheduled.add(acceptor = ConcurrentHashMap.newKeySet());
			int d2t = THREADLIM / scheduled.size();
			Consumer<Collection<ITickable>> sch = acceptor::addAll;
			scheduled.forEach(t -> {
				Set<ITickable> trans = t.stream().limit(d2t).collect(Collectors.toSet());
				t.removeAll(trans);
				sch.accept(trans);
			});

			ScheduledTasksExecutor executor = new ScheduledTasksExecutor(acceptor);
			scheduledTasksExecutors.add(executor);
			if(scheduledTasksExecutors.size() > 1) executorService.submit(executor);
			else no1exe = executor;
		}
		acceptor.add(task);
		if(totalScheduled.getAndIncrement() == OFFLOADHIGH){
			executorService.submit(no1exe);
			no1exe = null;
		}
	}

	protected void removeScheduled(ITickable task){
		scheduled.stream().filter(t -> t.contains(task)).findAny().ifPresent(exeSet -> {
			exeSet.remove(task);
			if(exeSet.size() == 0){
				scheduled.remove(exeSet);
				ScheduledTasksExecutor executor = scheduledTasksExecutors.stream().filter(exe -> exe.tasks == exeSet).findAny().get();
				scheduledTasksExecutors.remove(executor);
				executor.shutdown();
				if(no1exe == executor) no1exe = null;
			}
			if(totalScheduled.getAndDecrement() == OFFLOADLOW){
				Set<ITickable> allTasks = scheduled.stream().flatMap(Set::stream).collect(Collectors.toSet());

				scheduled.clear();
				scheduledTasksExecutors.forEach(ScheduledTasksExecutor::shutdown);
				scheduledTasksExecutors.clear();

				Set<ITickable> tasks = ConcurrentHashMap.newKeySet();
				tasks.addAll(allTasks);
				scheduled.add(tasks);
				scheduledTasksExecutors.add(no1exe = new ScheduledTasksExecutor(tasks));
			}
		});
	}

	protected ScheduledTasksExecutor no1exe = null;

	protected void serverTick(){
		if(no1exe != null) no1exe.tick();
	}

	/*
	 * Bound
	 */

	protected List<List<TasksManager.OTBTask>> allBoundTasks = new ArrayList<>();

	protected Stream<TasksManager.OTBTask> allBoundExtTasks(){
		return allBoundTasks.stream().flatMap(List::stream);
	}

	protected Stream<TasksManager.OTBTask> allBoundTasks(){
		return Stream.concat(scheduledTasksExecutors.stream(), allBoundExtTasks());
	}

	/*
	 * Suspend
	 */

	protected List<Runnable> suspendedTasksResume;

	protected Runnable suspend(){
		suspendedTasksResume = allBoundTasks().map(TasksManager.OTBTask::suspend).collect(Collectors.toList());
		return () -> {
			suspendedTasksResume.forEach(Runnable::run);
			suspendedTasksResume = null;
		};
	}

	protected void shutdown(){
		if(suspendedTasksResume == null) suspend();
		if(no1exe != null) no1exe.shutdown();
		allBoundTasks().forEach(TasksManager.OTBTask::shutdown);
		scheduledTasksExecutors.clear();
	}

	/*
	 * Unbound
	 */

	public Future<?> submitOffthreadUnboundTask(Runnable task){
		return executorService.submit(task);
	}

	public <R> Future<R> submitOffthreadUnboundTask(Callable<R> task){
		return executorService.submit(task);
	}

	protected class ScheduledTasksExecutor implements Runnable, TasksManager.OTBTask {

		protected final Set<ITickable> tasks;

		public ScheduledTasksExecutor(Set<ITickable> tasks){
			this.tasks = tasks;
		}

		protected volatile AtomicBoolean suspend = new AtomicBoolean(false), stop = new AtomicBoolean(false), running = new AtomicBoolean(false);

		@Override
		public void run(){
			while(!stop.get()) tick();
		}

		protected void tick(){
			if(!suspend.get()){
				running.set(true);
				tasks.forEach(ITickable::update);
				running.set(false);
			}
		}

		@Override
		public Runnable suspend(){
			suspend.set(true);
			while(running.get());
			return () -> suspend.set(false);
		}

		@Override
		public void shutdown(){
			stop.set(true);
		}

	}

	protected class LocalTasksManager implements TasksManager {

		protected final Network network;

		protected final Set<ITickable> scheduled = ConcurrentHashMap.newKeySet();
		protected final List<OTBTask> bound = new ArrayList<>();

		public LocalTasksManager(Network network){
			this.network = network;
			allBoundTasks.add(bound);
		}

		@Override
		public void addScheduledTask(ITickable task){
			scheduled.add(task);
			schedule(task);
		}

		@Override
		public void removeScheduledTask(ITickable task){
			scheduled.remove(task);
			removeScheduled(task);
		}

		@Override
		public <T extends Runnable & OTBTask> Future<?> submitOffthreadBoundTask(T task){
			bound.add(task);

			if(suspendedTasksResume != null) suspendedTasksResume.add(task.suspend());

			ListenableFuture<?> future = executorService.submit(task);
			//FIXME Possibly concurrent list access
			future.addListener(() -> bound.remove(task), MoreExecutors.directExecutor());
			return future;
		}

		@Override
		public <R, T extends Callable<R> & OTBTask> Future<R> submitOffthreadBoundTask(T task){
			bound.add(task);

			if(suspendedTasksResume != null) suspendedTasksResume.add(task.suspend());

			ListenableFuture<R> future = executorService.submit(task);
			//FIXME Possibly concurrent list access
			future.addListener(() -> bound.remove(task), MoreExecutors.directExecutor());
			return future;
		}

		@Override
		public Future<?> submitOffthreadUnboundTask(Runnable task){
			return GlobalTasksManager.this.submitOffthreadUnboundTask(task);
		}

		@Override
		public <R> Future<R> submitOffthreadUnboundTask(Callable<R> task){
			return GlobalTasksManager.this.submitOffthreadUnboundTask(task);
		}

		/*
		 * IO
		 * TODO IO for bound tasks
		 */

		@Override
		public NBTTagCompound serializeNBT(){
			return new NBTTagCompound();
		}

		@Override
		public void deserializeNBT(NBTTagCompound nbt){

		}

	}

}
