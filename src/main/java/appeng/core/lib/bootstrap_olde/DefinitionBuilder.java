package appeng.core.lib.bootstrap_olde;

import appeng.api.bootstrap.InitializationComponent;
import appeng.api.bootstrap.IDefinitionBuilder;
import appeng.api.definitions.IDefinition;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.common.registry.IForgeRegistryEntry;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public abstract class DefinitionBuilder<I, T, D extends IDefinition<T>, B extends DefinitionBuilder<I, T, D, B>>
		implements IDefinitionBuilder<T, D, B> {

	protected final FeatureFactory factory;

	protected final ResourceLocation registryName;

	private final I instance;

	private final List<Consumer<D>> buildCallbacks = new ArrayList<>();
	private final List<Consumer<D>> preInitCallbacks = new ArrayList<>();
	private final List<Consumer<D>> initCallbacks = new ArrayList<>();
	private final List<Consumer<D>> postInitCallbacks = new ArrayList<>();

	public DefinitionBuilder(FeatureFactory factory, ResourceLocation registryName, I instance){
		this.factory = factory;
		this.registryName = registryName;
		this.instance = instance;
	}

	@Override
	public B build(Consumer<D> callback){
		buildCallbacks.add(callback);
		return (B) this;
	}

	@Override
	public B preInit(Consumer<D> callback){
		preInitCallbacks.add(callback);
		return (B) this;
	}

	@Override
	public B init(Consumer<D> callback){
		initCallbacks.add(callback);
		return (B) this;
	}

	@Override
	public B postInit(Consumer<D> callback){
		postInitCallbacks.add(callback);
		return (B) this;
	}

	@Override
	public final D build(){
		D definition = def(setRegistryName(instance));

		preInitCallbacks.add(t -> register((t).maybe().get()));
		preInitCallbacks.forEach(consumer -> factory.<InitializationComponent.PreInit>addBootstrapComponent(() -> consumer.accept(definition)));
		initCallbacks.forEach(consumer -> factory.<InitializationComponent.Init>addBootstrapComponent(() -> consumer.accept(definition)));
		postInitCallbacks.forEach(consumer -> factory.<InitializationComponent.PostInit>addBootstrapComponent(() -> consumer.accept(definition)));

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

}
