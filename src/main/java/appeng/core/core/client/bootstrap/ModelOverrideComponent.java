package appeng.core.core.client.bootstrap;

import appeng.api.bootstrap.IDefinitionBuilder;
import appeng.api.definitions.IDefinition;
import appeng.core.AppEng;
import appeng.core.core.AppEngCore;
import com.google.common.collect.Sets;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.util.registry.IRegistry;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Consumer;

/**
 * @author Fredi100
 */
public class ModelOverrideComponent implements IDefinitionBuilder.DefinitionInitializationComponent {

	private final Consumer<ModelBakeEvent> customizer;

	public ModelOverrideComponent(Consumer<ModelBakeEvent> customizer){
		this.customizer = customizer;
	}

	@Override
	public void preInit(IDefinition def){
		AppEngCore.proxy.acceptModelCustomizer(customizer);
	}

}
