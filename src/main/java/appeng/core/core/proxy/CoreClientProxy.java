package appeng.core.core.proxy;

import appeng.api.module.AEStateEvent;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
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

	private List<Runnable> modelRegisterers = new ArrayList<>();

	@SubscribeEvent
	public void registerModels(ModelRegistryEvent event){
		modelRegisterers.forEach(Runnable::run);
	}

	@Override
	public void acceptModelRegisterer(Runnable registerer){
		modelRegisterers.add(registerer);
	}

	private List<Consumer<ModelBakeEvent>> modelCustomizers = new ArrayList<>();

	@SubscribeEvent
	public void sub(ModelBakeEvent event){
		modelCustomizers.forEach(customizer -> customizer.accept(event));
	}

	@Override
	public void acceptModelCustomizer(Consumer<ModelBakeEvent> customizer){
		super.acceptModelCustomizer(customizer);
		modelCustomizers.add(customizer);
	}
}
