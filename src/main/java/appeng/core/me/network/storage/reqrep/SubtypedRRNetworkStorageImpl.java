package appeng.core.me.network.storage.reqrep;

import appeng.core.me.api.network.storage.reqrep.SubtypedRRNetworkStorage;
import appeng.core.me.api.network.storage.reqrep.TypedRRNetworkStorage;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.util.INBTSerializable;
import org.apache.commons.lang3.mutable.MutableInt;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

public class SubtypedRRNetworkStorageImpl<T, ST, NS extends SubtypedRRNetworkStorageImpl<T, ST, NS, ReadReq, ReadRep, WriteReq, WriteRep>, ReadReq extends SubtypedRRNetworkStorage.Request<ReadRep>, ReadRep, WriteReq extends SubtypedRRNetworkStorage.Request<WriteRep>, WriteRep> extends RRNetworkStorageImpl<NS, ReadReq, ReadRep, WriteReq, WriteRep> implements SubtypedRRNetworkStorage<T, ST, NS, ReadReq, ReadRep, WriteReq, WriteRep> {

	public SubtypedRRNetworkStorageImpl(Function<ST, T> getType, Function<T, NBTTagCompound> serializerT, Function<NBTTagCompound, T> deserializerT, Function<ST, NBTTagCompound> serializerST, Function<NBTTagCompound, ST> deserializerST){
		this.getType = getType;
		this.serializerT = serializerT;
		this.deserializerT = deserializerT;
		this.serializerST = serializerST;
		this.deserializerST = deserializerST;
	}

	/*
	 * Storage
	 */

	protected final Function<ST, T> getType;

	protected T getType(ST subtype){
		return getType.apply(subtype);
	}

	protected Map<T, Subtypes> subtypes = new HashMap<>();

	protected Optional<Subtypes> get(T t){
		return Optional.ofNullable(subtypes.get(t));
	}

	protected Subtypes getOrPopulate(T t){
		Subtypes subtypes = get(t).orElse(null);
		if(subtypes == null) this.subtypes.put(t, subtypes = new Subtypes());
		return subtypes;
	}

	/**
	 * The maximum amount of object that can be stored/extracted
	 *
	 * @param st            object
	 * @param currentAmount currently stored amount
	 * @param store         true if store, false if extract
	 * @return maximum amount that can be stored/extracted, positive in both cases
	 */
	protected int getMaxStore(ST st, MutableInt currentAmount, boolean store){
		return store ? Integer.MAX_VALUE : currentAmount.intValue();
	}

	protected class Subtypes implements INBTSerializable<NBTTagCompound> {

		protected Map<ST, MutableInt> subtypes = new HashMap<>();

		@Nonnull
		protected MutableInt get(ST st){
			MutableInt stored = subtypes.get(st);
			return stored != null ? stored : new MutableInt();
		}

		@Nonnull
		protected MutableInt getOrPopulate(ST st){
			MutableInt stored = subtypes.get(st);
			if(stored == null) set(st, stored = new MutableInt());
			return stored;
		}

		protected void set(ST st, MutableInt stored){
			subtypes.put(st, stored);
		}

		@Override
		public NBTTagCompound serializeNBT(){
			NBTTagCompound nbt = new NBTTagCompound();
			NBTTagList subtypes = new NBTTagList();
			this.subtypes.forEach((st, amount) -> {
				if(amount.intValue() != 0){
					NBTTagCompound next = new NBTTagCompound();
					next.setTag("subtype", serializeST(st));
					next.setInteger("amount", amount.intValue());
					subtypes.appendTag(next);
				}
			});
			nbt.setTag("subtypes", subtypes);
			return nbt;
		}

		@Override
		public void deserializeNBT(NBTTagCompound nbt){
			NBTTagList subtypes = (NBTTagList) nbt.getTag("subtypes");
			subtypes.forEach(nbtBase -> {
				NBTTagCompound next = (NBTTagCompound) nbtBase;
				this.subtypes.put(deserializeST(next.getCompoundTag("subtype")), new MutableInt(next.getInteger("amount")));
			});
		}
	}

