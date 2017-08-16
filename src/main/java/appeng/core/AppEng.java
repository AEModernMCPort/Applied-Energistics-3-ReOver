package appeng.core;

import appeng.api.AEModInfo;
import appeng.api.bootstrap.DefinitionBuilderSupplier;
import appeng.api.config.ConfigurationLoader;
import appeng.api.module.AEStateEvent;
import appeng.api.module.Module;
import appeng.core.lib.module.AEStateEventImpl;
import appeng.core.lib.module.Toposorter;
import appeng.core.proxy.AppEngProxy;
import code.elix_x.excomms.reflection.ReflectionHelper;
import code.elix_x.excomms.reflection.ReflectionHelper.AClass;
import com.google.common.base.Stopwatch;
import com.google.common.collect.*;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.discovery.ASMDataTable;
import net.minecraftforge.fml.common.discovery.ASMDataTable.ASMData;
import net.minecraftforge.fml.common.event.*;
import net.minecraftforge.fml.common.event.FMLInterModComms.IMCMessage;
import net.minecraftforge.fml.relauncher.Side;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.mutable.MutableObject;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nonnull;
import java.io.File;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.BiFunction;

@Mod(modid = AppEng.MODID, name = AppEng.NAME, version = AppEng.VERSION, dependencies = AppEng.DEPENDENCIES)
public final class AppEng {

	public static final String MODID = AEModInfo.MODID;
	public static final String NAME = AEModInfo.NAME;
	public static final String VERSION = AEModInfo.VERSION;

	public static final String ASSETS = MODID + ":";

	public static final String DEPENDENCIES = "";

	public static final Logger logger = LogManager.getLogger(NAME);

	@Mod.Instance(MODID)
	private static AppEng INSTANCE;

	@SidedProxy(modId = MODID, clientSide = "appeng.core.proxy.AppEngClientProxy", serverSide = "appeng.core.proxy.AppEngServerProxy")
	private static AppEngProxy proxy;

	private ImmutableBiMap<String, ?> modules;
	private ImmutableMap<Class<?>, ?> classModule;
	private ImmutableList<String> moduleOrder;
	private Object current;

	private File configDirectory;

	public AppEng(){

	}

	@Nonnull
	public static AppEng instance(){
		return INSTANCE;
	}

	@Deprecated
	public <M> M getModule(String name){
		return (M) modules.get(name);
	}

	@Deprecated
	public <M> M getModule(Class<M> clas){
		return (M) classModule.get(clas);
	}

	@Deprecated
	public <M> String getName(M module){
		return modules.inverse().get(module);
	}

	public <M> M getCurrent(){
		return (M) current;
	}

	public String getCurrentName(){
		return getName(getCurrent());
	}

	public File getConfigDirectory(){
		return configDirectory;
	}

	private void fireModulesEvent(final AEStateEvent event){
		for(String name : moduleOrder){
			fireModuleEvent(name, event);
		}
	}

	private <M> void fireModuleEvent(M m, final AEStateEvent event){
		M module;
		if(m instanceof String) module = getModule((String) m);
		else if(m instanceof Class) module = getModule((Class<M>) m);
		else module = m;
		if(module != null){
			new AClass<M>((Class<M>) module.getClass()).getDeclaredMethods().forEach(method -> {
				if(method.get().getParameterTypes().length == 1 && method.get().getParameterTypes()[0].isAssignableFrom(event.getClass()) && method.get().getDeclaredAnnotation(Module.ModuleEventHandler.class) != null){
					current = module;
					method.invoke(module, event);
					current = null;
				}
			});
		}
	}

