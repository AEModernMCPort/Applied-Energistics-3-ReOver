package appeng.core.core.api.crafting.ion;

import net.minecraftforge.fluids.Fluid;

import java.util.Map;

public interface IonProvider {

	Map<Ion, Integer> getIons();

	default boolean isReactive(Fluid fluid){
		return false;
	}

}
