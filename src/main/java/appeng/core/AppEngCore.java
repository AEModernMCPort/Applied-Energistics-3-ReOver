
package appeng.core;


import javax.annotation.Nonnull;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLInterModComms.IMCEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerAboutToStartEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.event.FMLServerStoppedEvent;
import net.minecraftforge.fml.common.event.FMLServerStoppingEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.registry.FMLControlledNamespacedRegistry;
import net.minecraftforge.fml.common.registry.RegistryBuilder;

import appeng.api.definitions.IDefinition;
import appeng.api.definitions.IDefinitions;
import appeng.api.module.Module;
import appeng.api.module.Module.ModuleEventHandler;
import appeng.core.api.ICore;
import appeng.core.api.material.Material;
import appeng.core.definitions.CoreBlockDefinitions;
import appeng.core.definitions.CoreEntityDefinitions;
import appeng.core.definitions.CoreItemDefinitions;
import appeng.core.definitions.CoreMaterialDefinitions;
import appeng.core.definitions.CoreTileDefinitions;
import appeng.core.hooks.TickHandler;
import appeng.core.lib.bootstrap.FeatureFactory;
import appeng.core.lib.sync.GuiBridge;
import appeng.core.lib.sync.network.NetworkHandler;
import appeng.core.lib.worlddata.WorldData;

/*
 * TODO 1.10.2-MODUSEP - Dat giant mess though. Move all stuff that belongs to specific modules into these specific modules. Yes, you can boom the API.
 */
@Module( value = ICore.NAME, dependencies = "hard-before:module-*" )
public class AppEngCore implements ICore
{

	@Module.Instance( NAME )
	public static final AppEngCore INSTANCE = null;

	@SidedProxy( modId = AppEng.MODID, clientSide = "appeng.core.client.AppEngCoreClientProxy", serverSide = "appeng.core.server.AppEngCoreServerProxy" )
	private static AppEngCoreProxy proxy;

	private final Registration registration;

	private FMLControlledNamespacedRegistry<Material> materialRegistry;

	private FeatureFactory registry;

	private CoreItemDefinitions itemDefinitions;
	private CoreBlockDefinitions blockDefinitions;
	private CoreTileDefinitions tileDefinitions;
	private CoreMaterialDefinitions materialDefinitions;
	private CoreEntityDefinitions entityDefinitions;

	public AppEngCore()
	{
		this.registration = new Registration();
	}

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

	@Nonnull
	public final Registration getRegistration()
	{
		return this.registration;
	}

	public FMLControlledNamespacedRegistry<Material> getMaterialRegistry()
	{
		return materialRegistry;
	}

	@ModuleEventHandler
	public void preInit( FMLPreInitializationEvent event )
	{
		materialRegistry = (FMLControlledNamespacedRegistry<Material>) new RegistryBuilder().setName( new ResourceLocation( AppEng.MODID, "material" ) ).setType( Material.class ).setIDRange( 0, Short.MAX_VALUE ).create();

		registry = new FeatureFactory();
		this.materialDefinitions = new CoreMaterialDefinitions( registry );
		this.blockDefinitions = new CoreBlockDefinitions( registry );
		this.itemDefinitions = new CoreItemDefinitions( registry );
		this.tileDefinitions = new CoreTileDefinitions( registry );
		this.entityDefinitions = new CoreEntityDefinitions( registry );
		registry.preInit( event );

		CreativeTab.init();

		this.registration.preInitialize( event );

		proxy.preInit( event );
	}

	private void startService( final String serviceName, final Thread thread )
	{
		thread.setName( serviceName );
		thread.setPriority( Thread.MIN_PRIORITY );

		AELog.info( "Starting " + serviceName );
		thread.start();
	}

	@ModuleEventHandler
	public void init( FMLInitializationEvent event )
	{
		this.registration.initialize( event );

		registry.init( event );

		proxy.init( event );
	}

	@ModuleEventHandler
	public void postInit( FMLPostInitializationEvent event )
	{
		this.registration.postInit( event );

		registry.postInit( event );

		proxy.postInit( event );

		NetworkRegistry.INSTANCE.registerGuiHandler( this, GuiBridge.GUI_Handler );
		NetworkHandler.instance = new NetworkHandler( "AE2" );
	}

	@ModuleEventHandler
	public void handleIMCEvent( IMCEvent event )
	{
		final IMCHandler imcHandler = new IMCHandler();

		imcHandler.handleIMCEvent( event );
	}

	@ModuleEventHandler
	public void serverAboutToStart( FMLServerAboutToStartEvent event )
	{
		WorldData.onServerAboutToStart( event.getServer() );
	}

	@ModuleEventHandler
	public void serverStarting( FMLServerStartingEvent event )
	{
		event.registerServerCommand( new AECommand( event.getServer() ) );
	}

	@ModuleEventHandler
	public void serverStopping( FMLServerStoppingEvent event )
	{
		WorldData.instance().onServerStopping();
	}

	@ModuleEventHandler
	public void serverStopped( FMLServerStoppedEvent event )
	{
		WorldData.instance().onServerStoppped();
		TickHandler.INSTANCE.shutdown();
	}

}
