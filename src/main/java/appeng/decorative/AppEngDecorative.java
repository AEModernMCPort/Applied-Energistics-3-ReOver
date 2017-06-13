package appeng.decorative;

import appeng.api.bootstrap.DefinitionFactory;
import appeng.api.bootstrap.InitializationComponentsHandler;
import appeng.api.config.ConfigurationLoader;
import appeng.api.definitions.IDefinition;
import appeng.api.definitions.IDefinitions;
import appeng.api.entry.TileRegistryEntry;
import appeng.api.module.AEStateEvent;
import appeng.api.module.Module;
import appeng.api.module.Module.ModuleEventHandler;
import appeng.core.AppEng;
import appeng.core.core.CoreConfig;
import appeng.core.crafting.definitions.CraftingBlockDefinitions;
import appeng.core.crafting.definitions.CraftingItemDefinitions;
import appeng.core.crafting.definitions.CraftingTileDefinitions;
import appeng.core.lib.bootstrap.InitializationComponentsHandlerImpl;
import appeng.decorative.api.IDecorative;
import appeng.decorative.config.DecorativeConfig;
import appeng.decorative.proxy.DecorativeProxy;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.ForgeVersion;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.*;
import net.minecraftforge.fml.common.event.FMLInterModComms.IMCEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;

@Module(IDecorative.NAME)
@Mod(modid = AppEngDecorative.MODID, name = AppEngDecorative.MODNAME, version = AppEng.VERSION, dependencies = "required-after:" + AppEng.MODID, acceptedMinecraftVersions = ForgeVersion.mcVersion)
public class AppEngDecorative implements IDecorative {

	public static final String MODID = AppEng.MODID + "|" + IDecorative.NAME;

	public static final String MODNAME = AppEng.NAME + " | " + IDecorative.NAME;

	public static final Logger logger = LogManager.getLogger(MODID);

	@Module.Instance
	public static final AppEngDecorative INSTANCE = null;

	@SidedProxy(modId = MODID, clientSide = "appeng.decorative.proxy.DecorativeClientProxy", serverSide = "appeng.decorative.proxy.DecorativeServerProxy")
	public static DecorativeProxy proxy;

	public DecorativeConfig config;

	private InitializationComponentsHandler initHandler = new InitializationComponentsHandlerImpl();

	private DefinitionFactory registry;

	private CraftingItemDefinitions itemDefinitions;
	private CraftingBlockDefinitions blockDefinitions;
	private CraftingTileDefinitions tileDefinitions;

	@Override
	public <T, D extends IDefinitions<T, ? extends IDefinition<T>>> D definitions(Class<T> clas){
		if(clas == Item.class){
			return (D) itemDefinitions;
		}
		if(clas == Block.class){
			return (D) blockDefinitions;
		}
		if(clas == TileRegistryEntry.class){
			return (D) tileDefinitions;
		}
		return null;
	}

	@ModuleEventHandler
	public void preInitAE(AEStateEvent.AEPreInitializationEvent event){
		ConfigurationLoader<DecorativeConfig> configLoader = event.configurationLoader();
		try{
			configLoader.load(DecorativeConfig.class);
		} catch(IOException e){
			logger.error("Caught exception loading configuration", e);
		}
		config = configLoader.configuration();

		registry = event.factory(initHandler, proxy);
		this.itemDefinitions = new CraftingItemDefinitions(registry);
		this.blockDefinitions = new CraftingBlockDefinitions(registry);
		this.tileDefinitions = new CraftingTileDefinitions(registry);

		this.itemDefinitions.init(registry);
		this.blockDefinitions.init(registry);
		this.tileDefinitions.init(registry);

		initHandler.preInit();
		proxy.preInit(event);

		try{
			configLoader.save();
		} catch(IOException e){
			logger.error("Caught exception saving configuration", e);
		}
	}

	@EventHandler
	public void preInitForge(FMLPreInitializationEvent event){

	}

	@ModuleEventHandler
	public void initAE(final AEStateEvent.AEInitializationEvent event){
		initHandler.init();
		proxy.init(event);
	}

	@EventHandler
	public void initForge(final FMLInitializationEvent event){

	}

	@ModuleEventHandler
	public void postInitAE(final AEStateEvent.AEPostInitializationEvent event){
		initHandler.postInit();
		proxy.postInit(event);
	}

	@EventHandler
	public void postInitForge(final FMLPostInitializationEvent event){

	}

	@ModuleEventHandler
	public void handleIMCEventAE(AEStateEvent.ModuleIMCMessageEvent event){

	}

	@EventHandler
	public void handleIMCEventForge(IMCEvent event){

	}

	/*@ModuleEventHandler
	public void serverAboutToStartAE(FMLServerAboutToStartEvent event){

	}*/

	@EventHandler
	public void serverAboutToStartForge(FMLServerAboutToStartEvent event){

	}

	/*@ModuleEventHandler
	public void serverStartingAE(FMLServerStartingEvent event){

	}*/

	@EventHandler
	public void serverStartingForge(FMLServerStartingEvent event){

	}

	/*@ModuleEventHandler
	public void serverStoppingAE(FMLServerStoppingEvent event){

	}*/

	@EventHandler
	public void serverStoppingForge(FMLServerStoppingEvent event){

	}

	/*@ModuleEventHandler
	public void serverStoppedAE(FMLServerStoppedEvent event){

	}*/

	@EventHandler
	public void serverStoppedForge(FMLServerStoppedEvent event){

	}

}
