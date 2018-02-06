package appeng.core.me.network.storage;

import appeng.core.me.api.network.storage.TypedNetworkStorage;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import org.apache.commons.lang3.mutable.MutableInt;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;

public class TypedNetworkStorageImpl<T, NS extends TypedNetworkStorageImpl<T, NS, ReadReq, ReadRep, WriteReq, WriteRep>, ReadReq extends TypedNetworkStorage.Request<ReadRep>, ReadRep, WriteReq extends TypedNetworkStorage.Request<WriteRep>, WriteRep> extends NetworkStorageImpl<NS, ReadReq, ReadRep, WriteReq, WriteRep> implements TypedNetworkStorage<T, NS, ReadReq, ReadRep, WriteReq, WriteRep> {

	public TypedNetworkStorageImpl(Function<T, NBTTagCompound> serializer, Function<NBTTagCompound, T> deserializer){
		this.serializer = serializer;
		this.deserializer = deserializer;
	}

	/*
	 * Storage
	 */

	protected Map<T, MutableInt> storage = new HashMap<>();

	@Nonnull
	protected MutableInt get(T t){
		MutableInt stored = storage.get(t);
		return stored != null ? stored : new MutableInt();
	}

	@Nonnull
	protected MutableInt getOrPopulate(T t){
		MutableInt stored = storage.get(t);
		if(stored == null) set(t, stored = new MutableInt());
		return stored;
	}

	protected void set(T t, MutableInt stored){
		storage.put(t, stored);
	}

	/**
	 * The maximum amount of object that can be stored/extracted
	 *
	 * @param t             object
	 * @param currentAmount currently stored amount
	 * @param store         true if store, false if extract
	 * @return maximum amount that can be stored/extracted, positive in both cases
	 */
	protected int getMaxStore(T t, MutableInt currentAmount, boolean store){
		return store ? Integer.MAX_VALUE : currentAmount.intValue();
	}

	/*
	 * Read-write
	 */

	@Override
	protected <Req extends ReadReq, Rep extends ReadRep> Rep processReadRequest(Req req){
		if(req instanceof TypedNetworkStorage.Request.GetStoredAmount){
			TypedNetworkStorage.Request.GetStoredAmount<T> request = (TypedNetworkStorage.Request.GetStoredAmount<T>) req;
			return (Rep) new GetStoredAmountReply(get(request.query()).intValue());
		}
		return null;
	}

	@Override
	protected <Req extends WriteReq, Rep extends WriteRep> Rep processWriteRequest(Req req){
		if(req instanceof TypedNetworkStorage.Request.Store){
			TypedNetworkStorage.Request.Store<T> request = (TypedNetworkStorage.Request.Store<T>) req;

			assert request.minAmount() != 0 && request.maxAmount() != 0 && Math.signum(request.minAmount()) == Math.signum(request.maxAmount());
			boolean store = Math.signum(request.maxAmount()) == 1;
			int min = Math.min(Math.abs(request.minAmount()), Math.abs(request.maxAmount()));
			int max = Math.max(Math.abs(request.minAmount()), Math.abs(request.maxAmount()));

			MutableInt amount = getOrPopulate(request.query());

			int stored;
			int can = getMaxStore(request.query(), amount, store);
			if(can < min){
				stored = 0;
			} else if(can > max){
				stored = max;
				amount.add(store ? max : -max);
			} else {
				amount.add(store ? can : -can);
				stored = can;
			}
			return (Rep) new StoreReply(store ? stored : -stored);
		}
		return null;
	}

	/*
	 * Requests
	 */

	@Override
	public int getStoredAmount(T t){
		return this.<TypedNetworkStorage.Request.GetStoredAmount<T>, TypedNetworkStorage.Request.GetStoredAmount.Reply<T>>read(new GetStoredAmount(t)).amountStored();
	}

	@Override
	public void store(T t, int minAmount, int maxAmount, Consumer<TypedNetworkStorage.Request.Store.Reply<T>> replyConsumer){
		this.<TypedNetworkStorage.Request.Store<T>, TypedNetworkStorage.Request.Store.Reply<T>>write(new Store(t, minAmount, maxAmount, replyConsumer));
	}

	protected class GetStoredAmount implements TypedNetworkStorage.Request.GetStoredAmount<T> {

		protected T query;

		protected GetStoredAmount(){}

		public GetStoredAmount(T query){
			this.query = query;
		}

		@Override
		public T query(){
			return query;
		}

		@Override
		public NBTTagCompound serializeNBT(){
			return serializeT(query);
		}

		@Override
		public void deserializeNBT(NBTTagCompound nbt){
			this.query = deserializeT(nbt);
		}

	}

	protected static class GetStoredAmountReply<T> implements TypedNetworkStorage.Request.GetStoredAmount.Reply<T> {

		protected int amount;

		public GetStoredAmountReply(int amount){
			this.amount = amount;
		}

		@Override
		public int amountStored(){
			return amount;
		}
	}

	protected class Store extends RequestWithConsumer<TypedNetworkStorage.Request.Store.Reply<T>> implements TypedNetworkStorage.Request.Store<T> {

		protected T query;
		protected int min, max;

		public Store(){}

		public Store(T query, int min, int max, Consumer<Store.Reply<T>> replyConsumer){
			super(replyConsumer);
			this.query = query;
			this.min = min;
			this.max = max;
		}

		@Override
		public T query(){
			return query;
		}

		@Override
		public int minAmount(){
			return min;
		}

		@Override
		public int maxAmount(){
			return max;
		}

		@Override
		public NBTTagCompound serializeNBT(){
			NBTTagCompound nbt = new NBTTagCompound();
			nbt.setTag("query", serializeT(query));
			nbt.setInteger("min", min);
			nbt.setInteger("max", max);
			return nbt;
		}

		@Override
		public void deserializeNBT(NBTTagCompound nbt){
			this.query = deserializeT(nbt.getCompoundTag("query"));
			min = nbt.getInteger("min");
			max = nbt.getInteger("max");
		}

	}

	protected static class StoreReply<T> implements TypedNetworkStorage.Request.Store.Reply<T> {

		protected int amount;

		public StoreReply(int amount){
			this.amount = amount;
		}

		@Override
		public int amountStored(){
			return amount;
		}

	}

	/*
	 * IO
	 */

	protected final Function<T, NBTTagCompound> serializer;
	protected final Function<NBTTagCompound, T> deserializer;

	protected NBTTagCompound serializeT(T t){
		return serializer.apply(t);
	}

	protected T deserializeT(NBTTagCompound nbt){
		return deserializer.apply(nbt);
	}

	@Override
	public NBTTagCompound serializeNBT(){
		NBTTagCompound nbt = super.serializeNBT();
		NBTTagList storage = new NBTTagList();
		this.storage.forEach((t, amount) -> {
			if(amount.intValue() != 0){
				NBTTagCompound next = new NBTTagCompound();
				next.setTag("object", serializeT(t));
				next.setInteger("amount", amount.intValue());
				storage.appendTag(next);
			}
		});
		nbt.setTag("storage", storage);
		return nbt;
	}

	@Override
	public void deserializeNBT(NBTTagCompound nbt){
		super.deserializeNBT(nbt);
		this.storage.clear();
		NBTTagList storage = (NBTTagList) nbt.getTag("storage");
		storage.forEach(nbtBase -> {
			NBTTagCompound next = (NBTTagCompound) nbtBase;
			this.storage.put(deserializeT(next.getCompoundTag("object")), new MutableInt(next.getInteger("amount")));
		});
	}
}
