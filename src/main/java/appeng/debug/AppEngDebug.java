package appeng.debug;

import appeng.api.bootstrap.DefinitionFactory;
import appeng.api.bootstrap.InitializationComponentsHandler;
import appeng.api.config.ConfigurationLoader;
import appeng.api.definition.IDefinition;
import appeng.api.definitions.IDefinitions;
import appeng.api.module.AEStateEvent;
import appeng.api.module.Module;
import appeng.core.AppEng;
import appeng.core.lib.bootstrap.InitializationComponentsHandlerImpl;
import appeng.debug.config.DebugConfig;
import appeng.debug.definitions.DebugBlockDefinitions;
import appeng.debug.definitions.DebugItemDefinitions;
import appeng.debug.definitions.DebugTileDefinitions;
import appeng.debug.proxy.DebugProxy;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fml.common.SidedProxy;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;

/*
 * The only module not built with gradle.
 */
@Module(AppEngDebug.NAME)
public class AppEngDebug {

	public static final String NAME = "debug";

	public static final Logger logger = LogManager.getLogger(AppEng.NAME + "|"+ NAME);

	@Module.Instance
	public static final AppEngDebug INSTANCE = null;

	@SidedProxy(modId = AppEng.MODID, clientSide = "appeng.debug.proxy.DebugClientProxy", serverSide = "appeng.debug.proxy.DebugServerProxy")
	public static DebugProxy proxy;

	public DebugConfig config;

	private InitializationComponentsHandler initHandler = new InitializationComponentsHandlerImpl();

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

	@Module.ModuleEventHandler
	public void preInitAE(AEStateEvent.AEPreInitializationEvent event){
		ConfigurationLoader<DebugConfig> configLoader = event.configurationLoader();
		try{
			configLoader.load(DebugConfig.class);
		} catch(IOException e){
			logger.error("Caught exception loading configuration", e);
		}
		config = configLoader.configuration();

		DefinitionFactory registry = event.factory(initHandler, proxy);
		this.itemDefinitions = new DebugItemDefinitions(registry);
		this.blockDefinitions = new DebugBlockDefinitions(registry);
		this.tileDefinitions = new DebugTileDefinitions(registry);

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

	@Module.ModuleEventHandler
	public void initAE(final AEStateEvent.AEInitializationEvent event){
		initHandler.init();
		proxy.init(event);
	}

	@Module.ModuleEventHandler
	public void postInitAE(final AEStateEvent.AEPostInitializationEvent event){
		initHandler.postInit();
		proxy.postInit(event);
	}

}
