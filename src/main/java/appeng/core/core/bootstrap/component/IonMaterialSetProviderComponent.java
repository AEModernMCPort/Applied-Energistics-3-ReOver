package appeng.core.core.bootstrap.component;

import appeng.api.bootstrap.IDefinitionBuilder;
import appeng.core.core.AppEngCore;
import appeng.core.core.api.crafting.ion.Ion;
import appeng.core.core.api.definition.IMaterialDefinition;
import appeng.core.core.crafting.ion.IonProviderImpl;
import appeng.core.core.material.IonMaterial;
import com.google.common.collect.ImmutableMap;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.Fluid;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public class IonMaterialSetProviderComponent<M extends IonMaterial> implements IDefinitionBuilder.DefinitionInitializationComponent.Init<M, IMaterialDefinition<M>> {

	private ImmutableMap<ResourceLocation, Integer> ions;
	private boolean def;
	private Set<Fluid> fluids;

	public IonMaterialSetProviderComponent(ImmutableMap<ResourceLocation, Integer> ions){
		this.ions = ions;
	}

	public IonMaterialSetProviderComponent(ImmutableMap<ResourceLocation, Integer> ions, boolean def, Set<Fluid> fluids){
		this.ions = ions;
		this.def = def;
		this.fluids = fluids;
	}

	@Override
	public void init(IMaterialDefinition<M> def){
		Map<Ion, Integer> ions = new HashMap<>();
		this.ions.forEach((rion, amount) -> Optional.ofNullable(AppEngCore.INSTANCE.getIonRegistry().getValue(rion)).ifPresent(ion -> ions.put(ion, amount)));
		def.maybe().ifPresent(mion -> mion.setIonProvider(fluids != null ? new IonProviderImpl.Reactive(ions, this.def, fluids) : new IonProviderImpl(ions)));
	}
}
