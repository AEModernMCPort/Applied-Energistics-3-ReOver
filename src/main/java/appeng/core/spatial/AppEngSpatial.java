
package appeng.core.spatial;


import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.DimensionType;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerAboutToStartEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.event.FMLServerStoppedEvent;
import net.minecraftforge.fml.common.event.FMLServerStoppingEvent;

import appeng.api.definitions.IDefinition;
import appeng.api.definitions.IDefinitions;
import appeng.api.module.Module;
import appeng.api.module.Module.ModuleEventHandler;
import appeng.api.module.ModuleIMCMessageEvent;
import appeng.core.api.material.Material;
import appeng.core.lib.bootstrap.FeatureFactory;
import appeng.core.spatial.api.ISpatial;
import appeng.core.spatial.definitions.SpatialBiomeDefinitions;
import appeng.core.spatial.definitions.SpatialBlockDefinitions;
import appeng.core.spatial.definitions.SpatialDimensionTypeDefinitions;
import appeng.core.spatial.definitions.SpatialItemDefinitions;
import appeng.core.spatial.definitions.SpatialMaterialDefinitions;
import appeng.core.spatial.definitions.SpatialTileDefinitions;


@Module( ISpatial.NAME )
public class AppEngSpatial implements ISpatial
{

	@Module.Instance( NAME )
	public static final AppEngSpatial INSTANCE = null;

	private FeatureFactory registry;

	private SpatialItemDefinitions itemDefinitions;
	private SpatialBlockDefinitions blockDefinitions;
	private SpatialTileDefinitions tileDefinitions;
	private SpatialMaterialDefinitions materialDefinitions;
	private SpatialBiomeDefinitions biomeDefinitions;
	private SpatialDimensionTypeDefinitions dimensionTypeDefinitions;

	@Override
	public <T, D extends IDefinitions<T, ? extends IDefinition<T>>> D definitions( Class<T> clas )
	{
		if( clas == Item.class )
		{
			return (D) itemDefinitions;
		}
		if( clas == Block.class )
		{
			return (D) blockDefinitions;
		}
		if( clas == TileEntity.class )
		{
			return (D) tileDefinitions;
		}
		if( clas == Material.class )
		{
			return (D) materialDefinitions;
		}
		if( clas == Biome.class )
		{
			return (D) biomeDefinitions;
		}
		if( clas == DimensionType.class )
		{
			return (D) dimensionTypeDefinitions;
		}
		return null;
	}

	@ModuleEventHandler
	public void preInit( FMLPreInitializationEvent event )
	{
		registry = new FeatureFactory();
		this.materialDefinitions = new SpatialMaterialDefinitions( registry );
		this.blockDefinitions = new SpatialBlockDefinitions( registry );
		this.itemDefinitions = new SpatialItemDefinitions( registry );
		this.tileDefinitions = new SpatialTileDefinitions( registry );
		this.biomeDefinitions = new SpatialBiomeDefinitions( registry );
		this.dimensionTypeDefinitions = new SpatialDimensionTypeDefinitions( registry );
		registry.preInit( event );
	}

	@ModuleEventHandler
	public void init( FMLInitializationEvent event )
	{
		registry.init( event );
	}

	@ModuleEventHandler
	public void postInit( FMLPostInitializationEvent event )
	{
		registry.postInit( event );
	}

	@ModuleEventHandler
	public void handleIMCEvent( ModuleIMCMessageEvent event )
	{

	}

	@ModuleEventHandler
	public void serverAboutToStart( FMLServerAboutToStartEvent event )
	{

	}

	@ModuleEventHandler
	public void serverStarting( FMLServerStartingEvent event )
	{

	}

	@ModuleEventHandler
	public void serverStopping( FMLServerStoppingEvent event )
	{

	}

	@ModuleEventHandler
	public void serverStopped( FMLServerStoppedEvent event )
	{

	}

}
