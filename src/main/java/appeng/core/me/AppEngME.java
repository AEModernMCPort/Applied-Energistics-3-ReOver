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
import appeng.core.lib.capability.DelegateCapabilityStorage;
import appeng.core.me.api.IME;
import appeng.core.me.api.network.GlobalNBDManager;
import appeng.core.me.api.network.NBDIO;
import appeng.core.me.api.network.NetDevice;
import appeng.core.me.api.network.PhysicalDevice;
import appeng.core.me.api.network.block.Connection;
import appeng.core.me.api.network.device.DeviceRegistryEntry;
import appeng.core.me.api.network.storage.caps.EntityNetworkStorage;
import appeng.core.me.api.network.storage.caps.FluidNetworkStorage;
import appeng.core.me.api.network.storage.caps.ItemNetworkStorage;
import appeng.core.me.api.parts.container.IPartsContainer;
import appeng.core.me.api.parts.container.PartsAccess;
import appeng.core.me.api.parts.part.Part;
import appeng.core.me.bootstrap.DeviceDefinitionBuilder;
import appeng.core.me.bootstrap.PartDefinitionBuilder;
import appeng.core.me.config.MEConfig;
import appeng.core.me.definitions.*;
import appeng.core.me.netio.PartMessage;
import appeng.core.me.network.DevicesHelper;
import appeng.core.me.network.GlobalNBDManagerImpl;
import appeng.core.me.network.NBDIOImpl;
import appeng.core.me.network.storage.caps.EntityNetworkStorageImpl;
import appeng.core.me.network.storage.caps.FluidNetworkStorageImpl;
import appeng.core.me.network.storage.caps.ItemNetworkStorageImpl;
import appeng.core.me.parts.container.PartsContainer;
import appeng.core.me.parts.container.WorldPartsAccess;
import appeng.core.me.parts.part.PartsHelperImpl;
import appeng.core.me.proxy.MEProxy;
import code.elix_x.excore.utils.net.packets.SmartNetworkWrapper;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.Optional;

@Module(IME.NAME)
public class AppEngME implements IME {

	public static final Logger logger = LogManager.getLogger(AppEng.NAME + "|"+ NAME);

	@Module.Instance
	public static final AppEngME INSTANCE = null;

	@SidedProxy(modId = AppEng.MODID, clientSide = "appeng.core.me.proxy.MEClientProxy", serverSide = "appeng.core.me.proxy.MEServerProxy")
	public static MEProxy proxy;

	public MEConfig config;

	public SmartNetworkWrapper net;

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

	private PartsHelperImpl partsHelper;
	private DevicesHelper devicesHelper;
	private NBDIOImpl nbdio;

	@Override
	public <T, D extends IDefinitions<T, ? extends IDefinition<T>>> D definitions(Class<? super T> clas){
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

	public PartsHelperImpl getPartsHelper(){
		return partsHelper;
	}

	public DevicesHelper getDevicesHelper(){
		return devicesHelper;
	}

	@Override
	public void registerConnection(Connection connection){
		devicesHelper.registerConnection(connection);
	}

	@Override
	public NBDIO getNBDIO(){
		return nbdio;
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

		partsHelper = new PartsHelperImpl();
		devicesHelper = new DevicesHelper();

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

		initHandler.accept(partsHelper);
		initHandler.accept(devicesHelper);
		initHandler.accept(nbdio = new NBDIOImpl());
		CapabilityManager.INSTANCE.register(IPartsContainer.class, PartsContainer.Storage.INSTANCE, PartsContainer::new);
		CapabilityManager.INSTANCE.register(PartsAccess.Mutable.class, WorldPartsAccess.Storage.INSTANCE, WorldPartsAccess::new);

		CapabilityManager.INSTANCE.register(ItemNetworkStorage.class, new DelegateCapabilityStorage<>(), ItemNetworkStorageImpl::new);
//		CapabilityManager.INSTANCE.register(BlockNetworkStorage.class, new DelegateCapabilityStorage<>(), BlockNetworkStorageImpl::new); TODO 1.13 Implement
		CapabilityManager.INSTANCE.register(FluidNetworkStorage.class, new DelegateCapabilityStorage<>(), FluidNetworkStorageImpl::new);
		CapabilityManager.INSTANCE.register(EntityNetworkStorage.class, new DelegateCapabilityStorage<>(), EntityNetworkStorageImpl::new);

		net = new SmartNetworkWrapper("AE3" + "|"+ NAME);
		net.registerMessage3(m -> () -> Optional.ofNullable(Minecraft.getMinecraft().world.getCapability(PartsHelperImpl.worldPartsAccessCapability, null)).ifPresent(access -> access.receiveUpdate(m.posRot, m.id, m.data)), PartMessage.class, Side.CLIENT);

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
	public void serverStarting(AEStateEvent.AEServerAboutToStartEvent event){
		GlobalNBDManagerImpl.serverStarting(FMLCommonHandler.instance().getMinecraftServerInstance());
	}

	@ModuleEventHandler
	public void serverStopping(AEStateEvent.AEServerStoppingEvent event){
		GlobalNBDManagerImpl.serverStopping(FMLCommonHandler.instance().getMinecraftServerInstance());
	}

}
