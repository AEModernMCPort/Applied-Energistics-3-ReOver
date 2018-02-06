package appeng.core.me.api.network.storage;

import java.util.function.Consumer;

public interface TypedNetworkStorage<T, NS extends TypedNetworkStorage<T, NS, ReadReq, ReadRep, WriteReq, WriteRep>, ReadReq extends TypedNetworkStorage.Request<ReadRep>, ReadRep, WriteReq extends TypedNetworkStorage.Request<WriteRep>, WriteRep> extends NetworkStorage<NS, ReadReq, ReadRep, WriteReq, WriteRep> {

	/**
	 * Posts a {@linkplain Request.GetStoredAmount get stored amount} request to get the amount stored of given object.<br>
	 *
	 * @param t             {@linkplain Request.GetStoredAmount#query() object to query}
	 * @return {@linkplain Request.GetStoredAmount.Reply#amountStored() amount stored of given object}
	 */
	int getStoredAmount(T t);

	/**
	 * Posts a {@linkplain Request.Store store} request to store given amount of given object
	 *
	 * @param t             {@linkplain Request.Store#query() object to query}
	 * @param minAmount     {@linkplain Request.Store#minAmount() minimum amount to store}
	 * @param maxAmount     {@linkplain Request.Store#maxAmount() maximum amount to store}
	 * @param replyConsumer {@linkplain Request.Store.Reply reply} processor
	 */
	void store(T t, int minAmount, int maxAmount, Consumer<Request.Store.Reply<T>> replyConsumer);

	interface Request<Rep> extends NetworkStorage.Request<Rep> {

		/**
		 * Requests the amount stored of an object.
		 * <br><br>
		 * Read request.
		 *
		 * @param <T> object type
		 */
		interface GetStoredAmount<T> extends Request<GetStoredAmount.Reply<T>> {

			/**
			 * Get the object to query
			 *
			 * @return object to query
			 */
			T query();

			interface Reply<T> {

				/**
				 * Current amount stored of queried object
				 *
				 * @return amount stored
				 */
				int amountStored();

			}

		}

		/**
		 * Stores/Extracts as much as possible of given object.<br>
		 * {@linkplain Store#minAmount() Minimum amount} and {@linkplain Store#maxAmount() maximum amount} must be of the same sign - positive for store, negative for extract.<br>
		 * Minimum and maximum amounts can be returned from any of 2 methods, in both cases:
		 * <ul>
		 * <li>In case of store, storage tries to store as much as possible between specified min and max amounts (absolute, unsigned, inclusive) - returns stored amount (positive reply amount) if succeeds or 0 in case of failure.</li>
		 * <li>- In case of extract, storage tries to extract as much as possible between specified min and max amounts (absolute, unsigned, inclusive) - returns extracted amount (negative reply amount) if succeeds or 0 in case of failure.</li>
		 * </ul>
		 * <br><br>
		 * Write request.
		 *
		 * @param <T>
		 */
		interface Store<T> extends Request<Store.Reply<T>> {

			/**
			 * Get the object to query
			 *
			 * @return object to query
			 */
			T query();

			/**
			 * Get the minimum amount to store/extract
			 *
			 * @return minimum amount to store
			 */
			int minAmount();

			/**
			 * Get the maximum amount to store/extract
			 *
			 * @return maximum amount to store
			 */
			int maxAmount();

			interface Reply<T> {

				/**
				 * Amount of object successfully stored/extracted to/from the storage.<br>
				 * Returned amount is positive for store operation, negative for extraction.<br>
				 * Returns <tt>0</tt> if requested amount could not be stored/extracted or the request was invalid.
				 *
				 * @return amount successfully stored
				 */
				int amountStored();

			}

		}

	}

}
