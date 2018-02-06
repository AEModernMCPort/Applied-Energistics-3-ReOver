package appeng.core.me.api.network.storage;

public interface SubtypedNetworkStorage<T, ST, NS extends SubtypedNetworkStorage<T, ST, NS, ReadReq, ReadRep, WriteReq, WriteRep>, ReadReq extends SubtypedNetworkStorage.Request<ReadRep>, ReadRep, WriteReq extends SubtypedNetworkStorage.Request<WriteRep>, WriteRep> extends TypedNetworkStorage<ST, NS, ReadReq, ReadRep, WriteReq, WriteRep> {

	/**
	 * Posts a {@linkplain Request.GetSubtypesCount get subtype count} request to get the count of distinct subtypes of an object
	 *
	 * @param t {@linkplain Request.GetSubtypesCount#query() object to query}
	 * @return {@linkplain Request.GetSubtypesCount.Reply#subtypesCount() count of subtypes}
	 */
	int getSubtypesCount(T t);

	/**
	 * Posts a {@linkplain Request.GetTotalAmountStored get total amount stored} request to get the total amount stored of all subtypes of an object
	 *
	 * @param t {@linkplain Request.GetTotalAmountStored#query() object to query}
	 * @return {@linkplain Request.GetTotalAmountStored.Reply#totalAmountStored() total amount stored}
	 */
	int getTotalAmountStored(T t);

	interface Request<Rep> extends TypedNetworkStorage.Request<Rep> {

		/**
		 * Requests the count of distinct subtypes of an object
		 * <br><br>
		 * Read request.
		 *
		 * @param <T> object type
		 */
		interface GetSubtypesCount<T> extends SubtypedNetworkStorage.Request<GetSubtypesCount.Reply<T>> {

			/**
			 * Get the object to query
			 *
			 * @return object to query
			 */
			T query();

			interface Reply<T> {

				/**
				 * Current count of distinct subtypes of queried object
				 *
				 * @return count of subtypes
				 */
				int subtypesCount();

			}

		}

		/**
		 * Requests the total amount stored of all subtypes of an object
		 * <br><br>
		 * Read request.
		 *
		 * @param <T> object type
		 */
		interface GetTotalAmountStored<T> extends SubtypedNetworkStorage.Request<GetTotalAmountStored.Reply<T>> {

			/**
			 * Get the object to query
			 *
			 * @return object to query
			 */
			T query();

			interface Reply<T> {

				/**
				 * Current total amount stored of all subtypes of queried object
				 *
				 * @return total amount stored
				 */
				int totalAmountStored();

			}

		}

	}

}
