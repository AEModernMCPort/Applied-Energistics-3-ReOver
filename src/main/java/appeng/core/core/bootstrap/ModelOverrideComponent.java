package appeng.core.core.bootstrap;

import appeng.api.bootstrap.IDefinitionBuilder;
import appeng.api.definitions.IDefinition;
import appeng.core.AppEng;
import com.google.common.collect.Sets;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.util.registry.IRegistry;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.*;
import java.util.function.BiFunction;

/**
 * @author Fredi100
 */
public class ModelOverrideComponent implements IDefinitionBuilder.DefinitionInitializationComponent {

	static{
		MinecraftForge.EVENT_BUS.register(ModelOverrideComponent.class);
	}

	// Maps from resource path to customizer
	private final Map<String, BiFunction<ModelResourceLocation, IBakedModel, IBakedModel>> customizer = new HashMap<>();

	public ModelOverrideComponent addOverride(String resourcePath, BiFunction<ModelResourceLocation, IBakedModel, IBakedModel> customizer){
		this.customizer.put(resourcePath, customizer);
		return this;
	}

	private static List<ModelOverrideComponent> instances = new ArrayList<ModelOverrideComponent>();

	@SubscribeEvent
	public static void sub(ModelBakeEvent event){
		instances.forEach(instance -> instance.handleEvent(event));
	}

	void handleEvent(ModelBakeEvent event){
		IRegistry<ModelResourceLocation, IBakedModel> modelRegistry = event.getModelRegistry();
		Set<ModelResourceLocation> keys = Sets.newHashSet(modelRegistry.getKeys());

		for(ModelResourceLocation location : keys){
			if(!location.getResourceDomain().equals(AppEng.MODID)){
				continue;
			}

			BiFunction<ModelResourceLocation, IBakedModel, IBakedModel> customizer = this.customizer.get(location.getResourcePath());
			if(customizer != null){
				IBakedModel orgModel = modelRegistry.getObject(location);
				IBakedModel newModel = customizer.apply(location, orgModel);

				if(newModel != orgModel){
					modelRegistry.putObject(location, newModel);
				}
			}
		}
	}

	@Override
	public void preInit(IDefinition def){
		System.out.println("Initializing ModelOverrideComponent");
		instances.add(this);
	}
}
