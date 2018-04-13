package appeng.core.core;

import appeng.api.bootstrap.DefinitionFactory;
import appeng.api.bootstrap.InitializationComponentsHandler;
import appeng.api.config.ConfigurationLoader;
import appeng.api.definition.IDefinition;
import appeng.api.definitions.IDefinitions;
import appeng.api.entry.TileRegistryEntry;
import appeng.api.module.AEStateEvent;
import appeng.api.module.Module;
import appeng.api.module.Module.ModuleEventHandler;
import appeng.api.recipe.IGRecipeRegistry;
import appeng.core.AppEng;
import appeng.core.core.api.ICore;
import appeng.core.core.api.know.EternalWiki;
import appeng.core.core.api.know.IKnow;
import appeng.core.core.api.tick.Tickables;
import appeng.core.core.bootstrap.*;
import appeng.core.core.config.JSONConfigLoader;
import appeng.core.core.config.YAMLConfigLoader;
import appeng.core.core.definitions.*;
import appeng.core.core.know.EternalWikiImpl;
import appeng.core.core.know.KnowImpl;
import appeng.core.core.net.gui.CoreGuiHandler;
import appeng.core.core.proxy.CoreProxy;
import appeng.core.core.tick.TickablesImpl;
import appeng.core.lib.bootstrap.InitializationComponentsHandlerImpl;
import appeng.core.lib.capability.DelegateCapabilityStorage;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.nbt.NBTBase;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.DimensionType;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nullable;
import java.io.IOException;

@Module(value = ICore.NAME, dependencies = "hard-before:module-*")
public class AppEngCore implements ICore {

	public static final Logger logger = LogManager.getLogger(AppEng.NAME + "|"+ NAME);

	@Module.Instance
	public static final AppEngCore INSTANCE = null;

	@SidedProxy(modId = AppEng.MODID, clientSide = "appeng.core.core.proxy.CoreClientProxy", serverSide = "appeng.core.core.proxy.CoreServerProxy")
	public static CoreProxy proxy;

	@CapabilityInject(Tickables.class)
	public static Capability<Tickables> tickablesCapability;

	@CapabilityInject(IKnow.class)
	public static Capability<IKnow> knowCapability;

	private ConfigurationLoader<CoreConfig> configLoader;
	public CoreConfig config;

	private InitializationComponentsHandler initHandler = new InitializationComponentsHandlerImpl();

	private IForgeRegistry recipeRegistryRegistry;

	private DefinitionFactory registry;

	private CoreItemDefinitions itemDefinitions;
	private CoreBlockDefinitions blockDefinitions;
	private CoreTileDefinitions tileDefinitions;
	private CoreFluidDefinitions fluidDefinitions;
	private CoreEntityDefinitions entityDefinitions;

	private CoreGuiHandler guiHandler;

	public AppEngCore(){

	}

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
		if(clas == Fluid.class){
			return (D) fluidDefinitions;
		}
		if(clas == EntityEntry.class){
			return (D) entityDefinitions;
		}
		return null;
	}

	@Override
	public CoreGuiHandler guiHandler(){
		return guiHandler;
	}

	@Override
	public EternalWiki getEternalWiki(){
		return EternalWikiImpl.ETERNALWIKI;
	}

	@ModuleEventHandler
	public void bootstrap(AEStateEvent.AEBootstrapEvent event){
		event.registerConfigurationLoaderProvider("JSON", JSONConfigLoader::new);
		event.registerConfigurationLoaderProvider("YAML", YAMLConfigLoader::new);

		event.registerDefinitionBuilderSupplier(Item.class, Item.class, ItemDefinitionBuilder::new);
		event.registerDefinitionBuilderSupplier(Block.class, Block.class, BlockDefinitionBuilder::new);
		//TODO 1.11.2-ReOver - Fix NPE
		event.registerDefinitionBuilderSupplier(TileRegistryEntry.class, Class.class, TileDefinitionBuilder::new);
		event.registerDefinitionBuilderSupplier(Fluid.class, Fluid.class, FluidDefinitionBuilder::new);
		event.registerDefinitionBuilderSupplier(Biome.class, Biome.class, BiomeDefinitionBuilder::new);
		event.registerDefinitionBuilderSupplier(DimensionType.class, Integer.class, DimensionTypeDefinitionBuilder::new);
	}

	@ModuleEventHandler
	public void preInit(AEStateEvent.AEPreInitializationEvent event){
		recipeRegistryRegistry = new RegistryBuilder().setName(new ResourceLocation(AppEng.MODID, "recipe_registry")).setType(IGRecipeRegistry.class).disableSaving().setMaxID(Integer.MAX_VALUE - 1).create();

		configLoader = event.configurationLoader();
		try{
			configLoader.load(CoreConfig.class);
		} catch(IOException e){
			logger.error("Caught exception loading configuration", e);
		}
		initHandler.accept(config = configLoader.configuration());

		registry = event.factory(initHandler, proxy);
		this.itemDefinitions = new CoreItemDefinitions(registry);
		this.blockDefinitions = new CoreBlockDefinitions(registry);
		this.tileDefinitions = new CoreTileDefinitions(registry);
		this.fluidDefinitions = new CoreFluidDefinitions(registry);
		this.entityDefinitions = new CoreEntityDefinitions(registry);

		this.itemDefinitions.init(registry);
		this.blockDefinitions.init(registry);
		this.fluidDefinitions.init(registry);
		this.tileDefinitions.init(registry);
		this.entityDefinitions.init(registry);

		CapabilityManager.INSTANCE.register(Tickables.class, new Capability.IStorage<Tickables>() {

			@Nullable
			@Override
			public NBTBase writeNBT(Capability<Tickables> capability, Tickables instance, EnumFacing side){
				return null;
			}

			@Override
			public void readNBT(Capability<Tickables> capability, Tickables instance, EnumFacing side, NBTBase nbt){

			}

		}, TickablesImpl::new);
		CapabilityManager.INSTANCE.register(IKnow.class, new DelegateCapabilityStorage<>(), KnowImpl::new);


		guiHandler = new CoreGuiHandler();
		initHandler.acceptPostInit(EternalWikiImpl::freeze);

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

		try{
			configLoader.save();
		} catch(IOException e){
			logger.error("Caught exception saving configuration", e);
		}
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
