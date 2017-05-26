
package appeng.core.lib.bootstrap;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.google.common.collect.Maps;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.DimensionType;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import appeng.api.definitions.IDefinitionsProvider;
import appeng.api.definitions.IItemDefinition;
import appeng.core.AppEng;
import appeng.core.api.material.Material;
import appeng.core.lib.bootstrap.components.ModelOverrideComponent;
import appeng.core.lib.definitions.BlockDefinition;
import appeng.core.lib.features.AEFeature;
import appeng.core.lib.util.Platform;


public class FeatureFactory
{
	protected final AEFeature[] defaultFeatures;

	private final List<IBootstrapComponent> bootstrapComponents;

	@SideOnly( Side.CLIENT )
	ModelOverrideComponent modelOverrideComponent;

	private final Map<BlockDefinition<? extends Block>, IItemBlockCustomizer<ItemBlock>> defaultItemBlocks = Maps.newHashMap();

	public FeatureFactory()
	{
		this.defaultFeatures = new AEFeature[] { AEFeature.Core };
		this.bootstrapComponents = new ArrayList<>();

		if( Platform.isClient() )
		{
			modelOverrideComponent = new ModelOverrideComponent();
			this.bootstrapComponents.add( modelOverrideComponent );
		}
	}

	protected FeatureFactory( FeatureFactory parent, AEFeature... defaultFeatures )
	{
		this.defaultFeatures = defaultFeatures.clone();
		this.bootstrapComponents = parent.bootstrapComponents;
		if( Platform.isClient() )
		{
			this.modelOverrideComponent = parent.modelOverrideComponent;
		}
	}

	@Deprecated
	public <T extends TileEntity> TileDefinitionBuilder<T> tile( String id, Class<T> tile )
	{
		return tile( new ResourceLocation( AppEng.MODID, id ), tile );
	}

	public <T extends TileEntity> TileDefinitionBuilder<T> tile( ResourceLocation id, Class<T> tile )
	{
		return new TileDefinitionBuilder<T>( this, id, tile, ( (IDefinitionsProvider) AppEng.instance().getCurrent() ).definitions( Block.class ) ).features( defaultFeatures );
	}

	@Deprecated
	public <B extends Block> BlockDefinitionBuilder<B> block( String id, B block )
	{
		return block( new ResourceLocation( AppEng.MODID, id ), block );
	}

	public <B extends Block> BlockDefinitionBuilder<B> block( ResourceLocation id, B block )
	{
		return new BlockDefinitionBuilder<B>( this, id, block ).features( defaultFeatures );
	}

	@Deprecated
	public <I extends Item> ItemDefinitionBuilder<I> item( String id, I item )
	{
		return item( new ResourceLocation( AppEng.MODID, id ), item );
	}

	public <I extends Item> ItemDefinitionBuilder<I> item( ResourceLocation id, I item )
	{
		return new ItemDefinitionBuilder<I>( this, id, item ).features( defaultFeatures );
	}

	@Deprecated
	public <M extends Material> MaterialDefinitionBuilder<M> material( String id, M material )
	{
		return material( new ResourceLocation( AppEng.MODID, id ), material );
	}

	public <M extends Material> MaterialDefinitionBuilder<M> material( ResourceLocation id, M material )
	{
		return new MaterialDefinitionBuilder<M>( this, id, material ).features( defaultFeatures );
	}

	@Deprecated
	public <E extends EntityEntry> EntityDefinitionBuilder<E> entity( String id, E entity )
	{
		return entity( new ResourceLocation( AppEng.MODID, id ), entity );
	}

	public <E extends EntityEntry> EntityDefinitionBuilder<E> entity( ResourceLocation id, E entity )
	{
		return new EntityDefinitionBuilder<E>( this, id, entity ).features( defaultFeatures );
	}

	@Deprecated
	public <B extends Biome> BiomeDefinitionBuilder<B> biome( String id, B biome )
	{
		return biome( new ResourceLocation( AppEng.MODID, id ), biome );
	}

	public <B extends Biome> BiomeDefinitionBuilder<B> biome( ResourceLocation id, B biome )
	{
		return new BiomeDefinitionBuilder<B>( this, id, biome ).features( defaultFeatures );
	}

	@Deprecated
	public <D extends DimensionType> DimensionTypeDefinitionBuilder<D> dimensionType( String id, int did )
	{
		return dimensionType( new ResourceLocation( AppEng.MODID, id ), did );
	}

	public <D extends DimensionType> DimensionTypeDefinitionBuilder<D> dimensionType( ResourceLocation id, int did )
	{
		return new DimensionTypeDefinitionBuilder<D>( this, id, did ).features( defaultFeatures );
	}

	@Deprecated
	public <D extends DimensionType> DimensionTypeDefinitionBuilder<D> dimensionType( String id, int did, String name, String suffix, Class<? extends WorldProvider> clazz, boolean shouldLoadSpawn )
	{
		return dimensionType( new ResourceLocation( AppEng.MODID, id ), did, name, suffix, clazz, shouldLoadSpawn );
	}

	public <D extends DimensionType> DimensionTypeDefinitionBuilder<D> dimensionType( ResourceLocation id, int did, String name, String suffix, Class<? extends WorldProvider> clazz, boolean shouldLoadSpawn )
	{
		return new DimensionTypeDefinitionBuilder<D>( this, id, did, name, suffix, clazz, shouldLoadSpawn ).features( defaultFeatures );
	}

	<B extends Block> void addItemBlock( BlockDefinition<B> def, IItemBlockCustomizer itemBlock )
	{
		defaultItemBlocks.put( def, itemBlock );
	}

	public Map<ResourceLocation, IItemDefinition<Item>> buildDefaultItemBlocks()
	{
		Map result = Maps.newHashMap();
		this.defaultItemBlocks.forEach( ( def, item ) -> result.put( def.identifier(), item.customize( item( def.identifier(), item.createItemBlock( def.maybe().get() ) ) ).build() ) );
		this.defaultItemBlocks.clear();
		return result;
	}

	public FeatureFactory features( AEFeature... features )
	{
		return new FeatureFactory( this, features );
	}

	<B extends IBootstrapComponent> void addBootstrapComponent( B component )
	{
		this.bootstrapComponents.add( component );
	}

	public void preInit( FMLPreInitializationEvent event )
	{
		this.bootstrapComponents.forEach( component -> component.preInit( event.getSide() ) );
	}

	public void init( FMLInitializationEvent event )
	{
		this.bootstrapComponents.forEach( component -> component.init( event.getSide() ) );
	}

	public void postInit( FMLPostInitializationEvent event )
	{
		this.bootstrapComponents.forEach( component -> component.postInit( event.getSide() ) );
	}

}
