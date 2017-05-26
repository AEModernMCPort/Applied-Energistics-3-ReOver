
package appeng.core.lib.bootstrap;


import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.function.Consumer;

import javax.annotation.Nullable;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.common.registry.IForgeRegistryEntry;

import appeng.api.definitions.IDefinition;
import appeng.core.lib.bootstrap.components.InitComponent;
import appeng.core.lib.bootstrap.components.PostInitComponent;
import appeng.core.lib.bootstrap.components.PreInitComponent;
import appeng.core.lib.features.AEFeature;


public abstract class DefinitionBuilder<I, T, D extends IDefinition<T>, B extends DefinitionBuilder<I, T, D, B>> implements IDefinitionBuilder<T, D, B>
{

	protected final FeatureFactory factory;

	protected final ResourceLocation registryName;

	private final I instance;

	protected final EnumSet<AEFeature> features = EnumSet.noneOf( AEFeature.class );

	private final List<Consumer<D>> buildCallbacks = new ArrayList<>();
	private final List<Consumer<D>> preInitCallbacks = new ArrayList<>();
	private final List<Consumer<D>> initCallbacks = new ArrayList<>();
	private final List<Consumer<D>> postInitCallbacks = new ArrayList<>();

	public DefinitionBuilder( FeatureFactory factory, ResourceLocation registryName, I instance )
	{
		this.factory = factory;
		this.registryName = registryName;
		this.instance = instance;
	}

	@Override
	public B features( AEFeature... features )
	{
		this.features.clear();
		addFeatures( features );
		return (B) this;
	}

	@Override
	public B addFeatures( AEFeature... features )
	{
		Collections.addAll( this.features, features );
		return (B) this;
	}

	@Override
	public B build( Consumer<D> callback )
	{
		buildCallbacks.add( callback );
		return (B) this;
	}

	@Override
	public B preInit( Consumer<D> callback )
	{
		preInitCallbacks.add( callback );
		return (B) this;
	}

	@Override
	public B init( Consumer<D> callback )
	{
		initCallbacks.add( callback );
		return (B) this;
	}

	@Override
	public B postInit( Consumer<D> callback )
	{
		postInitCallbacks.add( callback );
		return (B) this;
	}

	@Override
	public final D build()
	{
		if( !AEConfig.instance.areFeaturesEnabled( features ) )
		{
			return def( null );
		}

		D definition = def( setRegistryName( instance ) );

		preInitCallbacks.add( t -> register( ( t ).maybe().get() ) );
		preInitCallbacks.forEach( consumer -> factory.<PreInitComponent>addBootstrapComponent( side -> consumer.accept( definition ) ) );
		initCallbacks.forEach( consumer -> factory.<InitComponent>addBootstrapComponent( side -> consumer.accept( definition ) ) );
		postInitCallbacks.forEach( consumer -> factory.<PostInitComponent>addBootstrapComponent( side -> consumer.accept( definition ) ) );

		buildCallbacks.forEach( consumer -> consumer.accept( definition ) );

		return definition;
	}

	protected I setRegistryName( I t )
	{
		if( t instanceof IForgeRegistryEntry )
		{
			( (IForgeRegistryEntry) t ).setRegistryName( registryName );
		}
		return t;
	}

	protected void register( T t )
	{
		if( t instanceof IForgeRegistryEntry )
		{
			GameRegistry.register( (IForgeRegistryEntry) t );
		}
	}

	protected abstract D def( @Nullable I t );

}
