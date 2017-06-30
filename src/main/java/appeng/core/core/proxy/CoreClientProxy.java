package appeng.core.core.proxy;

import appeng.api.module.AEStateEvent;
import appeng.core.core.client.render.model.ModelRegManagerHelper;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.common.MinecraftForge;
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

		/*modelRegisterers.add(() -> AppEngCore.INSTANCE.getMaterialRegistry().forEach(material -> {
			try{
				ModelLoaderRegistry.getModel(material.getModel());
			} catch(Exception e){
				e.printStackTrace();
			}
		}));*/
	}

	@Override
	public void acceptModelRegisterer(Runnable registerer){
		ModelRegManagerHelper.acceptRegistryEventListener(registerer);
	}

	@Override
	public void acceptModelCustomizer(Consumer<ModelBakeEvent> customizer){
		ModelRegManagerHelper.acceptBakeEventListener(customizer);
	}

}
