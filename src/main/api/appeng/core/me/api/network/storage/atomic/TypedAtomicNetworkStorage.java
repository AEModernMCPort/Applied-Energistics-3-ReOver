package appeng.core.me.api.network.storage.atomic;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.INBTSerializable;

public interface TypedAtomicNetworkStorage<T> extends INBTSerializable<NBTTagCompound> {

	/**
	 * Gets the current total amount of all stored objects.
	 *
	 * @return current total amount of all stored objects
	 */
	int getTotalStored();

	/**
	 * Gets the number of different types currently stored.
	 *
	 * @return number of different types currently stored
	 */
	int getStoredTypesCount();

	/**
	 * Gets the current stored amount of given object.
	 *
	 * @param t object to query
	 * @return current amount stored of given object
	 */
	int getStoredAmount(T t);

	/**
	 * Tries to store given amount of given object
	 * Stores/Extracts as much as possible of given object.<br>
	 * Minimum amount and maximum amount must be of the same sign - positive for store, negative for extract.<br>
	 * Minimum and maximum amounts can be passed to any of 2 params, in both cases:
	 * <ul>
	 * <li>In case of store, storage tries to store as much as possible between specified min and max amounts (absolute, unsigned, inclusive) - returns stored amount (positive) if succeeds or 0 in case of failure.</li>
	 * <li>- In case of extract, storage tries to extract as much as possible between specified min and max amounts (absolute, unsigned, inclusive) - returns extracted amount (negative) if succeeds or 0 in case of failure.</li>
	 * </ul>
	 *
	 * @param t         object to store
	 * @param minAmount minimum amount to store/extract
	 * @param maxAmount maximum amount to store/extract
	 * @return amount of object successfully stored/extracted to/from the storage, or <tt>0</tt> if requested amount could not be stored/extracted
	 */
	int store(T t, int minAmount, int maxAmount);

}
