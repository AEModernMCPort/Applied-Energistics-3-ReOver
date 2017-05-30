package appeng.core.worldgen;

import appeng.api.definitions.IDefinition;
import appeng.api.definitions.IDefinitions;
import appeng.api.module.AEStateEvent;
import appeng.api.module.Module;
import appeng.api.module.Module.ModuleEventHandler;
import appeng.core.AppEng;
import appeng.core.lib.bootstrap_olde.FeatureFactory;
import appeng.core.worldgen.api.IWorldGen;
import appeng.core.worldgen.definitions.WorldGenBlockDefinitions;
import appeng.core.worldgen.definitions.WorldGenItemDefinitions;
import appeng.core.worldgen.definitions.WorldGenTileDefinitions;
import appeng.core.worldgen.proxy.WorldGenProxy;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fml.common.SidedProxy;

@Module(IWorldGen.NAME)
public class AppEngWorldGen implements IWorldGen {

	@Module.Instance(NAME)
	public static final AppEngWorldGen INSTANCE = null;

	@SidedProxy(modId = AppEng.MODID, clientSide = "appeng.core.worldgen.proxy.WorldGenClientProxy", serverSide = "appeng.core.worldgen.proxy.WorldGenServerProxy")
	public static WorldGenProxy proxy;

	private FeatureFactory registry;

	private WorldGenItemDefinitions itemDefinitions;
	private WorldGenBlockDefinitions blockDefinitions;
	private WorldGenTileDefinitions tileDefinitions;

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
		this.blockDefinitions = new WorldGenBlockDefinitions(registry);
		this.itemDefinitions = new WorldGenItemDefinitions(registry);
		this.tileDefinitions = new WorldGenTileDefinitions(registry);
		registry.preInit(event);
	}

	@ModuleEventHandler
	public void init(AEStateEvent.AEInitializationEvent event){
		registry.init(event);
	}

	@ModuleEventHandler
	public void postInit(AEStateEvent.AEPostInitializationEvent event){
		registry.postInit(event);
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
