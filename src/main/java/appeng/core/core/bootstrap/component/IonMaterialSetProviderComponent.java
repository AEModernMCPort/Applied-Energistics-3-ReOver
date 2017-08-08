package appeng.core.core.bootstrap.component;

import appeng.api.bootstrap.IDefinitionBuilder;
import appeng.core.core.AppEngCore;
import appeng.core.core.api.crafting.ion.Ion;
import appeng.core.core.api.definition.IMaterialDefinition;
import appeng.core.core.crafting.ion.IonProviderImpl;
import appeng.core.core.material.IonMaterial;
import com.google.common.collect.ImmutableMap;
import net.minecraft.util.ResourceLocation;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class IonMaterialSetProviderComponent<M extends IonMaterial> implements IDefinitionBuilder.DefinitionInitializationComponent.Init<M, IMaterialDefinition<M>> {

	private ImmutableMap<ResourceLocation, Integer> ions;

	public IonMaterialSetProviderComponent(ImmutableMap<ResourceLocation, Integer> ions){
		this.ions = ions;
	}

	@Override
	public void init(IMaterialDefinition<M> def){
		Map<Ion, Integer> ions = new HashMap<>();
		this.ions.forEach((rion, amount) -> Optional.ofNullable(AppEngCore.INSTANCE.getIonRegistry().getValue(rion)).ifPresent(ion -> ions.put(ion, amount)));
		def.maybe().ifPresent(mion -> mion.setIonProvider(new IonProviderImpl(ions)));
	}
}
