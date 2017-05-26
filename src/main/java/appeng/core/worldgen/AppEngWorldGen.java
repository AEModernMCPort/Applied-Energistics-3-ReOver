
package appeng.core.worldgen;


import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
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
import appeng.core.lib.bootstrap.FeatureFactory;
import appeng.core.worldgen.api.IWorldGen;
import appeng.core.worldgen.definitions.WorldGenBlockDefinitions;
import appeng.core.worldgen.definitions.WorldGenItemDefinitions;
import appeng.core.worldgen.definitions.WorldGenTileDefinitions;


@Module( IWorldGen.NAME )
public class AppEngWorldGen implements IWorldGen
{

	@Module.Instance( NAME )
	public static final AppEngWorldGen INSTANCE = null;

	private FeatureFactory registry;

	private WorldGenItemDefinitions itemDefinitions;
	private WorldGenBlockDefinitions blockDefinitions;
	private WorldGenTileDefinitions tileDefinitions;

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
		return null;
	}

	@ModuleEventHandler
	public void preInit( FMLPreInitializationEvent event )
	{
		registry = new FeatureFactory();
		this.blockDefinitions = new WorldGenBlockDefinitions( registry );
		this.itemDefinitions = new WorldGenItemDefinitions( registry );
		this.tileDefinitions = new WorldGenTileDefinitions( registry );
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
