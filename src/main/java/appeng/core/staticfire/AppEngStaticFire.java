package appeng.core.staticfire;

import appeng.api.bootstrap.DefinitionFactory;
import appeng.api.bootstrap.InitializationComponentsHandler;
import appeng.api.config.ConfigurationLoader;
import appeng.api.definitions.IDefinition;
import appeng.api.definitions.IDefinitions;
import appeng.api.module.AEStateEvent;
import appeng.api.module.Module;
import appeng.core.AppEng;
import appeng.core.api.net.gui.GuiHandler;
import appeng.core.core.AppEngCore;
import appeng.core.lib.bootstrap.InitializationComponentsHandlerImpl;
import appeng.core.staticfire.api.IStaticFire;
import appeng.core.staticfire.block.TestBlock;
import appeng.core.staticfire.definitions.StaticFireItemDefinitions;
import appeng.core.staticfire.gui.StaticFireGuiHandler;
import appeng.core.staticfire.gui.TestGui;
import appeng.core.staticfire.proxy.StaticFireProxy;
import appeng.core.staticfire.definitions.StaticFireBlockDefinitions;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.ForgeVersion;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLInterModComms;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;

import java.io.IOException;

@Module(IStaticFire.NAME)
public class AppEngStaticFire implements IStaticFire{

    //@Module.Instance(IStaticFire.NAME)
    public static final AppEngStaticFire INSTANCE = null;

    @SidedProxy(modId = AppEng.MODID, clientSide = "appeng.core.staticfire.proxy.StaticFireClientProxy", serverSide = "appeng.core.staticfire.proxy.StaticFireServerProxy")
    public static StaticFireProxy proxy;

    private InitializationComponentsHandler initHandler = new InitializationComponentsHandlerImpl();

    private DefinitionFactory registry;

    private StaticFireItemDefinitions itemDefinitions;
    private StaticFireBlockDefinitions blockDefinitions;
    //private CraftingTileDefinitions tileDefinitions;

    public StaticFireConfig config;

    @Override
    public <T, D extends IDefinitions<T, ? extends IDefinition<T>>> D definitions(Class<T> clas){

        if(clas == Item.class){
            return (D) itemDefinitions;
        }

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

        ConfigurationLoader<StaticFireConfig> configLoader = event.configurationLoader();
        try{
            configLoader.load(StaticFireConfig.class);
        } catch(IOException e){
            //TODO 1.11.2-ReOver-StaticFire - handle IOs
        }

        config = configLoader.configuration();

        registry = event.factory(initHandler, proxy);
        this.itemDefinitions = new StaticFireItemDefinitions(registry);
        this.blockDefinitions = new StaticFireBlockDefinitions(registry);

        this.itemDefinitions.init(registry);
        this.blockDefinitions.init(registry);

        initHandler.preInit();
        proxy.preInit(event);

        try{
            configLoader.save();
        } catch(IOException e){
            //TODO 1.11.2-ReOver - handle IOs
        }

    }

    @Mod.EventHandler
    public void preInitForge(FMLPreInitializationEvent event){

    }

    @Module.ModuleEventHandler
    public void initAE(final AEStateEvent.AEInitializationEvent event){
        initHandler.init();
        proxy.init(event);
        //NetworkRegistry.INSTANCE.registerGuiHandler(AppEng.instance(), new StaticFireGuiHandler());
        AppEngCore.INSTANCE.guiHandler().registerGuiElement(new ResourceLocation(AppEng.MODID, "TestGui"), new TestGui());
        AppEngCore.INSTANCE.guiHandler().registerGuiClientElement(new ResourceLocation(AppEng.MODID, "TestGui"), new TestGui());
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
