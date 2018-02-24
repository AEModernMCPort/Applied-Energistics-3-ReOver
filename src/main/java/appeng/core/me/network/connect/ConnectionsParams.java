package appeng.core.me.network.connect;

import appeng.core.me.api.network.block.Connection;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;

public final class ConnectionsParams<IntP extends Comparable<IntP>> {

	private interface CPBO<IntP extends Comparable<IntP>> {

		IntP apply(@Nonnull Connection<IntP, ?> connection, @Nonnull IntP param1, @Nonnull IntP param2);

	}

	private static <IntP extends Comparable<IntP>> ConnectionsParams<IntP> binop(@Nullable ConnectionsParams<IntP> params1, @Nullable ConnectionsParams<IntP> params2, Supplier<ConnectionsParams<IntP>> bNN){
		if(params1 == null && params2 == null) return null;
		else if(params1 == null) return params2;
		else if(params2 == null) return params1;
		else return bNN.get();
	}

	private static <IntP extends Comparable<IntP>> ConnectionsParams<IntP> binopInter(@Nullable ConnectionsParams<IntP> params1, @Nullable ConnectionsParams<IntP> params2, @Nonnull CPBO<IntP> CPBO){
		return binop(params1, params2, () -> {
			Map<Connection<IntP, ?>, IntP> isect = new HashMap<>();
			params1.params.keySet().stream().filter(params2.params::containsKey).forEach(c -> isect.put(c, CPBO.apply(c, params1.getParam(c), params2.getParam(c))));
			return new ConnectionsParams<>(isect);
		});
	}

	private static <IntP extends Comparable<IntP>> ConnectionsParams<IntP> binopUnion(@Nullable ConnectionsParams<IntP> params1, @Nullable ConnectionsParams<IntP> params2, @Nonnull CPBO<IntP> CPBO){
		return binop(params1, params2, () -> {
			Map<Connection<IntP, ?>, IntP> union = new HashMap<>();
			Set<Connection<IntP, ?>> all = new HashSet<>();
			all.addAll(params1.params.keySet());
			all.addAll(params2.params.keySet());
			all.forEach(c -> {
				IntP p1 = params1.getParam(c);
				IntP p2 = params2.getParam(c);
				union.put(c, p1 == null ? p2 : p2 == null ? p1 : CPBO.apply(c, p1, p2));
			});
			return new ConnectionsParams<>(union);
		});
	}

	private static <IntP extends Comparable<IntP>> ConnectionsParams<IntP> binopKeep1(@Nullable ConnectionsParams<IntP> params1, @Nullable ConnectionsParams<IntP> params2, @Nonnull CPBO<IntP> CPBO){
		return binop(params1, params2, () -> {
			Map<Connection<IntP, ?>, IntP> keep1 = new HashMap<>();
			params1.params.keySet().stream().forEach(c -> keep1.put(c, !params2.params.containsKey(c) ? params1.getParam(c) : CPBO.apply(c, params1.getParam(c), params2.getParam(c))));
			return new ConnectionsParams<>(keep1);
		});
	}

	@Nullable
	public static <IntP extends Comparable<IntP>> ConnectionsParams<IntP> intersect(@Nullable ConnectionsParams<IntP> params1, @Nullable ConnectionsParams<IntP> params2){
		return binopInter(params1, params2, (connection, param1, param2) -> connection.intersect(param1, param2));
	}

	@Nullable
	public static <IntP extends Comparable<IntP>> ConnectionsParams<IntP> union(@Nullable ConnectionsParams<IntP> params1, @Nullable ConnectionsParams<IntP> params2){
		return binopUnion(params1, params2, (connection, param1, param2) -> connection.union(param1, param2));
	}

	@Nullable
	public static <IntP extends Comparable<IntP>> ConnectionsParams<IntP> addContained(@Nullable ConnectionsParams<IntP> params1, @Nullable ConnectionsParams<IntP> params2){
		return binopKeep1(params1, params2, (connection, param1, param2) -> connection.add(param1, param2));
	}

	@Nullable
	public static <IntP extends Comparable<IntP>> ConnectionsParams<IntP> subtractContained(@Nullable ConnectionsParams<IntP> params, @Nullable ConnectionsParams<IntP> sub){
		return binopKeep1(params, sub, (connection, param1, param2) -> connection.subtract(param1, param2));
	}

	private final Map<Connection<IntP, ?>, IntP> params;

	public ConnectionsParams(Map<Connection<IntP, ?>, IntP> params){
		this.params = ImmutableMap.copyOf(params);
	}

	@Nullable
	public <P extends Comparable<P>> P getParam(Connection<P, ?> connection){
		return (P) params.get(connection);
	}

	public boolean hasParams(){
		return !params.isEmpty();
	}

	public boolean hasNoParams(){
		return params.isEmpty();
	}

	public Collection<Connection<IntP, ?>> getAllConnections(){
		return params.keySet();
	}

	@Nonnull
	public ConnectionsParams<IntP> mul(Function<Connection<IntP, ?>, Double> c2d){
		return new ConnectionsParams<>(Maps.transformEntries(params, (c, p) -> c.mul(p, c2d.apply(c))));
	}

	@Override
	public boolean equals(Object o){
		if(this == o) return true;
		if(!(o instanceof ConnectionsParams)) return false;
		ConnectionsParams<?> that = (ConnectionsParams<?>) o;
		return Objects.equals(params, that.params);
	}

	@Override
	public int hashCode(){
		return Objects.hash(params);
	}

	@Override
	public String toString(){
		return "ConnectionsParams{" + params + '}';
	}

}
