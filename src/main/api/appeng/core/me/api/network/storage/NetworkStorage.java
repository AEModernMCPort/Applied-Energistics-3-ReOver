package appeng.core.me.api.network.storage;

import appeng.core.me.api.network.event.EventBusOwner;
import appeng.core.me.api.network.event.NCEventBus;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.INBTSerializable;

import java.util.function.Consumer;

public interface NetworkStorage<NS extends NetworkStorage<NS, ReadReq, ReadRep, WriteReq, WriteRep>, ReadReq extends NetworkStorage.Request<ReadRep>, ReadRep, WriteReq extends NetworkStorage.Request<WriteRep>, WriteRep> extends EventBusOwner<NS, NetworkStorage.NetworkStorageEvent<NS, ReadReq, ReadRep, WriteReq, WriteRep>>, INBTSerializable<NBTTagCompound> {

	/**
	 * Posts read request to the storage, result returned immediately on the same thread. Asynchronous, thread-safe, does not guarantee accurate reply unless requested from same thread as storage's.
	 *
	 * @param request read request
	 * @param <Req>   request type
	 * @param <Rep>   reply type
	 * @return reply to the request
	 */
	<Req extends Request<Rep>, Rep> Rep read(Req request);

	/**
	 * Posts write request to the storage, which will be processed on the storage's thread. Asynchronous, thread-safe, guarantees correct request execution and reply.<br><br>
	 * Replies can only be processed through {@linkplain Request#accept(Object)} to allows write requests serialization.
	 *
	 * @param request write request
	 * @param <Req>   request type
	 */
	<Req extends Request<Rep>, Rep> void write(Req request);

	/**
	 * Network storage request.<br>
	 * Must have either a no-args constructor or a single argument constructor compatible with the storage, (public or private), for deserialization.
	 *
	 * @param <Rep> reply type
	 */
	interface Request<Rep> extends Consumer<Rep>, INBTSerializable<NBTTagCompound> {

		/**
		 * Called once the request has been processed
		 *
		 * @param reply reply to this request
		 */
		@Override
		default void accept(Rep reply){}

	}

	interface NetworkStorageEvent<NS extends NetworkStorage<NS, ReadReq, ReadRep, WriteReq, WriteRep>, ReadReq extends NetworkStorage.Request<ReadRep>, ReadRep, WriteReq extends NetworkStorage.Request<WriteRep>, WriteRep> extends NCEventBus.Event<NS, NetworkStorage.NetworkStorageEvent<NS, ReadReq, ReadRep, WriteReq, WriteRep>> {

	}

}