	@EventHandler
	private void preInit(final FMLPreInitializationEvent event){
		Map<String, Pair<Class<?>, String>> foundModules = new HashMap<>();
		ASMDataTable annotations = event.getAsmData();
		for(ASMData data : annotations.getAll(Module.class.getCanonicalName())){
			foundModules.put((String) data.getAnnotationInfo().get("value"), new ImmutablePair<Class<?>, String>(new AClass<>(data.getClassName()).get(), (String) data.getAnnotationInfo().get("dependencies")));
		}

		Map<String, Class<?>> modules = Maps.newHashMap();
		for(Map.Entry<String, Pair<Class<?>, String>> entry : foundModules.entrySet()){
			if(isValid(entry.getKey(), foundModules, event.getSide(), Lists.newLinkedList())){
				modules.put(entry.getKey(), entry.getValue().getLeft());
			}
		}
		Toposorter.Graph<String> graph = new Toposorter.Graph<String>();
		Toposorter.Graph<String>.Node beforeall = graph.addNewNode(":beforeall", ":beforeall");
		Toposorter.Graph<String>.Node afterall = graph.addNewNode(":afterall", ":afterall");
		for(String name : modules.keySet()){
			addAsNode(name, foundModules, graph, event.getSide());
		}
		for(Toposorter.Graph<String>.Node n : graph.getAllNodes()){
			if(n.getName().startsWith(":")) continue;
			if(n.getDependencies().isEmpty() && !n.getWhatDependsOnMe().contains(beforeall)){
				n.dependOn(beforeall);
			}
			if(n.getWhatDependsOnMe().isEmpty() && !n.getDependencies().contains(afterall)){
				n.dependencyOf(afterall);
			}
		}

		List<String> moduleLoadingOrder = null;
		try{
			moduleLoadingOrder = Toposorter.toposort(graph);
			moduleLoadingOrder.removeIf((s) -> s.startsWith(":"));
		} catch(Toposorter.SortingException e){
			boolean moduleFound = false;
			event.getModLog().error("Module " + e.getNode() + " has circular dependencies:");
			for(String s : e.getVisitedNodes()){
				if(s.equals(e.getNode())){
					if(moduleFound){
						break;
					}
					moduleFound = true;
					event.getModLog().error("\"" + s + "\"");
					continue;
				}
				if(moduleFound){
					event.getModLog().error("depending on: \"" + s + "\"");
				}
			}
			event.getModLog().error("again depending on \"" + e.getNode() + "\"");
			proxy.moduleLoadingException(String.format("Circular dependency at module %s", e.getNode()), "The module " + TextFormatting.BOLD + e.getNode() + TextFormatting.RESET + " has circular dependencies! See the log for a list!");
		}
		ImmutableBiMap.Builder<String, Object> modulesBuilder = ImmutableBiMap.builder();
		ImmutableMap.Builder<Class<?>, Object> classModuleBuilder = ImmutableMap.builder();
		ImmutableList.Builder<String> orderBuilder = ImmutableList.builder();

		for(String name : moduleLoadingOrder){
			try{
				Class<?> moduleClass = modules.get(name);
				MutableObject<Object> moduleHolder = new MutableObject<>();
				if(moduleClass.isAnnotationPresent(Mod.class)) Loader.instance().getModObjectList().entrySet().stream().filter(entry -> entry.getValue().getClass() == moduleClass).findFirst().ifPresent(instance -> moduleHolder.setValue(instance.getValue()));
				if(moduleHolder.getValue() == null) moduleHolder.setValue(moduleClass.newInstance());
				Object module = moduleHolder.getValue();
				orderBuilder.add(name);
				modulesBuilder.put(name, module);
				classModuleBuilder.put(moduleClass, module);
			} catch(ReflectiveOperationException e){
				event.getModLog().error("Error while trying to setup the module " + name);
				e.printStackTrace();
			}
		}

		this.moduleOrder = orderBuilder.build();
		this.modules = modulesBuilder.build();
		this.classModule = classModuleBuilder.build();

		populateInstances(annotations);

		logger.info(String.format("Succesfully loaded %s modules", modules.size()));

		configDirectory = new File(event.getModConfigurationDirectory(), NAME);
		configDirectory.mkdirs();

		Map<String, BiFunction<String, Boolean, ConfigurationLoader>> configurationLoaderProviders = new HashMap<>();
		Map<Pair<Class, Class>, DefinitionBuilderSupplier> definitionBuilderSuppliers = new HashMap<>();
		fireModulesEvent(new AEStateEventImpl.AEBootstrapEventImpl(configurationLoaderProviders, definitionBuilderSuppliers));

		Configuration config = new Configuration(new File(event.getModConfigurationDirectory(), NAME + ".cfg"));
		config.load();
		BiFunction<String, Boolean, ConfigurationLoader> configurationLoaderProvider = configurationLoaderProviders.get(config.getString("Configuration Loader Provider", "CONFIG", "JSON", "Configuration loader provider to use for configuration loading.\nOne of: " + String.join(", ", configurationLoaderProviders.keySet()), configurationLoaderProviders.keySet().toArray(new String[0])));
		boolean dynamicDefaults = config.getBoolean("Dynamic Defaults", "CONFIG", true, "Do not write default values to config & feature files (excluding this one)");
		config.save();

		final Stopwatch watch = Stopwatch.createStarted();
		logger.info("Pre Initialization ( started )");

		fireModulesEvent(new AEStateEventImpl.AEPreInitializationEventImpl(configurationLoaderProvider, dynamicDefaults, definitionBuilderSuppliers));

		logger.info("Pre Initialization ( ended after " + watch.elapsed(TimeUnit.MILLISECONDS) + "ms )");
	}

