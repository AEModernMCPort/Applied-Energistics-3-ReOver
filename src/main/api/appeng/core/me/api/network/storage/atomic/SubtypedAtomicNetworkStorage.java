package appeng.core.me.api.network.storage.atomic;

public interface SubtypedAtomicNetworkStorage<ST, T> extends TypedAtomicNetworkStorage<ST> {

	/**
	 * Gets the current total amount of all stored objects of given type.
	 *
	 * @param t type
	 * @return current total amount of all stored objects of given type
	 */
	int getTotalSubtypesStored(T t);

	/**
	 * Gets the number of different subtypes of given type currently stored.
	 *
	 * @param t type
	 * @return number of different subtypes of given type currently stored
	 */
	int getStoredSubtypesCount(T t);

}
