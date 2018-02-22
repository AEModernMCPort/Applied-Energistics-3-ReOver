package appeng.core.me.network;

import appeng.api.bootstrap.InitializationComponent;
import appeng.core.AppEng;
import appeng.core.me.AppEngME;
import appeng.core.me.api.network.NetDevice;
import appeng.core.me.api.network.block.Connection;
import appeng.core.me.network.connect.ConnectionsParams;
import appeng.core.me.network.connect.DataConnection;
import appeng.core.me.network.connect.SPIntConnection;
import appeng.core.me.parts.part.device.Controller;
import com.google.common.collect.ImmutableMap;
import net.minecraft.util.ResourceLocation;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class DevicesHelper implements InitializationComponent {

	protected Map<ResourceLocation, Connection> connections = new HashMap<>();

	public void registerConnection(Connection connection){
		connections.put(connection.getId(), connection);
	}

	public <P extends Comparable<P>> Connection<P, ?> getConnection(ResourceLocation id){
		return connections.get(id);
	}

	public void forEachConnection(Consumer<Connection> consumer){
		connections.values().forEach(consumer);
	}

	@Deprecated
	public Connection ENERGY = new SPIntConnection(new ResourceLocation(AppEng.MODID, "energy"));
	@Deprecated
	public Connection DATA = new DataConnection(new ResourceLocation(AppEng.MODID, "data"));

	@Override
	public void init(){
		AppEngME.INSTANCE.registerConnection(ENERGY);
		AppEngME.INSTANCE.registerConnection(DATA);
	}

	public ConnectionsParams gatherConnectionsParams(NetDevice device){
		if(device instanceof Controller) return new ConnectionsParams(ImmutableMap.of(ENERGY, 100, DATA, new DataConnection.Params(192, 500)));
		else return new ConnectionsParams(ImmutableMap.of(ENERGY, 10, DATA, new DataConnection.Params(1, 10)));
	}

}
