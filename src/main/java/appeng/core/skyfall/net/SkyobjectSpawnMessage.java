package appeng.core.skyfall.net;

import appeng.core.skyfall.api.skyobject.Skyobject;
import appeng.core.skyfall.api.skyobject.SkyobjectProvider;
import appeng.core.skyfall.skyobject.SkyobjectsManagerImpl;
import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import org.apache.commons.lang3.tuple.Pair;

import java.util.UUID;

public class SkyobjectSpawnMessage<S extends Skyobject<S, P>, P extends SkyobjectProvider<S, P>> implements IMessage {

	public UUID uuid;
	public S skyobject;

	public SkyobjectSpawnMessage(){
	}

	public SkyobjectSpawnMessage(UUID uuid, S skyobject){
		this.uuid = uuid;
		this.skyobject = skyobject;
	}

	@Override
	public void toBytes(ByteBuf buf){
		ByteBufUtils.writeTag(buf, SkyobjectsManagerImpl.serializeSkyobject(this.uuid, this.skyobject));
	}

	@Override
	public void fromBytes(ByteBuf buf){
		Pair<UUID, S> skyobject = SkyobjectsManagerImpl.deserializeSkyobject(ByteBufUtils.readTag(buf));
		this.uuid = skyobject.getKey();
		this.skyobject = skyobject.getValue();
	}

}
