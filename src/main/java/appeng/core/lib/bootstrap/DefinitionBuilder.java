package appeng.core.lib.bootstrap;

import appeng.api.bootstrap.DefinitionFactory;
import appeng.api.bootstrap.IDefinitionBuilder;
import appeng.api.bootstrap.InitializationComponent;
import appeng.api.definitions.IDefinition;
import appeng.core.AppEng;
import appeng.core.lib.config.GlobalFeaturesManager;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.common.registry.IForgeRegistryEntry;
import net.minecraftforge.fml.relauncher.Side;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public abstract class DefinitionBuilder<I, T, D extends IDefinition<T>, B extends DefinitionBuilder<I, T, D, B>> implements IDefinitionBuilder<T, D, B> {

	protected final DefinitionFactory factory;

	protected final ResourceLocation registryName;
	private final I instance;

	protected ResourceLocation feature;

	protected boolean enabledByDefault = true;

	private final List<Consumer<D>> buildCallbacks = new ArrayList<>();
	private final Multimap<Side, DefinitionInitializationComponent<T, D>> initComponents = HashMultimap.create();

	public DefinitionBuilder(DefinitionFactory factory, ResourceLocation registryName, I instance, ResourceLocation feature){
		this.factory = factory;
		this.registryName = registryName;
		this.instance = instance;
		this.feature = feature;
	}

	public DefinitionBuilder(DefinitionFactory factory, ResourceLocation registryName, I instance, String featurePrefix){
		this(factory, registryName, instance, new ResourceLocation(AppEng.instance().getCurrentName(), featurePrefix + "/" + registryName.getResourcePath()));
	}

	@Override
	public B setFeature(ResourceLocation feature){
		this.feature = feature;
		return (B) this;
	}

	@Override
	public B setEnabledByDefault(boolean enabled){
		this.enabledByDefault = enabled;
		return (B) this;
	}

	@Override
	public B build(Consumer<D> callback){
		buildCallbacks.add(callback);
		return (B) this;
	}

	@Override
	public <I extends DefinitionInitializationComponent<T, D>> B initializationComponent(@Nullable Side side, I init){
		initComponents.put(side, init);
		return (B) this;
	}

	@Override
	public final D build(){
		if(!GlobalFeaturesManager.INSTANCE.isEnabled(feature, enabledByDefault)) return def(null);

		D definition = def(setRegistryName(instance));

		this.<DefinitionInitializationComponent.PreInit<T, D>>initializationComponent(null, t -> register((t).maybe().get()));
		initComponents.entries().forEach((entry) -> factory.initializationHandler(entry.getKey()).accept(new InitComponentPass<>(definition, entry.getValue())));

		buildCallbacks.forEach(consumer -> consumer.accept(definition));

		return definition;
	}

	protected I setRegistryName(I t){
		if(t instanceof IForgeRegistryEntry){
			((IForgeRegistryEntry) t).setRegistryName(registryName);
		}
		return t;
	}

	protected void register(T t){
		if(t instanceof IForgeRegistryEntry){
			GameRegistry.register((IForgeRegistryEntry) t);
		}
	}

	protected abstract D def(@Nullable I t);

	public static class InitComponentPass<T, D extends IDefinition<T>> implements InitializationComponent {

		private final D definition;
		private final DefinitionInitializationComponent<T, D> defInit;

		public InitComponentPass(D definition, DefinitionInitializationComponent<T, D> defInit){
			this.definition = definition;
			this.defInit = defInit;
		}

		@Override
		public void preInit(){
			defInit.preInit(definition);
		}

		@Override
		public void init(){
			defInit.init(definition);
		}

		@Override
		public void postInit(){
			defInit.postInit(definition);
		}
	}

}
