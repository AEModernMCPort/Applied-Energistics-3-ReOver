package appeng.core.me.proxy;

import appeng.api.module.AEStateEvent;
import appeng.core.AppEng;
import appeng.core.me.client.part.ClientPartHelper;
import com.owens.oobjloader.parser.ResourceHelper;
import net.minecraft.client.Minecraft;
import net.minecraftforge.client.model.obj.OBJLoader;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.relauncher.Side;

import java.util.Optional;

public class MEClientProxy extends MEProxy {

	private ClientPartHelper partHelper;

	public MEClientProxy(){
		super(Side.CLIENT);
	}

	@Override
	public void preInit(AEStateEvent.AEPreInitializationEvent event){
		MinecraftForge.EVENT_BUS.register(partHelper = new ClientPartHelper());
		OBJLoader.INSTANCE.addDomain(AppEng.MODID);
		super.preInit(event);
	}

	@Override
	public ResourceHelper getResourceHelper(){
		return location -> Minecraft.getMinecraft().getResourceManager().getResource(location).getInputStream();
	}

	@Override
	public Optional<ClientPartHelper> clientPartHelper(){
		return Optional.of(partHelper);
	}
}
