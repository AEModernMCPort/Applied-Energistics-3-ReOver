package appeng.core.core.client.render.model;

import appeng.core.AppEng;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

@Mod.EventBusSubscriber(modid = AppEng.MODID, value = Side.CLIENT)
public class ModelRegManagerHelper {

	private static List<Runnable> registryEventListeners = new ArrayList<>();

	private static List<Consumer<ModelBakeEvent>> bakeEventListeners = new ArrayList<>();

	@SubscribeEvent
	public static void registerModels(ModelRegistryEvent event){
		registryEventListeners.forEach(Runnable::run);
	}

	@SubscribeEvent
	public static void sub(ModelBakeEvent event){
		bakeEventListeners.forEach(customizer -> customizer.accept(event));
	}

	public static void acceptRegistryEventListener(Runnable registerer){
		registryEventListeners.add(registerer);
	}

	public static void acceptBakeEventListener(Consumer<ModelBakeEvent> customizer){
		bakeEventListeners.add(customizer);
	}

}
