package appeng.core.me.network.storage.reqrep;

import appeng.core.me.api.network.event.NCEventBus;
import appeng.core.me.api.network.storage.reqrep.RRNetworkStorage;
import code.elix_x.excomms.reflection.ReflectionHelper;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

import javax.annotation.Nonnull;
import java.util.LinkedList;
import java.util.Queue;
import java.util.function.Consumer;
import java.util.function.Supplier;

public abstract class RRNetworkStorageImpl<NS extends RRNetworkStorage<NS, ReadReq, ReadRep, WriteReq, WriteRep>, ReadReq extends RRNetworkStorage.Request<ReadRep>, ReadRep, WriteReq extends RRNetworkStorage.Request<WriteRep>, WriteRep> implements RRNetworkStorage<NS, ReadReq, ReadRep, WriteReq, WriteRep> {

	/*
	 * Requests
	 */

	//Read

	@Override
	public <Req extends Request<Rep>, Rep> Rep read(Req request){
		Rep reply = (Rep) this.processReadRequest((ReadReq) request);
		request.accept(reply);
		return reply;
	}

	protected abstract <Req extends ReadReq, Rep extends ReadRep> Rep processReadRequest(Req request);

	//Write

	protected Queue<WriteReq> writeRequests = new LinkedList<>();

	protected void processWriteRequests(Supplier<Boolean> processWhileTrue){
		while(!writeRequests.isEmpty() && processWhileTrue.get()) processWriteRequest(writeRequests.poll());
	}

	@Override
	public <Req extends Request<Rep>, Rep> void write(Req request){
		writeRequests.add((WriteReq) request);
	}

	protected abstract <Req extends WriteReq, Rep extends WriteRep> Rep processWriteRequest(Req request);

	protected class RequestWithConsumer<Rep> implements Request<Rep> {

		protected Consumer<Rep> replyConsumer;

		protected RequestWithConsumer(){}

		public RequestWithConsumer(Consumer<Rep> replyConsumer){
			this.replyConsumer = replyConsumer;
		}

		@Override
		public NBTTagCompound serializeNBT(){
			NBTTagCompound nbt = new NBTTagCompound();
			nbt.setString("consumer", replyConsumer.getClass().getName());
			return nbt;
		}

		@Override
		public void deserializeNBT(NBTTagCompound nbt){
			String rcc = nbt.getString("consumer");
			replyConsumer = ReflectionHelper.AClass.<Consumer<Rep>>find(rcc).orElseThrow(() -> new IllegalArgumentException(String.format("Reply consumer class (%s) no longer exists", rcc))).getDeclaredConstructor().orElseThrow(() -> new IllegalArgumentException(String.format("Reply consumer class (%s) does not have any no-args constructors", rcc))).setAccessible(true).newInstance().orElseThrow(() -> new IllegalArgumentException(String.format("Failed instantiating reply consumer (%s)", rcc)));
		}
	}

	/*
	 * Events
	 */

	@Nonnull
	@Override
	public NCEventBus<NS, NetworkStorageEvent<NS, ReadReq, ReadRep, WriteReq, WriteRep>> getEventBus(){
		return null;
	}

	/*
	 * IO
	 */

	protected <Rep, Req extends Request<Rep>> NBTTagCompound serializeRequest(Req request){
		NBTTagCompound nbt = new NBTTagCompound();
		nbt.setString("class", request.getClass().getName());
		nbt.setTag("request", request.serializeNBT());
		return nbt;
	}

	protected <Rep, Req extends Request<Rep>> Req deserializeRequest(NBTTagCompound nbt){
		ReflectionHelper.AClass<Req> clas = ReflectionHelper.AClass.<Req>find(nbt.getString("class")).orElseThrow(() -> new IllegalArgumentException("Request class no longer exists"));
		Req request = clas.getDeclaredConstructor().flatMap(c -> c.setAccessible(true).newInstance()).orElse(null);
		if(request == null) request = clas.findConstructorsForArgs(this.getClass()).findAny().flatMap(c -> c.newInstance(this)).orElse(null);
		if(request == null) throw new IllegalArgumentException(String.format("Request class %s does not have any compatible constructors with network storage of class %s", clas.get().getName(), this.getClass().getName()));
		request.deserializeNBT(nbt.getCompoundTag("request"));
		return request;
	}

	@Override
	public NBTTagCompound serializeNBT(){
		NBTTagCompound nbt = new NBTTagCompound();
		NBTTagList requests = new NBTTagList();
		this.writeRequests.stream().map(this::<WriteRep, WriteReq>serializeRequest).forEach(requests::appendTag);
		nbt.setTag("requests", requests);
		return nbt;
	}

	@Override
	public void deserializeNBT(NBTTagCompound nbt){
		this.writeRequests.clear();
		NBTTagList requests = (NBTTagList) nbt.getTag("requests");
		requests.forEach(next -> this.writeRequests.add(this.deserializeRequest((NBTTagCompound) next)));
	}
}
