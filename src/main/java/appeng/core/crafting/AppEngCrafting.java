package appeng.core.crafting;

import appeng.api.definitions.IDefinition;
import appeng.api.definitions.IDefinitions;
import appeng.api.module.AEStateEvent;
import appeng.api.module.Module;
import appeng.api.module.Module.ModuleEventHandler;
import appeng.core.crafting.api.ICrafting;
import appeng.core.crafting.definitions.CraftingBlockDefinitions;
import appeng.core.crafting.definitions.CraftingItemDefinitions;
import appeng.core.crafting.definitions.CraftingTileDefinitions;
import appeng.core.crafting.proxy.CraftingProxy;
import appeng.core.lib.bootstrap_olde.FeatureFactory;
import appeng.core.me.AppEngME;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fml.common.SidedProxy;

@Module(value = ICrafting.NAME, dependencies = "after:module-" + AppEngME.NAME)
public class AppEngCrafting implements ICrafting {

	@Module.Instance(NAME)
	public static final AppEngCrafting INSTANCE = null;

	@SidedProxy(clientSide = "appeng.core.core.proxy.CraftingClientProxy", serverSide = "appeng.core.core.proxy.CraftingServerProxy")
	public static CraftingProxy proxy;

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
	public void preInit(AEStateEvent.AEPreInitializationEvent event){
		registry = new FeatureFactory();
		this.blockDefinitions = new CraftingBlockDefinitions(registry);
		this.itemDefinitions = new CraftingItemDefinitions(registry);
		this.tileDefinitions = new CraftingTileDefinitions(registry);
		registry.preInit(event);
		proxy.preInit(event);
	}

	@ModuleEventHandler
	public void init(AEStateEvent.AEInitializationEvent event){
		registry.init(event);
		proxy.init(event);
	}

	@ModuleEventHandler
	public void postInit(AEStateEvent.AEPostInitializationEvent event){
		registry.postInit(event);
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
