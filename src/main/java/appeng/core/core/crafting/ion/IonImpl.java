package appeng.core.core.crafting.ion;

import appeng.core.core.api.crafting.ion.Ion;
import code.elix_x.excomms.color.RGBA;
import net.minecraftforge.registries.IForgeRegistryEntry;

public class IonImpl extends IForgeRegistryEntry.Impl<Ion> implements Ion {

	protected RGBA colorModifier;

	public IonImpl(){
	}

	public IonImpl(RGBA colorModifier){
		this.colorModifier = colorModifier;
	}

	@Override
	public RGBA getColorModifier(){
		return colorModifier;
	}

}
