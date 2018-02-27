package appeng.debug;

import appeng.api.bootstrap.DefinitionFactory;
import appeng.api.bootstrap.InitializationComponentsHandler;
import appeng.api.config.ConfigurationLoader;
import appeng.api.definition.IDefinition;
import appeng.api.definitions.IDefinitions;
import appeng.api.module.AEStateEvent;
import appeng.api.module.Module;
import appeng.core.AppEng;
import appeng.core.lib.bootstrap.InitializationComponentsHandlerImpl;
import appeng.core.lib.raytrace.RayTraceHelper;
import appeng.core.me.api.network.block.ConnectionPassthrough;
import appeng.core.me.api.parts.VoxelPosition;
import appeng.core.me.api.parts.container.PartInfo;
import appeng.core.me.api.parts.container.PartsAccess;
import appeng.core.me.api.parts.placement.VoxelRayTraceHelper;
import appeng.core.me.network.block.NetBlockDevicesManager;
import appeng.core.me.network.block.NetBlockImpl;
import appeng.core.me.parts.part.PartsHelper;
import appeng.debug.config.DebugConfig;
import appeng.debug.definitions.DebugBlockDefinitions;
import appeng.debug.definitions.DebugItemDefinitions;
import appeng.debug.definitions.DebugTileDefinitions;
import appeng.debug.proxy.DebugProxy;
import net.minecraft.block.Block;
import net.minecraft.init.Items;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.Optional;

/*
 * The only module not built with gradle.
 */
@Module(AppEngDebug.NAME)
public class AppEngDebug {

	public static final String NAME = "debug";

	public static final Logger logger = LogManager.getLogger(AppEng.NAME + "|"+ NAME);

	@Module.Instance
	public static final AppEngDebug INSTANCE = null;

	@SidedProxy(modId = AppEng.MODID, clientSide = "appeng.debug.proxy.DebugClientProxy", serverSide = "appeng.debug.proxy.DebugServerProxy")
	public static DebugProxy proxy;

	public DebugConfig config;

	private InitializationComponentsHandler initHandler = new InitializationComponentsHandlerImpl();

	private DebugItemDefinitions itemDefinitions;
	private DebugBlockDefinitions blockDefinitions;
	private DebugTileDefinitions tileDefinitions;

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

	@Module.ModuleEventHandler
	public void preInitAE(AEStateEvent.AEPreInitializationEvent event){
		ConfigurationLoader<DebugConfig> configLoader = event.configurationLoader();
		try{
			configLoader.load(DebugConfig.class);
		} catch(IOException e){
			logger.error("Caught exception loading configuration", e);
		}
		config = configLoader.configuration();

		DefinitionFactory registry = event.factory(initHandler, proxy);
		this.itemDefinitions = new DebugItemDefinitions(registry);
		this.blockDefinitions = new DebugBlockDefinitions(registry);
		this.tileDefinitions = new DebugTileDefinitions(registry);

		this.itemDefinitions.init(registry);
		this.blockDefinitions.init(registry);
		this.tileDefinitions.init(registry);

		MinecraftForge.EVENT_BUS.register(this);

		initHandler.preInit();
		proxy.preInit(event);

		try{
			configLoader.save();
		} catch(IOException e){
			logger.error("Caught exception saving configuration", e);
		}
	}

	@Module.ModuleEventHandler
	public void initAE(final AEStateEvent.AEInitializationEvent event){
		initHandler.init();
		proxy.init(event);
	}

	@Module.ModuleEventHandler
	public void postInitAE(final AEStateEvent.AEPostInitializationEvent event){
		initHandler.postInit();
		proxy.postInit(event);
	}

	@SubscribeEvent
	public void graphDebugStick(PlayerInteractEvent.RightClickItem event){
		if(!event.getWorld().isRemote && event.getEntityPlayer().getItemStackFromSlot(event.getHand() == EnumHand.MAIN_HAND ? EntityEquipmentSlot.MAINHAND : EntityEquipmentSlot.OFFHAND).getItem() == Items.STICK){
			RayTraceResult trace = RayTraceHelper.rayTrace(event.getEntityPlayer());
			if(trace.hitInfo instanceof VoxelPosition){
				VoxelPosition targetVoxel = VoxelRayTraceHelper.getOrApproximateHitVoxel(trace);
				PartsAccess.Mutable worldPartsAccess = event.getEntityPlayer().world.getCapability(PartsHelper.worldPartsAccessCapability, null);
				worldPartsAccess.getPart(targetVoxel).flatMap(PartInfo::getState).ifPresent(s -> {
					if(s instanceof ConnectionPassthrough){
						ConnectionPassthrough cp = (ConnectionPassthrough) s;
						cp.getAssignedNetBlock().ifPresent(netBlock -> {
							NetBlockDevicesManager dm = ((NetBlockImpl) netBlock).devicesManager;
							Optional pe = dm.getElement(cp.getUUIDForConnectionPassthrough());
							event.getEntityPlayer().sendMessage(new TextComponentString("PE " + pe.orElse(null)));
							pe.ifPresent(ppe -> event.getEntityPlayer().sendMessage(new TextComponentString("DSect " + dm.getDSect((NetBlockDevicesManager.PathwayElement) ppe))));
						});
					}
				});
			}
		}
	}

}
