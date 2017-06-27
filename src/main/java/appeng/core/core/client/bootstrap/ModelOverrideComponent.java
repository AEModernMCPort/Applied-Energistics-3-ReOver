package appeng.core.core.client.bootstrap;

import appeng.api.bootstrap.IDefinitionBuilder;
import appeng.api.definitions.IDefinition;
import appeng.core.core.AppEngCore;
import net.minecraftforge.client.event.ModelBakeEvent;

import java.util.function.Consumer;

/**
 * @author Fredi100
 */
public class ModelOverrideComponent<T, D extends IDefinition<T>> implements IDefinitionBuilder.DefinitionInitializationComponent<T, D> {

	private final Consumer<ModelBakeEvent> customizer;

	public ModelOverrideComponent(Consumer<ModelBakeEvent> customizer){
		this.customizer = customizer;
	}

	@Override
	public void preInit(D def){
		AppEngCore.proxy.acceptModelCustomizer(customizer);
	}

}
