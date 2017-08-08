package appeng.core.core.crafting.ion;

import appeng.core.core.api.crafting.ion.Ion;
import appeng.core.core.api.crafting.ion.IonProvider;
import com.google.common.collect.ImmutableMap;

import java.util.Collections;
import java.util.Map;

public class IonProviderImpl implements IonProvider {

	protected final ImmutableMap<Ion, Integer> ions;

	public IonProviderImpl(Map<Ion, Integer> ions){
		this.ions = ImmutableMap.copyOf(ions);
	}

	public IonProviderImpl(){
		this(Collections.EMPTY_MAP);
	}

	@Override
	public ImmutableMap<Ion, Integer> getIons(){
		return ions;
	}
}
