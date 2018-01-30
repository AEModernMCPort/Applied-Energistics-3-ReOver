package appeng.core.me.api.network.storage;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.INBTSerializable;

public interface NetworkStorage<ReadReq extends NetworkStorage.Request<ReadRep>, ReadRep, WriteReq extends NetworkStorage.Request<WriteRep>, WriteRep> extends INBTSerializable<NBTTagCompound> {

	/**
	 * Posts read request to the storage, result returned immediately on the same thread. Asynchronous, thread-safe, does not guarantee accurate reply unless requested from same thread as storage's.
	 *
	 * @param request read request
	 * @param <Rep>   reply type
	 * @param <Req>   request type
	 * @return reply to the request
	 */
	<Rep extends ReadRep, Req extends ReadReq> Rep read(Req request);

	/**
	 * Posts write request to the storage, which will be processed on the storage's thread. Asynchronous, thread-safe, guarantees correct request execution and reply.<br><br>
	 * Replies can only be processed through {@linkplain Request#fullfill(Object)} to allows write requests serialization.
	 *
	 * @param request write request
	 * @param <Req>   request type
	 */
	<Req extends WriteReq> void write(Req request);

	interface Request<Rep> extends INBTSerializable<NBTTagCompound> {

		/**
		 * Called once the request has been processed
		 *
		 * @param reply reply to this request
		 */
		default void fullfill(Rep reply){}

	}

}
