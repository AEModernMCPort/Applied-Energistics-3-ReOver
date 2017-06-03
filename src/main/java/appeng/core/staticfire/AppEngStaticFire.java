package appeng.core.staticfire;

import appeng.api.bootstrap.DefinitionFactory;
import appeng.api.bootstrap.InitializationComponentsHandler;
import appeng.api.definitions.IDefinition;
import appeng.api.definitions.IDefinitions;
import appeng.api.module.AEStateEvent;
import appeng.api.module.Module;
import appeng.core.AppEng;
import appeng.core.lib.bootstrap.InitializationComponentsHandlerImpl;
import appeng.core.staticfire.api.IStaticFire;
import appeng.core.staticfire.proxy.StaticFireProxy;
import appeng.core.staticfire.definitions.StaticFireBlockDefinitions;
import net.minecraft.block.Block;
import net.minecraftforge.common.ForgeVersion;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLInterModComms;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

@Module(IStaticFire.NAME)
public class AppEngStaticFire implements IStaticFire{

    @Module.Instance(NAME)
    public static final AppEngStaticFire INSTANCE = null;

    @SidedProxy(modId = AppEng.MODID, clientSide = "appeng.core.staticfire.proxy.StaticFireClientProxy", serverSide = "appeng.core.staticfire.proxy.StaticFireServerProxy")
    public static StaticFireProxy proxy;

    private InitializationComponentsHandler initHandler = new InitializationComponentsHandlerImpl();

    private DefinitionFactory registry;

    //private CraftingItemDefinitions itemDefinitions;
    private StaticFireBlockDefinitions blockDefinitions;
    //private CraftingTileDefinitions tileDefinitions;

    @Override
    public <T, D extends IDefinitions<T, ? extends IDefinition<T>>> D definitions(Class<T> clas){
        /*
        if(clas == Item.class){
            return (D) itemDefinitions;
        }
        */
        if(clas == Block.class){
            return (D) blockDefinitions;
        }
        /*
        if(clas == TileEntity.class){
            return (D) tileDefinitions;
        }
        */
        return null;
    }

    @Module.ModuleEventHandler
    public void preInitAE(AEStateEvent.AEPreInitializationEvent event){
        registry = event.factory(initHandler, proxy);
        this.blockDefinitions = new StaticFireBlockDefinitions(registry);
        //this.itemDefinitions = new CraftingItemDefinitions(registry);
        //this.tileDefinitions = new CraftingTileDefinitions(registry);

        initHandler.preInit();
        proxy.preInit(event);
    }

    @Mod.EventHandler
    public void preInitForge(FMLPreInitializationEvent event){

    }

    @Module.ModuleEventHandler
    public void initAE(final AEStateEvent.AEInitializationEvent event){
        initHandler.init();
        proxy.init(event);
    }

    @Mod.EventHandler
    public void initForge(final FMLInitializationEvent event){

    }

    @Module.ModuleEventHandler
    public void postInitAE(final AEStateEvent.AEPostInitializationEvent event){
        initHandler.postInit();
        proxy.postInit(event);
    }

    @Mod.EventHandler
    public void postInitForge(final FMLPostInitializationEvent event){

    }

    @Module.ModuleEventHandler
    public void handleIMCEventAE(AEStateEvent.ModuleIMCMessageEvent event){

    }

    @Mod.EventHandler
    public void handleIMCEventForge(FMLInterModComms.IMCEvent event){

    }

}
