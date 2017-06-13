package appeng.core.me;

import appeng.api.bootstrap.DefinitionFactory;
import appeng.api.bootstrap.InitializationComponentsHandler;
import appeng.api.config.ConfigurationLoader;
import appeng.api.definitions.IDefinition;
import appeng.api.definitions.IDefinitions;
import appeng.api.module.AEStateEvent;
import appeng.api.module.Module;
import appeng.api.module.Module.ModuleEventHandler;
import appeng.core.AppEng;
import appeng.core.api.material.Material;
import appeng.core.core.CoreConfig;
import appeng.core.lib.bootstrap.InitializationComponentsHandlerImpl;
import appeng.core.me.api.IME;
import appeng.core.me.config.MEConfig;
import appeng.core.me.definitions.*;
import appeng.core.me.proxy.MEProxy;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.registry.EntityEntry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;

@Module(IME.NAME)
public class AppEngME implements IME {

	public static final Logger logger = LogManager.getLogger(AppEng.NAME + "|"+ NAME);

	@Module.Instance(NAME)
	public static final AppEngME INSTANCE = null;

	@SidedProxy(modId = AppEng.MODID, clientSide = "appeng.core.me.proxy.MEClientProxy", serverSide = "appeng.core.me.proxy.MEServerProxy")
	public static MEProxy proxy;

	public MEConfig config;

	private InitializationComponentsHandler initHandler = new InitializationComponentsHandlerImpl();

	private DefinitionFactory registry;

	private MEItemDefinitions itemDefinitions;
	private MEBlockDefinitions blockDefinitions;
	private METileDefinitions tileDefinitions;
	private MEMaterialDefinitions materialDefinitions;
	private MEEntityDefinitions entityDefinitions;

	@Override
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
		if(clas == Material.class){
			return (D) materialDefinitions;
		}
		if(clas == EntityEntry.class){
			return (D) entityDefinitions;
		}
		return null;
	}

	@ModuleEventHandler
	public void preInit(AEStateEvent.AEPreInitializationEvent event){
		ConfigurationLoader<MEConfig> configLoader = event.configurationLoader();
		try{
			configLoader.load(MEConfig.class);
		} catch(IOException e){
			logger.error("Caught exception loading configuration", e);
		}
		config = configLoader.configuration();

		registry = event.factory(initHandler, proxy);
		this.itemDefinitions = new MEItemDefinitions(registry);
		this.blockDefinitions = new MEBlockDefinitions(registry);
		this.tileDefinitions = new METileDefinitions(registry);
		this.entityDefinitions = new MEEntityDefinitions(registry);
		this.materialDefinitions = new MEMaterialDefinitions(registry);

		this.itemDefinitions.init(registry);
		this.blockDefinitions.init(registry);
		this.tileDefinitions.init(registry);
		this.entityDefinitions.init(registry);
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
	public void init(AEStateEvent.AEInitializationEvent event){
		initHandler.init();
		proxy.init(event);
	}

	@ModuleEventHandler
	public void postInit(AEStateEvent.AEPostInitializationEvent event){
		initHandler.postInit();
		proxy.postInit(event);
	}

	@ModuleEventHandler
	public void handleIMCEvent(AEStateEvent.ModuleIMCMessageEvent event){

	}

	/*@ModuleEventHandler
	public void serverAboutToStart(FMLServerAboutToStartEvent event){

	}

	@ModuleEventHandler
	public void serverStarting(FMLServerStartingEvent event){

	}

	@ModuleEventHandler
	public void serverStopping(FMLServerStoppingEvent event){

	}

	@ModuleEventHandler
	public void serverStopped(FMLServerStoppedEvent event){

	}*/

}
