package appeng.core.core.crafting.ion;

import appeng.api.bootstrap.InitializationComponent;
import appeng.api.config.ConfigCompilable;
import appeng.core.AppEng;
import appeng.core.core.AppEngCore;
import appeng.core.core.api.crafting.ion.Ion;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import net.minecraft.util.ResourceLocation;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class IonCraftingConfig implements ConfigCompilable, InitializationComponent.Init  {

	private Map<String, List<ResourceLocation>> oreDictToIons = new HashMap<>();
	public transient Multimap<String, Ion> oreDictToIonsM;

	public IonCraftingConfig(){
		oreDictToIons.put("gemQuartz", Lists.newArrayList(new ResourceLocation(AppEng.MODID, "quartz")));
		oreDictToIons.put("dustRedstone", Lists.newArrayList(new ResourceLocation(AppEng.MODID, "redstone")));
		oreDictToIons.put("gunpowder", Lists.newArrayList(new ResourceLocation(AppEng.MODID, "sulfur")));
		oreDictToIons.put("dustSulfur", Lists.newArrayList(new ResourceLocation(AppEng.MODID, "sulfur")));
		oreDictToIons.put("enderpearl", Lists.newArrayList(new ResourceLocation(AppEng.MODID, "ender")));
	}

	@Override
	public void compile(){

	}

	@Override
	public void init(){
		oreDictToIonsM = HashMultimap.create();
		oreDictToIons.forEach((ore, ions) -> ions.forEach(ionr -> Optional.ofNullable(AppEngCore.INSTANCE.getIonRegistry().getValue(ionr)).ifPresent(ion -> oreDictToIonsM.put(ore, ion))));
	}

	@Override
	public void decompile(){
		oreDictToIons.clear();
		oreDictToIonsM.keySet().forEach(ore -> oreDictToIons.put(ore, oreDictToIonsM.get(ore).stream().map(Ion::getRegistryName).collect(Collectors.toList())));
	}

}
