package appeng.core.core.api.crafting.ion;

import net.minecraftforge.fluids.Fluid;

import java.util.Map;
import java.util.Random;

public interface IonProvider {

	Map<Ion, Integer> getIons(Random random);

	default boolean isReactive(Fluid fluid){
		return false;
	}

}