	/**
	 * Checks whether all required dependencies are here
	 */
	private boolean isValid(String name, Map<String, Pair<Class<?>, String>> modules, Side currentSide, LinkedList<String> modulesBeingChecked) // LinkedList is list and stack
	{
		if(modulesBeingChecked.contains(name)) return true; // A module depends on itself, so we assume it works
		if(!modules.containsKey(name)) return false;
		if(modules.get(name).getRight() == null || modules.get(name).getRight().equals("")) return true;
		boolean hasBefore = false, hasAfter = false, hasBeforeAll = false, hasAfterAll = false;
		for(String dep : modules.get(name).getRight().split(";")){
			String[] temp = dep.split(":");
			if(temp.length == 0){
				continue;
			}
			String[] modifiers = temp[0].split("\\-");
			String depName = temp.length > 0 ? temp[1] : null;
			Side requiredSide = ArrayUtils.contains(modifiers, "client") ? Side.CLIENT : ArrayUtils.contains(modifiers, "server") ? Side.SERVER : currentSide;
			boolean hard = ArrayUtils.contains(modifiers, "hard");
			boolean crash = hard && ArrayUtils.contains(modifiers, "crash");
			boolean before = ArrayUtils.contains(modifiers, "before");
			boolean after = ArrayUtils.contains(modifiers, "after");
			if(name == null){
				if(requiredSide == currentSide){
					continue;
				} else if(crash){
					proxy.moduleLoadingException(String.format("Module %s is %s side only!", name, requiredSide.toString()), "Module " + TextFormatting.BOLD + name + TextFormatting.RESET + " can only be used on " + TextFormatting.BOLD + requiredSide.toString() + TextFormatting.RESET + "!");
				}
				return false;
			} else if(depName != null && hard){
				String what = depName.substring(0, depName.indexOf('-'));
				String which = depName.substring(depName.indexOf('-') + 1, depName.length());
				boolean depFound = false;
				if(requiredSide == currentSide){
					if(what.equals("mod")){
						depFound = Loader.isModLoaded(which);
					} else if(what.equals("module")){
						if(which.equals("*")){ // All modules
							depFound = true;
							if(before) hasBeforeAll = true;
							if(after) hasAfterAll = true;
						} else {
							modulesBeingChecked.push(name);
							depFound = isValid(which, modules, currentSide, modulesBeingChecked);
							modulesBeingChecked.pop();
							if(after) hasAfter = true;
							if(before) hasBefore = true;
						}
					}
				}
				if(!depFound){
					if(crash){
						proxy.moduleLoadingException(String.format("Missing hard required dependency for module %s - %s", name, depName), "Module " + TextFormatting.BOLD + name + TextFormatting.RESET + " is missing required hard dependency " + TextFormatting.BOLD + depName + TextFormatting.RESET + ".");
					}
					return false;
				}
			} else if(!before && !after) // Soft dependency
			{
				return false; // Syntax error
			}
		}
		if(hasAfterAll && (hasBefore || hasBeforeAll)){
			return false;
		}
		if(hasBeforeAll && (hasAfter || hasAfterAll)){
			return false;
		}
		return true;
	}

