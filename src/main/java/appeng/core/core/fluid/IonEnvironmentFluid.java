package appeng.core.core.fluid;

import net.minecraftforge.fluids.Fluid;

public class IonEnvironmentFluid extends Fluid {

	public IonEnvironmentFluid(Fluid proxy){
		super("ae3_ion_" + proxy.getName(), proxy.getStill(), proxy.getFlowing());
		setUnlocalizedName(proxy.getUnlocalizedName());
		setLuminosity(proxy.getLuminosity());
		setDensity(proxy.getDensity());
		setViscosity(proxy.getViscosity());
		setTemperature(proxy.getTemperature());
		setGaseous(proxy.isGaseous());
		setRarity(proxy.getRarity());
		setFillSound(proxy.getFillSound());
		setEmptySound(proxy.getEmptySound());
	}
}
