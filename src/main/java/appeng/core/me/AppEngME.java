package appeng.core.me;

import appeng.api.definitions.IDefinition;
import appeng.api.definitions.IDefinitions;
import appeng.api.module.AEStateEvent;
import appeng.api.module.Module;
import appeng.api.module.Module.ModuleEventHandler;
import appeng.core.api.material.Material;
import appeng.core.lib.bootstrap_olde.FeatureFactory;
import appeng.core.me.api.IME;
import appeng.core.me.definitions.*;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fml.common.registry.EntityEntry;

@Module(IME.NAME)
public class AppEngME implements IME {

	@Module.Instance(NAME)
	public static final AppEngME INSTANCE = null;

	private FeatureFactory registry;

	private MEItemDefinitions itemDefinitions;
	private MEBlockDefinitions blockDefinitions;
	private METileDefinitions tileDefinitions;
	private MEMaterialDefinitions materialDefinitions;
	private MEEntityDefinitions entityDefinitions;

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
		if(clas == EntityEntry.class){
			return (D) entityDefinitions;
		}
		return null;
	}

	@ModuleEventHandler
	public void preInit(AEStateEvent.AEPreInitializationEvent event){
		registry = new FeatureFactory();
		this.materialDefinitions = new MEMaterialDefinitions(registry);
		this.blockDefinitions = new MEBlockDefinitions(registry);
		this.itemDefinitions = new MEItemDefinitions(registry);
		this.tileDefinitions = new METileDefinitions(registry);
		this.entityDefinitions = new MEEntityDefinitions(registry);
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
