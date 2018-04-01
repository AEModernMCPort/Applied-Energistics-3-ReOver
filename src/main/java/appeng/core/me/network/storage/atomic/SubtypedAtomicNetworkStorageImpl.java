package appeng.core.me.network.storage.atomic;

import appeng.core.me.api.network.storage.atomic.SubtypedAtomicNetworkStorage;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.util.INBTSerializable;
import org.apache.commons.lang3.mutable.MutableInt;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;

public class SubtypedAtomicNetworkStorageImpl<ST, T> implements SubtypedAtomicNetworkStorage<ST, T> {

	public SubtypedAtomicNetworkStorageImpl(Function<ST, T> getType, Function<T, NBTTagCompound> serializerT, Function<NBTTagCompound, T> deserializerT, Function<ST, NBTTagCompound> serializerST, Function<NBTTagCompound, ST> deserializerST){
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

	protected AtomicInteger totalStored = new AtomicInteger();
	protected ConcurrentMap<T, Subtypes> storage = new ConcurrentHashMap<>();

	@Override
	public int getTotalStored(){
		return totalStored.get();
	}

	@Override
	public int getStoredTypesCount(){
		return storage.size();
	}

	@Override
	public int getTotalSubtypesStored(T t){
		return Optional.ofNullable(storage.get(t)).map(st -> st.totalStored.get()).orElse(0);
	}

	@Override
	public int getStoredSubtypesCount(T t){
		return Optional.ofNullable(storage.get(t)).map(st -> st.subtypes.size()).orElse(0);
	}

	@Override
	public int getStoredAmount(ST st){
		return Optional.ofNullable(storage.get(getType(st))).map(sts -> sts.getStoredAmount(st)).orElse(0);
	}

	/**
	 * The maximum amount of object that can be stored/extracted.<br>
	 * <b>Must be atomic and side effect free.</b>
	 *
	 * @param t             object
	 * @param currentAmount currently stored amount
	 * @param store         true if store, false if extract
	 * @return maximum amount that can be stored/extracted, unsigned (positive) in both cases
	 */
	protected int getMaxStore(ST t, int currentAmount, boolean store){
		return store ? Integer.MAX_VALUE : currentAmount;
	}

	@Override
	public int store(ST st, int minAmount, int maxAmount){
		assert minAmount != 0 && maxAmount != 0 && Math.signum(minAmount) == Math.signum(maxAmount);
		T t = getType(st);
		boolean store = Math.signum(maxAmount) == 1;
		int min = Math.min(Math.abs(minAmount), Math.abs(maxAmount));
		int max = Math.max(Math.abs(minAmount), Math.abs(maxAmount));
		Subtypes subtypes = storage.getOrDefault(t, newSubtypes());
		int res = (store ? storage.computeIfAbsent(t, kt -> newSubtypes()) : storage.getOrDefault(t, newSubtypes())).store(st, store, min, max);
		totalStored.addAndGet(res);
		return res;
	}

	protected Subtypes newSubtypes(){
		return new Subtypes();
	}

	protected class Subtypes implements INBTSerializable<NBTTagCompound> {

		protected AtomicInteger totalStored = new AtomicInteger();
		protected ConcurrentMap<ST, AtomicInteger> subtypes = new ConcurrentHashMap<>();

		protected int getStoredAmount(ST st){
			return subtypes.getOrDefault(st, new AtomicInteger()).get();
		}

		protected int store(ST st, boolean store, int min, int max){
			MutableInt sres = new MutableInt(); //Same invocation, same thread
			(store ? subtypes.computeIfAbsent(st, kt -> new AtomicInteger()) : subtypes.getOrDefault(st, new AtomicInteger())).updateAndGet(amount -> {
				int stored;
				int can = getMaxStore(st, amount, store);
				if(can < min){
					stored = 0;
				} else if(can > max){
					stored = max;
					amount += store ? max : -max;
				} else {
					amount += store ? can : -can;
					stored = can;
				}
				sres.setValue(stored);
				return amount;
			});
			int res = store ? sres.getValue() : -sres.getValue();
			totalStored.addAndGet(res);
			return res;
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
				int amount = next.getInteger("amount");
				this.subtypes.put(deserializeST(next.getCompoundTag("subtype")), new AtomicInteger(amount));
				totalStored.addAndGet(amount);
			});
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
		NBTTagCompound nbt = new NBTTagCompound();
		NBTTagList storage = new NBTTagList();
		this.storage.forEach((t, st) -> {
			if(st.totalStored.get() != 0){
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
		this.storage.clear();
		NBTTagList storage = (NBTTagList) nbt.getTag("storage");
		storage.forEach(nbtBase -> {
			NBTTagCompound next = (NBTTagCompound) nbtBase;
			Subtypes subtypes = newSubtypes();
			subtypes.deserializeNBT(next.getCompoundTag("subtypes"));
			this.storage.put(deserializeT(next.getCompoundTag("object")), subtypes);
		});
	}

}
