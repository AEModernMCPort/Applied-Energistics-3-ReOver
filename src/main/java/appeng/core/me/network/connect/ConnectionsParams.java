package appeng.core.me.network.connect;

import appeng.core.me.api.network.block.Connection;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

public final class ConnectionsParams<IntP extends Comparable<IntP>> {

	private interface CPBO<IntP extends Comparable<IntP>> {

		IntP apply(@Nonnull Connection<IntP, ?> connection, @Nonnull IntP param1, @Nonnull IntP param2);

	}

	private static <IntP extends Comparable<IntP>> ConnectionsParams<IntP> binop(@Nullable ConnectionsParams<IntP> params1, @Nullable ConnectionsParams<IntP> params2, @Nonnull CPBO<IntP> CPBO){
		if(params1 == null && params2 == null) return null;
		else if(params1 == null) return params2;
		else if(params2 == null) return params1;
		else {
			Map<Connection<IntP, ?>, IntP> isect = new HashMap<>();
			Collection<Connection<IntP, ?>> cs2 = params2.params.keySet();
			params1.params.keySet().stream().filter(cs2::contains).forEach(c -> isect.put(c, CPBO.apply(c, params1.getParam(c), params2.getParam(c))));
			return new ConnectionsParams<>(isect);
		}
	}

	@Nullable
	public static <IntP extends Comparable<IntP>> ConnectionsParams<IntP> intersect(@Nullable ConnectionsParams<IntP> params1, @Nullable ConnectionsParams<IntP> params2){
		return binop(params1, params2, (connection, param1, param2) -> connection.intersect(param1, param2));
	}

	@Nullable
	public static <IntP extends Comparable<IntP>> ConnectionsParams<IntP> union(@Nullable ConnectionsParams<IntP> params1, @Nullable ConnectionsParams<IntP> params2){
		return binop(params1, params2, (connection, param1, param2) -> connection.union(param1, param2));
	}

	@Nullable
	public static <IntP extends Comparable<IntP>> ConnectionsParams<IntP> add(@Nullable ConnectionsParams<IntP> params1, @Nullable ConnectionsParams<IntP> params2){
		return binop(params1, params2, (connection, param1, param2) -> connection.add(param1, param2));
	}

	@Nullable
	public static <IntP extends Comparable<IntP>> ConnectionsParams<IntP> subtract(@Nullable ConnectionsParams<IntP> params, @Nullable ConnectionsParams<IntP> sub){
		return binop(params, sub, (connection, param1, param2) -> connection.subtract(param1, param2));
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
