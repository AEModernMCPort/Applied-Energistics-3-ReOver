package appeng.core.core.client.render.model;

import appeng.core.AppEng;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.common.model.IModelState;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import org.apache.commons.lang3.mutable.MutableObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

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

	private static Optional<IModel> tryLoad(ResourceLocation location){
		try{
			return Optional.of(ModelLoaderRegistry.getModel(location));
		} catch(Exception e){
			AppEng.logger.error("Could not load model " + location, e);
			return Optional.empty();
		}
	}
	
	public static void loadAndRegisterModel(ResourceLocation modelLocation, ModelResourceLocation registryKey, IModelState state, VertexFormat format, Function<ResourceLocation, TextureAtlasSprite> bakedTextureGetter){
		final MutableObject<Optional<IModel>> model = new MutableObject(Optional.empty());
		acceptRegistryEventListener(() -> model.setValue(tryLoad(modelLocation)));
		acceptBakeEventListener(modelBakeEvent -> model.getValue().ifPresent(iModel -> modelBakeEvent.getModelRegistry().putObject(registryKey, iModel.bake(state, format, bakedTextureGetter))));
	}

}
