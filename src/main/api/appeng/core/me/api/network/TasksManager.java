package appeng.core.me.api.network;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ITickable;
import net.minecraftforge.common.util.INBTSerializable;

import java.util.concurrent.Callable;
import java.util.concurrent.Future;

public interface TasksManager extends INBTSerializable<NBTTagCompound> {

	void addScheduledTask(ITickable task);
	void removeScheduledTask(ITickable task);

	<T extends Runnable & OTBTask> Future<?> submitOffthreadBoundTask(T task);
	<R, T extends Callable<R> & OTBTask> Future<R> submitOffthreadBoundTask(T task);

	Future<?> submitOffthreadUnboundTask(Runnable task);
	<R> Future<R> submitOffthreadUnboundTask(Callable<R> task);

	interface OTBTask {

		/**
		 * Called whenever this task should be suspended. When the method returns, no operation is performed by the thread and underlying data is safely serializable, until resume is called.<br>
		 * Returned {@linkplain Runnable runnable} will be called to resume thread operation
		 *
		 * @return runnable to resume thread operation
		 */
		Runnable suspend();

	}

}
