package appeng.core.core.proxy;

import appeng.api.module.AEStateEvent;
import appeng.core.api.material.Material;
import appeng.core.core.AppEngCore;
import appeng.core.core.client.render.model.ModelRegManagerHelper;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;

import java.util.function.Consumer;

public class CoreClientProxy extends CoreProxy {

	public CoreClientProxy(){
		super(Side.CLIENT);
	}

	@Override
	public void preInit(AEStateEvent.AEPreInitializationEvent event){
		MinecraftForge.EVENT_BUS.register(this);
		super.preInit(event);
	}

	@Override
	public void acceptModelRegisterer(Runnable registerer){
		ModelRegManagerHelper.acceptRegistryEventListener(registerer);
	}

	@Override
	public void acceptModelCustomizer(Consumer<ModelBakeEvent> customizer){
		ModelRegManagerHelper.acceptBakeEventListener(customizer);
	}
	
	@SubscribeEvent(priority = EventPriority.LOWEST)
	public void allMaterialsRegistered(RegistryEvent.Register<Material> event){
		AppEngCore.INSTANCE.getMaterialRegistry().forEach(material -> ModelRegManagerHelper.loadAndRegisterModel(new ModelResourceLocation(material.getModel(), "inventory"), material.getModel(), DefaultVertexFormats.ITEM));
	}

}
