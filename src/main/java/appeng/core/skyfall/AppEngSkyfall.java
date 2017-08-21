package appeng.core.skyfall;

import appeng.api.bootstrap.DefinitionFactory;
import appeng.api.bootstrap.InitializationComponentsHandler;
import appeng.api.config.ConfigurationLoader;
import appeng.api.definition.IDefinition;
import appeng.api.definitions.IDefinitions;
import appeng.api.module.AEStateEvent;
import appeng.api.module.Module;
import appeng.api.module.Module.ModuleEventHandler;
import appeng.core.AppEng;
import appeng.core.lib.bootstrap.InitializationComponentsHandlerImpl;
import appeng.core.lib.capability.DelegateCapabilityStorage;
import appeng.core.skyfall.api.ISkyfall;
import appeng.core.skyfall.api.skyobject.Skyobject;
import appeng.core.skyfall.api.skyobject.SkyobjectProvider;
import appeng.core.skyfall.api.skyobject.SkyobjectsManager;
import appeng.core.skyfall.bootstrap.SkyobjectGeneratorDefinitionBuilder;
import appeng.core.skyfall.command.SkyobjectsCommand;
import appeng.core.skyfall.config.SkyfallConfig;
import appeng.core.skyfall.definitions.SkyfallBlockDefinitions;
import appeng.core.skyfall.definitions.SkyfallItemDefinitions;
import appeng.core.skyfall.definitions.SkyfallSkyobjectProviderDefinitions;
import appeng.core.skyfall.net.SkyobjectSpawnMessage;
import appeng.core.skyfall.net.SkyobjectsSyncMessage;
import appeng.core.skyfall.proxy.SkyfallProxy;
import appeng.core.skyfall.skyobject.SkyobjectsManagerImpl;
import code.elix_x.excore.utils.net.packets.SmartNetworkWrapper;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;

@Module(ISkyfall.NAME)
public class AppEngSkyfall implements ISkyfall {

	public static final Logger logger = LogManager.getLogger(AppEng.NAME + "|"+ NAME);

	@Module.Instance
	public static final AppEngSkyfall INSTANCE = null;

	@SidedProxy(modId = AppEng.MODID, clientSide = "appeng.core.skyfall.proxy.SkyfallClientProxy", serverSide = "appeng.core.skyfall.proxy.SkyfallServerProxy")
	public static SkyfallProxy proxy;

	@CapabilityInject(SkyobjectsManager.class)
	public static Capability<SkyobjectsManager> skyobjectsManagerCapability;

	public SkyfallConfig config;

	public SmartNetworkWrapper net;

	private InitializationComponentsHandler initHandler = new InitializationComponentsHandlerImpl();

	private IForgeRegistry skyobjectProvidersRegistry;

	private DefinitionFactory registry;

	private SkyfallBlockDefinitions blockDefinitions;
	private SkyfallItemDefinitions itemDefinitions;
	private SkyfallSkyobjectProviderDefinitions skyobjectProviderDefinitions;

	private ConfigurationLoader<SkyfallConfig> configLoader;

	@Override
	public <T, D extends IDefinitions<T, ? extends IDefinition<T>>> D definitions(Class<T> clas){
		if(clas == Item.class) return (D) itemDefinitions;
		if(clas == Block.class) return (D) blockDefinitions;
		if(clas == SkyobjectProvider.class) return (D) skyobjectProviderDefinitions;
		return null;
	}

	public <S extends Skyobject<S, P>, P extends SkyobjectProvider<S, P>> IForgeRegistry<P> getSkyobjectProvidersRegistry(){
		return skyobjectProvidersRegistry;
	}

	@ModuleEventHandler
	public void bootstrap(AEStateEvent.AEBootstrapEvent event){
		event.registerDefinitionBuilderSupplier(SkyobjectProvider.class, SkyobjectProvider.class, (factory, registryName, skyobjectGenerator) -> new SkyobjectGeneratorDefinitionBuilder<>(factory,registryName, skyobjectGenerator));
	}

	@ModuleEventHandler
	public void preInit(AEStateEvent.AEPreInitializationEvent event){
		skyobjectProvidersRegistry = new RegistryBuilder<>().setName(new ResourceLocation(AppEng.MODID, "skyobject_generator")).setType((Class) SkyobjectProvider.class).disableSaving().setMaxID(Integer.MAX_VALUE - 1).create();

		configLoader = event.configurationLoader();
		try{
			configLoader.load(SkyfallConfig.class);
		} catch(IOException e){
			logger.error("Caught exception loading configuration", e);
		}
		config = configLoader.configuration();

		registry = event.factory(initHandler, proxy);
		this.blockDefinitions = new SkyfallBlockDefinitions(registry);
		this.itemDefinitions = new SkyfallItemDefinitions(registry);
		this.skyobjectProviderDefinitions = new SkyfallSkyobjectProviderDefinitions(registry);

		this.itemDefinitions.init(registry);
		this.blockDefinitions.init(registry);
		this.skyobjectProviderDefinitions.init(registry);

		CapabilityManager.INSTANCE.register(SkyobjectsManager.class, new DelegateCapabilityStorage<>(), SkyobjectsManagerImpl::new);

		net = new SmartNetworkWrapper(AppEng.NAME + "|"+ NAME);

		net.registerMessage3(message -> () -> Minecraft.getMinecraft().world.getCapability(skyobjectsManagerCapability, null).deserializeNBT(message.nbt), SkyobjectsSyncMessage.class, Side.CLIENT);
		net.registerMessage3(message -> () -> ((SkyobjectsManagerImpl) Minecraft.getMinecraft().world.getCapability(skyobjectsManagerCapability, null)).receiveClientSkyobject(message.uuid, message.skyobject), SkyobjectSpawnMessage.class, Side.CLIENT);

		initHandler.preInit();
		proxy.preInit(event);
	}

	@ModuleEventHandler
	public void init(AEStateEvent.AEInitializationEvent event){
		config.init();

		initHandler.init();
		proxy.init(event);
	}

	@ModuleEventHandler
	public void postInit(AEStateEvent.AEPostInitializationEvent event){
		initHandler.postInit();
		proxy.postInit(event);

		try{
			configLoader.save();
		} catch(IOException e){
			logger.error("Caught exception saving configuration", e);
		}
	}

	@ModuleEventHandler
	public void handleIMCEvent(AEStateEvent.ModuleIMCMessageEvent event){

	}

	@ModuleEventHandler
	public void serverStarting(AEStateEvent.AEServerStartingEvent event){
		event.registerModuleSubcommand(new SkyobjectsCommand());
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
