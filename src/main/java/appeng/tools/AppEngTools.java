package appeng.tools;

import appeng.api.bootstrap.DefinitionFactory;
import appeng.api.bootstrap.InitializationComponentsHandler;
import appeng.api.definitions.IDefinition;
import appeng.api.definitions.IDefinitions;
import appeng.api.module.AEStateEvent;
import appeng.api.module.Module;
import appeng.api.module.Module.ModuleEventHandler;
import appeng.core.AppEng;
import appeng.core.api.material.Material;
import appeng.core.lib.bootstrap.InitializationComponentsHandlerImpl;
import appeng.tools.api.ITools;
import appeng.tools.definitions.ToolsItemDefinitions;
import appeng.tools.definitions.ToolsMaterialDefinitions;
import appeng.tools.proxy.ToolsProxy;
import net.minecraft.item.Item;
import net.minecraftforge.common.ForgeVersion;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.*;
import net.minecraftforge.fml.common.event.FMLInterModComms.IMCEvent;

@Module(ITools.NAME)
@Mod(modid = AppEngTools.MODID, name = AppEngTools.MODNAME, version = AppEng.VERSION, dependencies = "required-after:" + AppEng.MODID, acceptedMinecraftVersions = ForgeVersion.mcVersion)
public class AppEngTools implements ITools {

	public static final String MODID = AppEng.MODID + "|" + ITools.NAME;

	public static final String MODNAME = AppEng.NAME + " | " + ITools.NAME;

	@Module.Instance(NAME)
	public static final AppEngTools INSTANCE = null;

	@SidedProxy(modId = MODID, clientSide = "appeng.tools.proxy.ToolsClientProxy", serverSide = "appeng.tools.proxy.ToolsServerProxy")
	public static ToolsProxy proxy;

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
		registry = event.factory(initHandler, proxy);
		this.materialDefinitions = new ToolsMaterialDefinitions(registry);
		this.itemDefinitions = new ToolsItemDefinitions(registry);

		initHandler.preInit();
		proxy.preInit(event);
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
