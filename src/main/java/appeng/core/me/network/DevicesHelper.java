package appeng.core.me.network;

import appeng.api.bootstrap.InitializationComponent;
import appeng.core.AppEng;
import appeng.core.me.AppEngME;
import appeng.core.me.api.network.block.Connection;
import appeng.core.me.network.connect.SPIntConnection;
import net.minecraft.util.ResourceLocation;

import java.util.HashMap;
import java.util.Map;

public class DevicesHelper implements InitializationComponent {

	protected Map<ResourceLocation, Connection> connections = new HashMap<>();

	public void registerConnection(Connection connection){
		connections.put(connection.getId(), connection);
	}

	@Override
	public void init(){
		AppEngME.INSTANCE.registerConnection(new SPIntConnection(new ResourceLocation(AppEng.MODID, "energy")));
	}
}
