package appeng.core.core.proxy;

import appeng.api.module.AEStateEvent;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class CoreClientProxy extends CoreProxy {

	private List<Consumer<ModelBakeEvent>> modelCustomizers = new ArrayList<>();

	public CoreClientProxy(){
		super(Side.CLIENT);
	}

	@SubscribeEvent
	public void sub(ModelBakeEvent event){
		modelCustomizers.forEach(customizer -> customizer.accept(event));
	}

	@Override
	public void preInit(AEStateEvent.AEPreInitializationEvent event){
		MinecraftForge.EVENT_BUS.register(this);
		super.preInit(event);
	}

	@Override
	public void acceptModelCustomizer(Consumer<ModelBakeEvent> customizer){
		super.acceptModelCustomizer(customizer);
		modelCustomizers.add(customizer);
	}
}
