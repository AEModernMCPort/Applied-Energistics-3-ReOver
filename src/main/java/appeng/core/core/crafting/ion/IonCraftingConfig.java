package appeng.core.core.crafting.ion;

import appeng.api.bootstrap.InitializationComponent;
import appeng.api.config.ConfigCompilable;
import appeng.core.AppEng;
import appeng.core.core.AppEngCore;
import appeng.core.core.api.crafting.ion.Ion;
import com.google.common.collect.*;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.MutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.util.*;
import java.util.stream.Collectors;

public class IonCraftingConfig implements ConfigCompilable, InitializationComponent.Init  {

	private Map<String, List<MutablePair<String, Integer>>> oreDict2Ions = new HashMap<>();
	public transient Multimap<String, Pair<Ion, Integer>> oreDict2IonsC = HashMultimap.create();

	private Map<String, Reactivity> oreDict2Reactivity = new HashMap<>();
	public transient Map<String, Reactivity.Compiled> oreDict2ReactivityC = new HashMap<>();

	public IonCraftingConfig(){
		oreDict2Ions.put("gemQuartz", Lists.newArrayList(new MutablePair<>(new ResourceLocation(AppEng.MODID, "quartz").toString(), 1)));
		oreDict2Ions.put("dustRedstone", Lists.newArrayList(new MutablePair<>(new ResourceLocation(AppEng.MODID, "redstone").toString(), 1)));
		oreDict2Ions.put("gunpowder", Lists.newArrayList(new MutablePair<>(new ResourceLocation(AppEng.MODID, "sulfur").toString(), 1)));
		oreDict2Ions.put("dustSulfur", Lists.newArrayList(new MutablePair<>(new ResourceLocation(AppEng.MODID, "sulfur").toString(), 1)));
		oreDict2Ions.put("enderpearl", Lists.newArrayList(new MutablePair<>(new ResourceLocation(AppEng.MODID, "ender").toString(), 1)));

		oreDict2Ions.put("certusQuartz", Lists.newArrayList(new MutablePair<>(new ResourceLocation(AppEng.MODID, "certus").toString(), 1), new MutablePair<>(new ResourceLocation(AppEng.MODID, "quartz").toString(), 3)));
		oreDict2Ions.put("certusRedstone", Lists.newArrayList(new MutablePair<>(new ResourceLocation(AppEng.MODID, "certus").toString(), 1), new MutablePair<>(new ResourceLocation(AppEng.MODID, "redstone").toString(), 3)));
		oreDict2Ions.put("certusSulfur", Lists.newArrayList(new MutablePair<>(new ResourceLocation(AppEng.MODID, "certus").toString(), 1), new MutablePair<>(new ResourceLocation(AppEng.MODID, "sulfur").toString(), 2)));
		oreDict2Ions.put("incertus", Lists.newArrayList(new MutablePair<>(new ResourceLocation(AppEng.MODID, "certus").toString(), 1), new MutablePair<>(new ResourceLocation(AppEng.MODID, "ender").toString(), 1)));


		oreDict2Reactivity.put("certusQuartz", new Reactivity(false, Sets.newHashSet("water")));
		oreDict2Reactivity.put("certusRedstone", new Reactivity(false, Sets.newHashSet("water")));
		oreDict2Reactivity.put("certusSulfur", new Reactivity(false, Sets.newHashSet("water")));
		oreDict2Reactivity.put("incertus", new Reactivity(false, Sets.newHashSet("water")));
	}

	@Override
	public void compile(){

	}

	@Override
	public void init(){
		oreDict2IonsC.clear();
		oreDict2Ions.forEach((ore, ions) -> ions.forEach(ionr -> Optional.ofNullable(AppEngCore.INSTANCE.getIonRegistry().getValue(new ResourceLocation(ionr.getKey()))).ifPresent(ion -> oreDict2IonsC.put(ore, new ImmutablePair<>(ion, ionr.getRight())))));

		oreDict2ReactivityC.clear();
		oreDict2ReactivityC.putAll(Maps.transformValues(oreDict2Reactivity, Reactivity::compile));
	}

	@Override
	public void decompile(){
		oreDict2Ions.clear();
		oreDict2IonsC.keySet().forEach(ore -> oreDict2Ions.put(ore, oreDict2IonsC.get(ore).stream().map(ion -> new MutablePair<>(ion.getLeft().getRegistryName().toString(), ion.getRight())).collect(Collectors.toList())));

		oreDict2Reactivity.clear();
		oreDict2Reactivity.putAll(Maps.transformValues(oreDict2ReactivityC, Reactivity.Compiled::decompile));
	}

	public static class Reactivity {

		public boolean def = false;
		public Set<String> fluids = new HashSet<>();

		public Reactivity(){

		}

		public Reactivity(boolean def, Set<String> fluids){
			this.def = def;
			this.fluids = fluids;
		}

		Compiled compile(){
			return new Compiled(def, fluids.stream().map(FluidRegistry::getFluid).collect(Collectors.toSet()));
		}

		public static class Compiled {

			public boolean def = false;
			public Set<Fluid> fluids = new HashSet<>();

			public Compiled(){

			}

			public Compiled(boolean def, Set<Fluid> fluids){
				this.def = def;
				this.fluids = fluids;
			}

			Reactivity decompile(){
				return new Reactivity(def, fluids.stream().map(Fluid::getName).collect(Collectors.toSet()));
			}

		}

	}

}
