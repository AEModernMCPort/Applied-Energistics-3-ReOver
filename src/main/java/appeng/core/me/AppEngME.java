package appeng.core.me;

import appeng.api.bootstrap.DefinitionFactory;
import appeng.api.bootstrap.InitializationComponentsHandler;
import appeng.api.config.ConfigurationLoader;
import appeng.api.definition.IDefinition;
import appeng.api.definitions.IDefinitions;
import appeng.api.entry.TileRegistryEntry;
import appeng.api.module.AEStateEvent;
import appeng.api.module.Module;
import appeng.api.module.Module.ModuleEventHandler;
import appeng.core.AppEng;
import appeng.core.lib.bootstrap.InitializationComponentsHandlerImpl;
import appeng.core.me.api.IME;
import appeng.core.me.api.network.*;
import appeng.core.me.api.network.device.DeviceRegistryEntry;
import appeng.core.me.api.parts.container.IPartsContainer;
import appeng.core.me.api.parts.container.PartsAccess;
import appeng.core.me.api.parts.part.Part;
import appeng.core.me.api.parts.placement.PartPlacementLogic;
import appeng.core.me.bootstrap.DeviceDefinitionBuilder;
import appeng.core.me.bootstrap.PartDefinitionBuilder;
import appeng.core.me.config.MEConfig;
import appeng.core.me.definitions.*;
import appeng.core.me.network.GlobalNBDManagerImpl;
import appeng.core.me.network.NetworkImpl;
import appeng.core.me.parts.container.PartsContainer;
import appeng.core.me.parts.container.WorldPartsAccess;
import appeng.core.me.parts.part.PartsHelper;
import appeng.core.me.parts.placement.DefaultPartPlacementLogic;
import appeng.core.me.proxy.MEProxy;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.function.BiFunction;

@Module(IME.NAME)
public class AppEngME implements IME {

	public static final Logger logger = LogManager.getLogger(AppEng.NAME + "|"+ NAME);

	@Module.Instance
	public static final AppEngME INSTANCE = null;

	@SidedProxy(modId = AppEng.MODID, clientSide = "appeng.core.me.proxy.MEClientProxy", serverSide = "appeng.core.me.proxy.MEServerProxy")
	public static MEProxy proxy;

	public MEConfig config;

	private InitializationComponentsHandler initHandler = new InitializationComponentsHandlerImpl();

	private IForgeRegistry partRegistry;
	private IForgeRegistry deviceRegistry;

	private DefinitionFactory registry;

	private MEItemDefinitions itemDefinitions;
	private MEBlockDefinitions blockDefinitions;
	private METileDefinitions tileDefinitions;
	private MEEntityDefinitions entityDefinitions;
	private MEPartDefinitions partDefinitions;
	private MEDeviceDefinitions deviceDefinitions;

	private PartsHelper partsHelper;

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
		if(clas == EntityEntry.class){
			return (D) entityDefinitions;
		}
		if(clas == Part.class){
			return (D) partDefinitions;
		}
		if(clas == DeviceRegistryEntry.class){
			return (D) deviceDefinitions;
		}
		return null;
	}

	public <P extends Part<P, S>, S extends Part.State<P, S>> IForgeRegistry<P> getPartRegistry(){
		return partRegistry;
	}

	public <N extends NetDevice<N, P>, P extends PhysicalDevice<N, P>> IForgeRegistry<DeviceRegistryEntry<N, P>> getDeviceRegistry(){
		return deviceRegistry;
	}

	public PartsHelper getPartsHelper(){
		return partsHelper;
	}

	@Override
	public PartPlacementLogic createDefaultPlacementLogic(Part part){
		return new DefaultPartPlacementLogic(part);
	}

	@Override
	public <N extends Network> void registerNetworkLoader(ResourceLocation id, BiFunction<NetworkUUID, NBTTagCompound, N> loader){
		GlobalNBDManagerImpl.registerNetworkLoader(id, loader);
	}

	@Override
	public GlobalNBDManager getGlobalNBDManager(){
		return GlobalNBDManagerImpl.getInstance();
	}

	@ModuleEventHandler
	public void bootstrap(AEStateEvent.AEBootstrapEvent event){
		event.registerDefinitionBuilderSupplier(Part.class, Part.class, (factory, registryName, input) -> new PartDefinitionBuilder(factory, registryName, input));
		event.registerDefinitionBuilderSupplier(DeviceRegistryEntry.class, Void.class, ((factory, registryName, input) -> new DeviceDefinitionBuilder(factory, registryName)));
	}

	@ModuleEventHandler
	public void preInit(AEStateEvent.AEPreInitializationEvent event){
		partRegistry = new RegistryBuilder().setName(new ResourceLocation(AppEng.MODID, "parts")).setType(Part.class).setMaxID(Integer.MAX_VALUE - 1).create();
		deviceRegistry = new RegistryBuilder().setName(new ResourceLocation(AppEng.MODID, "devices")).setType(DeviceRegistryEntry.class).setMaxID(Integer.MAX_VALUE - 1).create();

		ConfigurationLoader<MEConfig> configLoader = event.configurationLoader();
		try{
			configLoader.load(MEConfig.class);
		} catch(IOException e){
			logger.error("Caught exception loading configuration", e);
		}
		config = configLoader.configuration();
		config.registerColors();

		registry = event.factory(initHandler, proxy);
		this.itemDefinitions = new MEItemDefinitions(registry);
		this.blockDefinitions = new MEBlockDefinitions(registry);
		this.tileDefinitions = new METileDefinitions(registry);
		this.entityDefinitions = new MEEntityDefinitions(registry);
		this.partDefinitions = new MEPartDefinitions(registry);
		this.deviceDefinitions = new MEDeviceDefinitions(registry);

		this.itemDefinitions.init(registry);
		this.blockDefinitions.init(registry);
		this.tileDefinitions.init(registry);
		this.entityDefinitions.init(registry);
		this.partDefinitions.init(registry);
		this.deviceDefinitions.init(registry);

		registerNetworkLoader(GlobalNBDManagerImpl.DEFAULTLOADER, NetworkImpl::createFromNBT);
		initHandler.accept(partsHelper = new PartsHelper());
		CapabilityManager.INSTANCE.register(IPartsContainer.class, PartsContainer.Storage.INSTANCE, PartsContainer::new);
		CapabilityManager.INSTANCE.register(PartsAccess.Mutable.class, WorldPartsAccess.Storage.INSTANCE, WorldPartsAccess::new);

		MinecraftForge.EVENT_BUS.register(this);

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

	@ModuleEventHandler
	public void serverStarting(AEStateEvent.AEServerStartingEvent event){
		GlobalNBDManagerImpl.serverStarting(FMLCommonHandler.instance().getMinecraftServerInstance());
	}

	@ModuleEventHandler
	public void serverStopping(AEStateEvent.AEServerStoppingEvent event){
		GlobalNBDManagerImpl.serverStopping(FMLCommonHandler.instance().getMinecraftServerInstance());
	}

}
