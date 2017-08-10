package appeng.core.lib.oredict;

import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

import java.util.Arrays;
import java.util.stream.Stream;

public class OreDictHelper {

	public static Stream<String> getOres(ItemStack stack){
		return Arrays.stream(OreDictionary.getOreIDs(stack)).mapToObj(OreDictionary::getOreName);
	}

}
