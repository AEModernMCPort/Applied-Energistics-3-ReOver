
package appeng.core.me;


import java.io.File;
import java.util.HashMap;
import java.util.Map;

import com.google.common.collect.BiMap;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerAboutToStartEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.event.FMLServerStoppedEvent;
import net.minecraftforge.fml.common.event.FMLServerStoppingEvent;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.registry.FMLControlledNamespacedRegistry;
import net.minecraftforge.fml.common.registry.IForgeRegistry;
import net.minecraftforge.fml.common.registry.IForgeRegistry.AddCallback;
import net.minecraftforge.fml.common.registry.IForgeRegistry.ClearCallback;
import net.minecraftforge.fml.common.registry.IForgeRegistry.CreateCallback;
import net.minecraftforge.fml.common.registry.IForgeRegistry.SubstitutionCallback;
import net.minecraftforge.fml.common.registry.RegistryBuilder;

import appeng.api.definitions.IDefinition;
import appeng.api.definitions.IDefinitions;
import appeng.api.module.Module;
import appeng.api.module.Module.ModuleEventHandler;
import appeng.api.module.ModuleIMCMessageEvent;
import appeng.core.AppEng;
import appeng.core.api.material.Material;
import appeng.core.lib.features.AEFeature;
import appeng.core.me.api.IME;
import appeng.core.me.api.part.PartRegistryEntry;
import appeng.core.me.api.parts.IPart;
import appeng.core.me.bootstrap.MEFeatureFactory;
import appeng.core.me.definitions.MEBlockDefinitions;
import appeng.core.me.definitions.MEEntityDefinitions;
import appeng.core.me.definitions.MEItemDefinitions;
import appeng.core.me.definitions.MEMaterialDefinitions;
import appeng.core.me.definitions.METileDefinitions;
import appeng.core.me.item.ItemCard;


@Module( IME.NAME )
public class AppEngME implements IME
{

	@Module.Instance( NAME )
	public static final AppEngME INSTANCE = null;

	private static final ResourceLocation CLASS2PARTMAP = new ResourceLocation( AppEng.MODID, "class-partrege_map" );

	public final ItemCard.EnumCardType CAPACITY = ItemCard.EnumCardType.addCardType( "CAPACITY" );
	public final ItemCard.EnumCardType REDSTONE = ItemCard.EnumCardType.addCardType( "REDSTONE" );

	public final ItemCard.EnumCardType FUZZY = ItemCard.EnumCardType.addCardType( "FUZZY" );
	public final ItemCard.EnumCardType INVERTER = ItemCard.EnumCardType.addCardType( "INVERTER" );
	public final ItemCard.EnumCardType ACCELERATION = ItemCard.EnumCardType.addCardType( "ACCELERATION" );

	private FMLControlledNamespacedRegistry<PartRegistryEntry> partRegistry;

	private FeatureFactory registry;

	private MEItemDefinitions itemDefinitions;
	private MEBlockDefinitions blockDefinitions;
	private METileDefinitions tileDefinitions;
	private MEMaterialDefinitions materialDefinitions;
	private MEEntityDefinitions entityDefinitions;

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
		if( clas == EntityEntry.class )
		{
			return (D) entityDefinitions;
		}
		return null;
	}

	private Map<Class<?>, ResourceLocation> getClass2PartMap( Map<ResourceLocation, ?> slaveset )
	{
		return (Map<Class<?>, ResourceLocation>) slaveset.get( CLASS2PARTMAP );
	}

	public ResourceLocation getRegistryName( Class<? extends IPart> clas )
	{
		return (ResourceLocation) partRegistry.getSlaveMap( CLASS2PARTMAP, Map.class ).get( clas );
	}

	@ModuleEventHandler
	public void preInit( FMLPreInitializationEvent event )
	{
		registry = new FeatureFactory();
		this.materialDefinitions = new MEMaterialDefinitions( registry );
		this.blockDefinitions = new MEBlockDefinitions( registry );
		this.itemDefinitions = new MEItemDefinitions( registry );
		this.tileDefinitions = new METileDefinitions( registry );
		this.entityDefinitions = new MEEntityDefinitions( registry );
		registry.preInit( event );

		FacadeConfig.instance = new FacadeConfig( new File( AppEng.instance().getConfigDirectory(), "Facades.cfg" ) );
		if( AEConfig.instance.isFeatureEnabled( AEFeature.Facades ) )
		{
			CreativeTabFacade.init();
		}
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
