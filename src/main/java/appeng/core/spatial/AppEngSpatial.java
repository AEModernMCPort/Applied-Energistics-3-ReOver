package appeng.core.spatial;

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
import appeng.core.spatial.api.ISpatial;
import appeng.core.spatial.definitions.*;
import appeng.core.spatial.proxy.SpatialProxy;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.DimensionType;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.fml.common.SidedProxy;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Module(ISpatial.NAME)
public class AppEngSpatial implements ISpatial {

	public static final Logger logger = LogManager.getLogger(AppEng.NAME + "|"+ NAME);

	@Module.Instance(NAME)
	public static final AppEngSpatial INSTANCE = null;

	@SidedProxy(modId = AppEng.MODID, clientSide = "appeng.core.spatial.proxy.SpatialClientProxy", serverSide = "appeng.core.spatial.proxy.SpatialServerProxy")
	public static SpatialProxy proxy;

	private InitializationComponentsHandler initHandler = new InitializationComponentsHandlerImpl();

	private DefinitionFactory registry;

	private SpatialItemDefinitions itemDefinitions;
	private SpatialBlockDefinitions blockDefinitions;
	private SpatialTileDefinitions tileDefinitions;
	private SpatialMaterialDefinitions materialDefinitions;
	private SpatialBiomeDefinitions biomeDefinitions;
	private SpatialDimensionTypeDefinitions dimensionTypeDefinitions;

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
		if(clas == Biome.class){
			return (D) biomeDefinitions;
		}
		if(clas == DimensionType.class){
			return (D) dimensionTypeDefinitions;
		}
		return null;
	}

	@ModuleEventHandler
	public void preInit(AEStateEvent.AEPreInitializationEvent event){
		registry = event.factory(initHandler, proxy);
		this.itemDefinitions = new SpatialItemDefinitions(registry);
		this.blockDefinitions = new SpatialBlockDefinitions(registry);
		this.tileDefinitions = new SpatialTileDefinitions(registry);
		this.biomeDefinitions = new SpatialBiomeDefinitions(registry);
		this.dimensionTypeDefinitions = new SpatialDimensionTypeDefinitions(registry);
		this.materialDefinitions = new SpatialMaterialDefinitions(registry);

		this.itemDefinitions.init(registry);
		this.blockDefinitions.init(registry);
		this.tileDefinitions.init(registry);
		this.biomeDefinitions.init(registry);
		this.dimensionTypeDefinitions.init(registry);
		this.materialDefinitions.init(registry);

		initHandler.preInit();
		proxy.preInit(event);
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
