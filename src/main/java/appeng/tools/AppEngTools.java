package appeng.tools;

import appeng.api.bootstrap.DefinitionFactory;
import appeng.api.bootstrap.InitializationComponentsHandler;
import appeng.api.config.ConfigurationLoader;
import appeng.api.definition.IDefinition;
import appeng.api.definitions.IDefinitions;
import appeng.api.module.AEStateEvent;
import appeng.api.module.Module;
import appeng.api.module.Module.ModuleEventHandler;
import appeng.core.AppEng;
import appeng.core.core.api.material.Material;
import appeng.core.lib.bootstrap.InitializationComponentsHandlerImpl;
import appeng.tools.api.ITools;
import appeng.tools.config.ToolsConfig;
import appeng.tools.definitions.ToolsItemDefinitions;
import appeng.tools.definitions.ToolsMaterialDefinitions;
import appeng.tools.proxy.ToolsProxy;
import net.minecraft.item.Item;
import net.minecraftforge.fml.common.SidedProxy;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;

@Module(ITools.NAME)
public class AppEngTools implements ITools {

	public static final Logger logger = LogManager.getLogger(AppEng.NAME + "|"+ NAME);

	@Module.Instance
	public static final AppEngTools INSTANCE = null;

	@SidedProxy(modId = AppEng.MODID, clientSide = "appeng.tools.proxy.ToolsClientProxy", serverSide = "appeng.tools.proxy.ToolsServerProxy")
	public static ToolsProxy proxy;

	public ToolsConfig config;

	private InitializationComponentsHandler initHandler = new InitializationComponentsHandlerImpl();

	private DefinitionFactory registry;

	private ToolsItemDefinitions itemDefinitions;
	private ToolsMaterialDefinitions materialDefinitions;

	@Override
	public <T, D extends IDefinitions<T, ? extends IDefinition<T>>> D definitions(Class<T> clas){
		if(clas == Item.class){
			return (D) itemDefinitions;
		}
		if(clas == Material.class){
			return (D) materialDefinitions;
		}
		return null;
	}

	@ModuleEventHandler
	public void preInitAE(AEStateEvent.AEPreInitializationEvent event){
		ConfigurationLoader<ToolsConfig> configLoader = event.configurationLoader();
		try{
			configLoader.load(ToolsConfig.class);
		} catch(IOException e){
			logger.error("Caught exception loading configuration", e);
		}
		config = configLoader.configuration();

		registry = event.factory(initHandler, proxy);
		this.itemDefinitions = new ToolsItemDefinitions(registry);
		this.materialDefinitions = new ToolsMaterialDefinitions(registry);

		this.itemDefinitions.init(registry);
		this.materialDefinitions.init(registry);

		initHandler.preInit();
		proxy.preInit(event);

		try{
			configLoader.save();
		} catch(IOException e){
			logger.error("Caught exception saving configuration", e);
		}
	}

	@ModuleEventHandler
	public void initAE(final AEStateEvent.AEInitializationEvent event){
		initHandler.init();
		proxy.init(event);
	}

	@ModuleEventHandler
	public void postInitAE(final AEStateEvent.AEPostInitializationEvent event){
		initHandler.postInit();
		proxy.postInit(event);
	}

	@ModuleEventHandler
	public void handleIMCEventAE(AEStateEvent.ModuleIMCMessageEvent event){

	}

}
