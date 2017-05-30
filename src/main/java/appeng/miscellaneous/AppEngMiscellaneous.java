package appeng.miscellaneous;

import appeng.api.bootstrap.InitializationComponentsHandler;
import appeng.api.definitions.IDefinition;
import appeng.api.definitions.IDefinitions;
import appeng.api.module.AEStateEvent;
import appeng.api.module.Module;
import appeng.api.module.Module.ModuleEventHandler;
import appeng.core.AppEng;
import appeng.core.crafting.definitions.CraftingBlockDefinitions;
import appeng.core.crafting.definitions.CraftingItemDefinitions;
import appeng.core.crafting.definitions.CraftingTileDefinitions;
import appeng.core.lib.bootstrap.InitializationComponentsHandlerImpl;
import appeng.miscellaneous.api.IMiscellaneous;
import appeng.miscellaneous.proxy.MiscProxy;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.ForgeVersion;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.*;
import net.minecraftforge.fml.common.event.FMLInterModComms.IMCEvent;

@Module(IMiscellaneous.NAME)
@Mod(modid = AppEngMiscellaneous.MODID, name = AppEngMiscellaneous.MODNAME, version = AppEng.VERSION, dependencies = "required-after:" + AppEng.MODID, acceptedMinecraftVersions = ForgeVersion.mcVersion)
public class AppEngMiscellaneous implements IMiscellaneous {

	public static final String MODID = AppEng.MODID + "|" + IMiscellaneous.NAME;

	public static final String MODNAME = AppEng.NAME + " | " + IMiscellaneous.NAME;

	@Module.Instance(NAME)
	public static final AppEngMiscellaneous INSTANCE = null;

	@SidedProxy(modId = MODID, clientSide = "appeng.miscellaneous.proxy.MiscClientProxy", serverSide = "appeng.miscellaneous.proxy.MiscServerProxy")
	public static MiscProxy proxy;

	private InitializationComponentsHandler initHandler = new InitializationComponentsHandlerImpl();

	private FeatureFactory registry;

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
		if(clas == TileEntity.class){
			return (D) tileDefinitions;
		}
		return null;
	}

	@ModuleEventHandler
	public void preInitAE(AEStateEvent.AEPreInitializationEvent event){
		registry = new FeatureFactory();
		this.blockDefinitions = new CraftingBlockDefinitions(registry);
		this.itemDefinitions = new CraftingItemDefinitions(registry);
		this.tileDefinitions = new CraftingTileDefinitions(registry);
		registry.preInit(event);

		initHandler.preInit();
		proxy.preInit(event);
	}

	@EventHandler
	public void preInitForge(FMLPreInitializationEvent event){

	}

	@ModuleEventHandler
	public void initAE(final AEStateEvent.AEInitializationEvent event){
		registry.init(event);

		initHandler.init();
		proxy.init(event);
	}

	@EventHandler
	public void initForge(final FMLInitializationEvent event){

	}

	@ModuleEventHandler
	public void postInitAE(final AEStateEvent.AEPostInitializationEvent event){
		registry.postInit(event);

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
