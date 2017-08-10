package appeng.core.core.crafting.ion;

import appeng.core.AppEng;
import appeng.core.core.api.crafting.ion.Ion;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.oredict.OreDictionary;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class IonCraftingConfig {

	private Map<String, List<ResourceLocation>> oreDictToIons = new HashMap<>();
	public transient Multimap<String, Ion> oreDictToIonsM;

	public IonCraftingConfig(){
		oreDictToIons.put("gemQuartz", Lists.newArrayList(new ResourceLocation(AppEng.MODID, "quartz")));
		oreDictToIons.put("dustRedstone", Lists.newArrayList(new ResourceLocation(AppEng.MODID, "redstone")));
		oreDictToIons.put("gunpowder", Lists.newArrayList(new ResourceLocation(AppEng.MODID, "sulfur")));
		oreDictToIons.put("dustSulfur", Lists.newArrayList(new ResourceLocation(AppEng.MODID, "sulfur")));
		oreDictToIons.put("enderpearl", Lists.newArrayList(new ResourceLocation(AppEng.MODID, "ender")));
	}

}
