package appeng.debug;

import appeng.api.definitions.IDefinition;
import appeng.api.definitions.IDefinitions;
import appeng.api.module.Module;
import appeng.core.AppEng;
import appeng.core.lib.bootstrap.FeatureFactory;
import appeng.debug.definitions.DebugBlockDefinitions;
import appeng.debug.definitions.DebugItemDefinitions;
import appeng.debug.definitions.DebugTileDefinitions;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.ForgeVersion;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.*;

/*
 * The only module not built with gradle.
 */
@Module(AppEngDebug.NAME)
@Mod(modid = AppEngDebug.MODID, name = AppEngDebug.NAME, version = AppEng.VERSION, dependencies = "required-after:" + AppEng.MODID, acceptedMinecraftVersions = ForgeVersion.mcVersion)
public class AppEngDebug {

	public static final String NAME = "debug";

	public static final String MODID = AppEng.MODID + "|" + NAME;

	public static final String MODNAME = AppEng.NAME + " | " + NAME;

	private DebugItemDefinitions itemDefinitions;
	private DebugBlockDefinitions blockDefinitions;
	private DebugTileDefinitions tileDefinitions;

	public <T, D extends IDefinitions<T, ? extends IDefinition<T>>> D definitions(Class<T> clas){
		if(clas == Item.class){
			return (D) itemDefinitions;
		}
		if(clas == Block.class){
			return (D) blockDefinitions;
		}
		if(clas == TileEntity.class){
			return (D) tileDefinitions;
		}
		return null;
	}

	@EventHandler
	public void preInit(FMLPreInitializationEvent event){
		FeatureFactory registry = new FeatureFactory();
		this.itemDefinitions = new DebugItemDefinitions(registry);
		this.blockDefinitions = new DebugBlockDefinitions(registry);
		this.tileDefinitions = new DebugTileDefinitions(registry);
	}

	@EventHandler
	public void init(final FMLInitializationEvent event){

	}

	@EventHandler
	public void postInit(final FMLPostInitializationEvent event){

	}

	@EventHandler
	public void serverAboutToStart(FMLServerAboutToStartEvent event){

	}

	@EventHandler
	public void serverStarting(FMLServerStartingEvent event){

	}

	@EventHandler
	public void serverStopping(FMLServerStoppingEvent event){

	}

	@EventHandler
	public void serverStopped(FMLServerStoppedEvent event){

	}

}