	/*
	 * Read-write
	 */

	@Override
	protected <Req extends ReadReq, Rep extends ReadRep> Rep processReadRequest(Req request){
		if(request instanceof TypedRRNetworkStorage.Request.GetStoredAmount){
			TypedRRNetworkStorage.Request.GetStoredAmount<ST> req = (TypedRRNetworkStorage.Request.GetStoredAmount) request;
			return (Rep) new GetStoredAmountReply(get(getType(req.query())).map(st -> st.get(req.query()).getValue()).orElse(0));
		} else if(request instanceof SubtypedRRNetworkStorage.Request.GetSubtypesCount){
			SubtypedRRNetworkStorage.Request.GetSubtypesCount<T> req = (SubtypedRRNetworkStorage.Request.GetSubtypesCount) request;
			return (Rep) new GetSubtypesCountReply<>(get(req.query()).map(st -> st.subtypes.size()).orElse(0));
		} else if(request instanceof SubtypedRRNetworkStorage.Request.GetTotalAmountStored){
			SubtypedRRNetworkStorage.Request.GetTotalAmountStored<T> req = (SubtypedRRNetworkStorage.Request.GetTotalAmountStored) request;
			return (Rep) new GetTotalAmountStoredReply<>(get(req.query()).map(st -> st.subtypes.values().stream().mapToInt(MutableInt::intValue).sum()).orElse(0));
		}
		return null;
	}

