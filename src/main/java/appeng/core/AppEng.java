package appeng.core;

import appeng.api.module.Module;
import appeng.api.module.ModuleIMCMessageEvent;
import appeng.core.lib.crash.CrashInfo;
import appeng.core.lib.crash.ModCrashEnhancement;
import appeng.core.lib.module.Toposorter;
import code.elix_x.excomms.reflection.ReflectionHelper.AClass;
import code.elix_x.excomms.reflection.ReflectionHelper.AMethod;
import com.google.common.base.Stopwatch;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.common.ForgeVersion;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
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

@Mod(modid = AppEng.MODID, name = AppEng.NAME, version = AEConfig.VERSION/*, dependencies = AppEng.MOD_DEPENDENCIES*/, acceptedMinecraftVersions = ForgeVersion.mcVersion, guiFactory = "appeng.core.client.gui.config.AEConfigGuiFactory")
public final class AppEng {

	public static final String MODID = "appliedenergistics3";
	public static final String NAME = "Applied Energistics 3";

	public static final String ASSETS = MODID + ":";

	public static final String MOD_DEPENDENCIES =
			// a few mods, AE should load after, probably.
			// required-after:AppliedEnergistics2API|all;
			// "after:gregtech_addon;after:Mekanism;after:IC2;after:ThermalExpansion;after:BuildCraft|Core;" +

			// depend on version of forge used for build.
			"after:appliedenergistics2-core;" + "required-after:Forge@[" // require forge.
					+ net.minecraftforge.common.ForgeVersion.majorVersion + '.' // majorVersion
					+ net.minecraftforge.common.ForgeVersion.minorVersion + '.' // minorVersion
					+ net.minecraftforge.common.ForgeVersion.revisionVersion + '.' // revisionVersion
					+ net.minecraftforge.common.ForgeVersion.buildVersion + ",)"; // buildVersion

	public static final Logger logger = LogManager.getLogger(NAME);

	@Mod.Instance(MODID)
	private static AppEng INSTANCE;

	private ImmutableMap<String, ?> modules;
	private ImmutableMap<Class<?>, ?> classModule;
	private ImmutableList<String> moduleOrder;
	private ImmutableMap<?, Boolean> internal;
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

	public <M> M getCurrent(){
		return (M) current;
	}

	public File getConfigDirectory(){
		return configDirectory;
	}

	private void fireModulesEvent(final FMLEvent event){
		for(String name : moduleOrder){
			fireModuleEvent(name, event);
		}
	}

	private <M> void fireModuleEvent(M module, final FMLEvent event){
		if(module instanceof String){
			module = getModule((String) module);
		}
		if(module instanceof Class){
			module = getModule((Class<M>) module);
		}
		if(module != null){
			for(AMethod<M, ?> method : new AClass<M>((Class<M>) module.getClass()).getDeclaredMethods()){
				if(method.get().getParameterTypes().length == 1 && method.get().getParameterTypes()[0].isAssignableFrom(event.getClass()) && method.get().getDeclaredAnnotation(Module.ModuleEventHandler.class) != null){
					current = module;
					method.invoke(module, event);
					current = null;
				}
			}
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
			CommonHelper.proxy.moduleLoadingException(String.format("Circular dependency at module %s", e.getNode()), "The module " + TextFormatting.BOLD + e.getNode() + TextFormatting.RESET + " has circular dependencies! See the log for a list!");
		}
		ImmutableMap.Builder<String, Object> modulesBuilder = ImmutableMap.builder();
		ImmutableMap.Builder<Class<?>, Object> classModuleBuilder = ImmutableMap.builder();
		ImmutableMap.Builder<Object, Boolean> internalBuilder = ImmutableMap.builder();
		ImmutableList.Builder<String> orderBuilder = ImmutableList.builder();

		for(String name : moduleLoadingOrder){
			try{
				Class<?> moduleClass = modules.get(name);
				boolean mod = moduleClass.isAnnotationPresent(Mod.class);
				MutableObject<Object> moduleHolder = new MutableObject<>();
				if(mod){
					Loader.instance().getModObjectList().entrySet().stream().filter(entry -> entry.getValue().getClass() == moduleClass).findFirst().ifPresent(instance -> moduleHolder.setValue(instance.getValue()));
				}
				if(moduleHolder.getValue() == null){
					moduleHolder.setValue(moduleClass.newInstance());
				}
				Object module = moduleHolder.getValue();
				orderBuilder.add(name);
				modulesBuilder.put(name, module);
				classModuleBuilder.put(moduleClass, module);
				internalBuilder.put(module, !mod);
			} catch(ReflectiveOperationException e){
				event.getModLog().error("Error while trying to setup the module " + name);
				e.printStackTrace();
			}
		}

		this.moduleOrder = orderBuilder.build();
		this.modules = modulesBuilder.build();
		this.classModule = classModuleBuilder.build();
		this.internal = internalBuilder.build();

		populateInstances(annotations);

		logger.info("Succesfully loaded %s modules", modules.size());

		final Stopwatch watch = Stopwatch.createStarted();
		logger.info("Pre Initialization ( started )");

		fireModulesEvent(event);

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
					CommonHelper.proxy.moduleLoadingException(String.format("Module %s is %s side only!", name, requiredSide.toString()), "Module " + TextFormatting.BOLD + name + TextFormatting.RESET + " can only be used on " + TextFormatting.BOLD + requiredSide.toString() + TextFormatting.RESET + "!");
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
						CommonHelper.proxy.moduleLoadingException(String.format("Missing hard required dependency for module %s - %s", name, depName), "Module " + TextFormatting.BOLD + name + TextFormatting.RESET + " is missing required hard dependency " + TextFormatting.BOLD + depName + TextFormatting.RESET + ".");
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
				Object instance = modules.get(data.getAnnotationInfo().get("value"));
				if(instance == null){
					instance = classModule.get(Class.forName((String) data.getAnnotationInfo().get("value")));
				}
				AClass<I> target = new AClass(Class.forName(data.getClassName(), true, mcl));
				target.getDeclaredField(data.getObjectName()).setAccessible(true).setFinal(false).set((I) classModule.get(target.getClass()), instance);
			} catch(ReflectiveOperationException e){
				e.printStackTrace();
				// :(
			}
		}
	}

	@EventHandler
	private void init(final FMLInitializationEvent event){
		final Stopwatch start = Stopwatch.createStarted();
		logger.info("Initialization ( started )");

		fireModulesEvent(event);

		logger.info("Initialization ( ended after " + start.elapsed(TimeUnit.MILLISECONDS) + "ms )");
	}

	@EventHandler
	private void postInit(final FMLPostInitializationEvent event){
		final Stopwatch start = Stopwatch.createStarted();
		logger.info("Post Initialization ( started )");

		fireModulesEvent(event);

		logger.info("Post Initialization ( ended after " + start.elapsed(TimeUnit.MILLISECONDS) + "ms )");
	}

	@EventHandler
	private void handleIMCEvent(final FMLInterModComms.IMCEvent event){
		for(IMCMessage message : event.getMessages()){
			fireModuleEvent(message.key, new ModuleIMCMessageEvent(message));
		}
	}

	@EventHandler
	private void serverAboutToStart(final FMLServerAboutToStartEvent event){
		fireModulesEvent(event);
	}

	@EventHandler
	private void serverStarting(final FMLServerStartingEvent event){
		fireModulesEvent(event);
	}

	@EventHandler
	private void serverStopping(final FMLServerStoppingEvent event){
		fireModulesEvent(event);
	}

	@EventHandler
	private void serverStopped(final FMLServerStoppedEvent event){
		fireModulesEvent(event);
	}
}