	private void addAsNode(String name, Map<String, Pair<Class<?>, String>> foundModules, Toposorter.Graph<String> graph, Side currentSide){
		if(graph.hasNode(name)){
			return;
		}
		Toposorter.Graph<String>.Node node = graph.addNewNode(name, name);
		if(foundModules.get(name).getRight() == null || foundModules.get(name).getRight().equals("")){
			return;
		}
		for(String dep : foundModules.get(name).getRight().split(";")){
			String[] temp = dep.split(":");
			if(temp.length == 0) continue;
			String[] modifiers = temp[0].split("\\-");
			String depName = temp.length > 0 ? temp[1] : null;
			Side requiredSide = ArrayUtils.contains(modifiers, "client") ? Side.CLIENT : ArrayUtils.contains(modifiers, "server") ? Side.SERVER : currentSide;
			boolean before = ArrayUtils.contains(modifiers, "before");
			boolean after = ArrayUtils.contains(modifiers, "after");
			if(depName != null){
				String what = depName.substring(0, depName.indexOf('-'));
				String which = depName.substring(depName.indexOf('-') + 1, depName.length());
				if(what.equals("module") && requiredSide == currentSide){
					if(which.equals("*")){
						if(after){
							node.dependOn(graph.getNode(":afterall"));
						} else if(before){
							node.dependencyOf(graph.getNode(":beforeall"));
						}
					} else {
						addAsNode(which, foundModules, graph, currentSide);
						if(after){
							node.dependOn(graph.getNode(which));
						} else if(before){
							node.dependencyOf(graph.getNode(which));
						}
					}
					// "mod" cannot be handled here because AE3 cannot control mod loading else there is no vertex added to this graph
				}
			}
		}
	}

	private <I> void populateInstances(ASMDataTable annotations){
		ClassLoader mcl = Loader.instance().getModClassLoader();

		for(ASMData data : annotations.getAll(Module.Instance.class.getTypeName())){
			try{
				AClass<I> target = new AClass(Class.forName(data.getClassName(), true, mcl));
				ReflectionHelper.AField<I, ?> field = target.getDeclaredField(data.getObjectName());
				modules.values().stream().filter(module -> field.get().getType().isInstance(module)).findFirst().ifPresent(instance -> field.setAccessible(true).setFinal(false).set((I) classModule.get(target.getClass()), instance));
			} catch(ReflectiveOperationException e){
				logger.error("Could not inject module's instance", e);
				// :(
			}
		}
	}

	@EventHandler
	private void init(final FMLInitializationEvent event){
		final Stopwatch start = Stopwatch.createStarted();
		logger.info("Initialization ( started )");

		fireModulesEvent(new AEStateEventImpl.AEInitializationEventImpl());

		logger.info("Initialization ( ended after " + start.elapsed(TimeUnit.MILLISECONDS) + "ms )");
	}

	@EventHandler
	private void postInit(final FMLPostInitializationEvent event){
		final Stopwatch start = Stopwatch.createStarted();
		logger.info("Post Initialization ( started )");

		fireModulesEvent(new AEStateEventImpl.AEPostInitializationEventImpl());

		logger.info("Post Initialization ( ended after " + start.elapsed(TimeUnit.MILLISECONDS) + "ms )");
	}

	@EventHandler
	private void loadComplete(FMLLoadCompleteEvent event){
		fireModulesEvent(new AEStateEventImpl.AELoadCompleteEventImpl());
	}

	@EventHandler
	private void handleIMCEvent(final FMLInterModComms.IMCEvent event){
		for(IMCMessage message : event.getMessages()){
			fireModuleEvent(message.key, new AEStateEventImpl.ModuleIMCMessageEventImpl(message));
		}
	}

	@EventHandler
	private void serverAboutToStart(final FMLServerAboutToStartEvent event){
		fireModulesEvent(new AEStateEventImpl.AEServerAboutToStartEventImpl());
	}

	@EventHandler
	private void serverStarting(final FMLServerStartingEvent event){
		fireModulesEvent(new AEStateEventImpl.AEServerStartingEventImpl());
	}

	@EventHandler
	private void serverStopping(final FMLServerStoppingEvent event){
		fireModulesEvent(new AEStateEventImpl.AEServerStoppingEventImpl());
	}

	@EventHandler
	private void serverStopped(final FMLServerStoppedEvent event){
		fireModulesEvent(new AEStateEventImpl.AEServerStoppedEventImpl());
	}

}