	@Override
	protected <Req extends WriteReq, Rep extends WriteRep> Rep processWriteRequest(Req request){
		if(request instanceof TypedRRNetworkStorage.Request.Store){
			TypedRRNetworkStorage.Request.Store<ST> req = (TypedRRNetworkStorage.Request.Store<ST>) request;

			assert req.minAmount() != 0 && req.maxAmount() != 0 && Math.signum(req.minAmount()) == Math.signum(req.maxAmount());
			boolean store = Math.signum(req.maxAmount()) == 1;
			int min = Math.min(Math.abs(req.minAmount()), Math.abs(req.maxAmount()));
			int max = Math.max(Math.abs(req.minAmount()), Math.abs(req.maxAmount()));

			MutableInt amount = getOrPopulate(getType(req.query())).getOrPopulate(req.query());

			int stored;
			int can = getMaxStore(req.query(), amount, store);
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
	public int getStoredAmount(ST st){
		return this.<TypedRRNetworkStorage.Request.GetStoredAmount<ST>, TypedRRNetworkStorage.Request.GetStoredAmount.Reply<ST>>read(new GetStoredAmount(st)).amountStored();
	}

	@Override
	public int getSubtypesCount(T t){
		return this.<SubtypedRRNetworkStorage.Request.GetSubtypesCount<T>, SubtypedRRNetworkStorage.Request.GetSubtypesCount.Reply<T>>read(new GetSubtypesCount(t)).subtypesCount();
	}

	@Override
	public int getTotalAmountStored(T t){
		return this.<SubtypedRRNetworkStorage.Request.GetTotalAmountStored<T>, SubtypedRRNetworkStorage.Request.GetTotalAmountStored.Reply<T>>read(new GetTotalAmountStored(t)).totalAmountStored();
	}

	@Override
	public void store(ST st, int minAmount, int maxAmount, Consumer<TypedRRNetworkStorage.Request.Store.Reply<ST>> replyConsumer){
		this.<TypedRRNetworkStorage.Request.Store<ST>, TypedRRNetworkStorage.Request.Store.Reply<ST>>write(new Store(st, minAmount, maxAmount, replyConsumer));
	}

	protected class GetStoredAmount implements TypedRRNetworkStorage.Request.GetStoredAmount<ST> {

		protected ST query;

		protected GetStoredAmount(){}

		public GetStoredAmount(ST query){
			this.query = query;
		}

		@Override
		public ST query(){
			return query;
		}

		@Override
		public NBTTagCompound serializeNBT(){
			return serializeST(query);
		}

		@Override
		public void deserializeNBT(NBTTagCompound nbt){
			this.query = deserializeST(nbt);
		}

	}

	protected static class GetStoredAmountReply<ST> implements TypedRRNetworkStorage.Request.GetStoredAmount.Reply<ST> {

		protected int amount;

		public GetStoredAmountReply(int amount){
			this.amount = amount;
		}

		@Override
		public int amountStored(){
			return amount;
		}
	}

	protected class GetSubtypesCount implements SubtypedRRNetworkStorage.Request.GetSubtypesCount<T> {

		protected T query;

		protected GetSubtypesCount(){}

		public GetSubtypesCount(T query){
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

	protected static class GetSubtypesCountReply<T> implements SubtypedRRNetworkStorage.Request.GetSubtypesCount.Reply<T> {

		protected int count;

		public GetSubtypesCountReply(int count){
			this.count = count;
		}

		@Override
		public int subtypesCount(){
			return count;
		}
	}

	protected class GetTotalAmountStored implements SubtypedRRNetworkStorage.Request.GetTotalAmountStored<T> {

		protected T query;

		protected GetTotalAmountStored(){}

		public GetTotalAmountStored(T query){
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

	protected static class GetTotalAmountStoredReply<T> implements SubtypedRRNetworkStorage.Request.GetTotalAmountStored.Reply<T> {

		protected int amount;

		public GetTotalAmountStoredReply(int amount){
			this.amount = amount;
		}

		@Override
		public int totalAmountStored(){
			return amount;
		}
	}

	protected class Store extends RequestWithConsumer<TypedRRNetworkStorage.Request.Store.Reply<ST>> implements TypedRRNetworkStorage.Request.Store<ST> {

		protected ST query;
		protected int min, max;

		public Store(){}

		public Store(ST query, int min, int max, Consumer<Reply<ST>> replyConsumer){
			super(replyConsumer);
			this.query = query;
			this.min = min;
			this.max = max;
		}

		@Override
		public ST query(){
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
			nbt.setTag("query", serializeST(query));
			nbt.setInteger("min", min);
			nbt.setInteger("max", max);
			return nbt;
		}

		@Override
		public void deserializeNBT(NBTTagCompound nbt){
			this.query = deserializeST(nbt.getCompoundTag("query"));
			min = nbt.getInteger("min");
			max = nbt.getInteger("max");
		}

	}

	protected static class StoreReply<ST> implements TypedRRNetworkStorage.Request.Store.Reply<ST> {

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

	protected final Function<T, NBTTagCompound> serializerT;
	protected final Function<NBTTagCompound, T> deserializerT;

	protected NBTTagCompound serializeT(T t){
		return serializerT.apply(t);
	}

	protected T deserializeT(NBTTagCompound nbt){
		return deserializerT.apply(nbt);
	}

	protected final Function<ST, NBTTagCompound> serializerST;
	protected final Function<NBTTagCompound, ST> deserializerST;

	protected NBTTagCompound serializeST(ST st){
		return serializerST.apply(st);
	}

	protected ST deserializeST(NBTTagCompound nbt){
		return deserializerST.apply(nbt);
	}

	@Override
	public NBTTagCompound serializeNBT(){
		NBTTagCompound nbt = super.serializeNBT();
		NBTTagList storage = new NBTTagList();
		this.subtypes.forEach((t, st) -> {
			if(!st.subtypes.isEmpty()){
				NBTTagCompound next = new NBTTagCompound();
				next.setTag("object", serializeT(t));
				next.setTag("subtypes", st.serializeNBT());
				storage.appendTag(next);
			}
		});
		nbt.setTag("storage", storage);
		return nbt;
	}

	@Override
	public void deserializeNBT(NBTTagCompound nbt){
		super.deserializeNBT(nbt);
		this.subtypes.clear();
		NBTTagList storage = (NBTTagList) nbt.getTag("storage");
		storage.forEach(nbtBase -> {
			NBTTagCompound next = (NBTTagCompound) nbtBase;
			Subtypes subtypes = new Subtypes();
			subtypes.deserializeNBT(next.getCompoundTag("subtypes"));
			this.subtypes.put(deserializeT(next.getCompoundTag("object")), subtypes);
		});
	}

}
