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

	private static List<Runnable> modelRegisterers = new ArrayList<>();

	private static List<Consumer<ModelBakeEvent>> modelCustomizers = new ArrayList<>();

	@SubscribeEvent
	public static void registerModels(ModelRegistryEvent event){
		modelRegisterers.forEach(Runnable::run);
	}

	@SubscribeEvent
	public static void sub(ModelBakeEvent event){
		modelCustomizers.forEach(customizer -> customizer.accept(event));
	}

	public static void acceptModelRegisterer(Runnable registerer){
		modelRegisterers.add(registerer);
	}

	public static void acceptModelCustomizer(Consumer<ModelBakeEvent> customizer){
		modelCustomizers.add(customizer);
	}

}
