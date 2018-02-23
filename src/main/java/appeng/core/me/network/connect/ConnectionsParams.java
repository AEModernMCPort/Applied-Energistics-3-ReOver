package appeng.core.me.network.connect;

import appeng.core.me.api.network.block.Connection;
import com.google.common.collect.ImmutableMap;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public final class ConnectionsParams<IntP extends Comparable<IntP>> {

	@Nullable
	public static <IntP extends Comparable<IntP>> ConnectionsParams<IntP> join(@Nullable ConnectionsParams<IntP> params1, @Nullable ConnectionsParams<IntP> params2){
		if(params1 == null && params2 == null) return null;
		else if(params1 == null) return params2;
		else if(params2 == null) return params1;
		else {
			Map<Connection<IntP, ?>, IntP> joined = new HashMap<>();
			Collection<Connection<IntP, ?>> cs2 = params2.params.keySet();
			params1.params.keySet().stream().filter(cs2::contains).forEach(c -> joined.put(c, c.join(params1.getParam(c), params2.getParam(c))));
			return new ConnectionsParams(joined);
		}
	}

	private final Map<Connection<IntP, ?>, IntP> params;

	public ConnectionsParams(Map<Connection<IntP, ?>, IntP> params){
		this.params = ImmutableMap.copyOf(params);
	}

	@Nullable
	public <P extends Comparable<P>> P getParam(Connection<P, ?> connection){
		return (P) params.get(connection);
	}

	public boolean hasNoParams(){
		return params.isEmpty();
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
