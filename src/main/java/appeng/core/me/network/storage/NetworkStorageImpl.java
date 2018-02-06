package appeng.core.me.network.storage;

import appeng.core.me.api.network.event.NCEventBus;
import appeng.core.me.api.network.storage.NetworkStorage;
import code.elix_x.excomms.reflection.ReflectionHelper;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

import javax.annotation.Nonnull;
import java.util.LinkedList;
import java.util.Queue;
import java.util.function.Consumer;
import java.util.function.Supplier;

public abstract class NetworkStorageImpl<NS extends NetworkStorage<NS, ReadReq, ReadRep, WriteReq, WriteRep>, ReadReq extends NetworkStorage.Request<ReadRep>, ReadRep, WriteReq extends NetworkStorage.Request<WriteRep>, WriteRep> implements NetworkStorage<NS, ReadReq, ReadRep, WriteReq, WriteRep> {

	/*
	 * Requests
	 */

	//Read

	@Override
	public <Req extends Request<Rep>, Rep> Rep read(Req request){
		Rep reply = (Rep) this.processReadRequest((Request) request);
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
			try {
				replyConsumer = new ReflectionHelper.AClass<Consumer<Rep>>(nbt.getString("consumer")).getDeclaredConstructor().setAccessible(true).newInstance();
			} catch(RuntimeException e){
				throw new IllegalArgumentException("Reply consumer class does not have any no-args constructors", e);
			}
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
		ReflectionHelper.AClass<Req> clas = new ReflectionHelper.AClass<>(nbt.getString("class"));
		Req request;
		try{
			request = clas.getDeclaredConstructor().setAccessible(true).newInstance();
		} catch(RuntimeException e){
			request = clas.getDeclaredConstructors().filter(constructor -> constructor.get().getParameterCount() == 1 && constructor.get().getParameterTypes()[0].isAssignableFrom(this.getClass())).findAny().orElseThrow(() -> new IllegalArgumentException(String.format("Request class %s does not have any compatible constructors with network storage of class %s", clas.get().getName(), this.getClass().getName()), e)).newInstance(this);
		}
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
