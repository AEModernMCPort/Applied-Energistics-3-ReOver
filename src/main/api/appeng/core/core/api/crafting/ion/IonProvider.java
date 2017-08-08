package appeng.core.core.api.crafting.ion;

import java.util.Map;

public interface IonProvider {

	Map<Ion, Integer> getIons();

	default boolean isReactive(){
		return false;
	}

}
