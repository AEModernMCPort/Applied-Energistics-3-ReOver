package appeng.core.me.proxy;

import appeng.core.me.client.part.ClientPartHelper;
import com.owens.oobjloader.parser.ResourceHelper;
import net.minecraft.client.resources.IResourcePack;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.fml.relauncher.Side;

import java.util.Optional;

public class MEServerProxy extends MEProxy {

	public MEServerProxy(){
		super(Side.SERVER);
	}

	@Override
	public ResourceHelper getResourceHelper(){
		//TODO 1.13 Data packs?
		return location -> MEServerProxy.class.getResourceAsStream("/assets/" + location.getNamespace() + "/" + location.getPath());
	}

	@Override
	public Optional<ClientPartHelper> clientPartHelper(){
		return Optional.empty();
	}

}
