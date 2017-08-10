package appeng.core.core.crafting.ion;

import appeng.core.core.api.crafting.ion.Ion;
import appeng.core.core.api.crafting.ion.IonProvider;
import com.google.common.collect.ImmutableMap;
import net.minecraftforge.fluids.Fluid;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

public class IonProviderImpl implements IonProvider {

	protected final ImmutableMap<Ion, Integer> ions;

	public IonProviderImpl(Map<Ion, Integer> ions){
		this.ions = ImmutableMap.copyOf(ions);
	}

	public IonProviderImpl(Ion ion, int amount){
		this.ions = ImmutableMap.of(ion, amount);
	}

	public IonProviderImpl(){
		this(Collections.EMPTY_MAP);
	}

	public IonProviderImpl(Collection<Pair<Ion, Integer>> ions){
		this.ions = ImmutableMap.copyOf(ions);
	}

	@Override
	public ImmutableMap<Ion, Integer> getIons(){
		return ions;
	}

	public static class Reactive extends IonProviderImpl {

		public boolean def;
		public Set<Fluid> fluids;

		public Reactive(Map<Ion, Integer> ions, boolean def, Set<Fluid> fluids){
			super(ions);
			this.def = def;
			this.fluids = fluids;
		}

		public Reactive(Ion ion, int amount, boolean def, Set<Fluid> fluids){
			super(ion, amount);
			this.def = def;
			this.fluids = fluids;
		}

		@Override
		public boolean isReactive(Fluid fluid){
			return def != fluids.contains(fluid);
		}

	}

}
