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
import appeng.core.core.api.crafting.ion.Ion;
import appeng.core.core.api.crafting.ion.IonEnvironment;
import appeng.core.core.api.crafting.ion.IonProvider;
import appeng.core.core.api.material.Material;
import appeng.core.core.bootstrap.*;
import appeng.core.core.config.JSONConfigLoader;
import appeng.core.core.config.YAMLConfigLoader;
import appeng.core.core.crafting.ion.CraftingIonRegistry;
import appeng.core.core.crafting.ion.IonProviderImpl;
import appeng.core.core.definitions.*;
import appeng.core.core.net.gui.CoreGuiHandler;
import appeng.core.core.proxy.CoreProxy;
import appeng.core.lib.bootstrap.InitializationComponentsHandlerImpl;
import com.sun.xml.internal.ws.policy.privateutil.PolicyUtils;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.DimensionType;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.registries.ForgeRegistry;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nullable;
import java.io.IOException;
import java.util.Collections;
import java.util.Map;

@Module(value = ICore.NAME, dependencies = "hard-before:module-*")
public class AppEngCore implements ICore {

	public static final Logger logger = LogManager.getLogger(AppEng.NAME + "|"+ NAME);

	@Module.Instance
	public static final AppEngCore INSTANCE = null;

	@SidedProxy(modId = AppEng.MODID, clientSide = "appeng.core.core.proxy.CoreClientProxy", serverSide = "appeng.core.core.proxy.CoreServerProxy")
	public static CoreProxy proxy;

	@CapabilityInject(IonEnvironment.class)
	public static Capability<IonEnvironment> ionEnvironmentCapability;

	@CapabilityInject(IonProvider.class)
	public static Capability<IonProvider> ionProviderCapability;

	public CoreConfig config;

	private InitializationComponentsHandler initHandler = new InitializationComponentsHandlerImpl();

	private IForgeRegistry recipeRegistryRegistry;
	private ForgeRegistry<Material> materialRegistry;
	private IForgeRegistry<Ion> ionRegistry;

	private DefinitionFactory registry;

	private CoreItemDefinitions itemDefinitions;
	private CoreBlockDefinitions blockDefinitions;
	private CoreTileDefinitions tileDefinitions;
	private CoreFluidDefinitions fluidDefinitions;
	private CoreMaterialDefinitions materialDefinitions;
	private CoreIonDefinitions ionDefinitions;
	private CoreEntityDefinitions entityDefinitions;

	private CraftingIonRegistry craftingIonRegistry;

	private CoreGuiHandler guiHandler;

	public AppEngCore(){

	}

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
		if(clas == Fluid.class){
			return (D) fluidDefinitions;
		}
		if(clas == Material.class){
			return (D) materialDefinitions;
		}
		if(clas == Ion.class){
			return (D) ionDefinitions;
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

	public ForgeRegistry<Material> getMaterialRegistry(){
		return materialRegistry;
	}

	public IForgeRegistry<Ion> getIonRegistry(){
		return ionRegistry;
	}

	public CraftingIonRegistry getCraftingIonRegistry(){
		return craftingIonRegistry;
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

		event.registerDefinitionBuilderSupplier(Material.class, Material.class, MaterialDefinitionBuilder::new);
		event.registerDefinitionBuilderSupplier(Ion.class, Ion.class, IonDefinitionBuilder::new);
	}

	@ModuleEventHandler
	public void preInit(AEStateEvent.AEPreInitializationEvent event){
		recipeRegistryRegistry = new RegistryBuilder().setName(new ResourceLocation(AppEng.MODID, "recipe_registry")).setType(IGRecipeRegistry.class).disableSaving().setMaxID(Integer.MAX_VALUE - 1).create();
		materialRegistry = (ForgeRegistry<Material>) new RegistryBuilder<Material>().setName(new ResourceLocation(AppEng.MODID, "material")).setType(Material.class).setIDRange(0, Short.MAX_VALUE).create();
		ionRegistry = new RegistryBuilder<Ion>().setName(new ResourceLocation(AppEng.MODID, "ion")).setType(Ion.class).disableSaving().setMaxID(Integer.MAX_VALUE - 1).create();

		ConfigurationLoader<CoreConfig> configLoader = event.configurationLoader();
		try{
			configLoader.load(CoreConfig.class);
		} catch(IOException e){
			logger.error("Caught exception loading configuration", e);
		}
		config = configLoader.configuration();

		registry = event.factory(initHandler, proxy);
		this.itemDefinitions = new CoreItemDefinitions(registry);
		this.blockDefinitions = new CoreBlockDefinitions(registry);
		this.tileDefinitions = new CoreTileDefinitions(registry);
		this.fluidDefinitions = new CoreFluidDefinitions(registry);
		this.entityDefinitions = new CoreEntityDefinitions(registry);
		this.materialDefinitions = new CoreMaterialDefinitions(registry);
		this.ionDefinitions = new CoreIonDefinitions(registry);

		this.itemDefinitions.init(registry);
		this.blockDefinitions.init(registry);
		this.fluidDefinitions.init(registry);
		this.tileDefinitions.init(registry);
		this.entityDefinitions.init(registry);
		this.materialDefinitions.init(registry);
		this.ionDefinitions.init(registry);

		CapabilityManager.INSTANCE.register(IonEnvironment.class, new Capability.IStorage<IonEnvironment>() {

			@Nullable
			@Override
			public NBTBase writeNBT(Capability<IonEnvironment> capability, IonEnvironment instance, EnumFacing side){
				return instance.serializeNBT();
			}

			@Override
			public void readNBT(Capability<IonEnvironment> capability, IonEnvironment instance, EnumFacing side, NBTBase nbt){
				instance.deserializeNBT((NBTTagCompound) nbt);
			}

		}, appeng.core.core.crafting.ion.IonEnvironment::new);

		CapabilityManager.INSTANCE.register(IonProvider.class, new Capability.IStorage<IonProvider>() {

			@Nullable
			@Override
			public NBTBase writeNBT(Capability<IonProvider> capability, IonProvider instance, EnumFacing side){
				return null;
			}

			@Override
			public void readNBT(Capability<IonProvider> capability, IonProvider instance, EnumFacing side, NBTBase nbt){

			}

		}, IonProviderImpl::new);

		craftingIonRegistry = new CraftingIonRegistry();
		craftingIonRegistry.registerEnvironmentFluid(FluidRegistry.WATER);

		guiHandler = new CoreGuiHandler();

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
