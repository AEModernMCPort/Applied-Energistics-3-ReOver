package appeng.core.core.crafting.ion;

import appeng.core.core.AppEngCore;
import appeng.core.core.api.crafting.ion.Ion;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class IonEnvironment implements appeng.core.core.api.crafting.ion.IonEnvironment{

	protected Fluid environment;
	protected Map<Ion, Integer> ions = new HashMap<>();

	public IonEnvironment(@Nonnull Fluid environment){
		this.environment = environment;
	}

	public IonEnvironment(){
		this(FluidRegistry.WATER);
	}

	@Override
	@Nonnull
	public Fluid getEnvironment(){
		return environment;
	}

	public void setEnvironment(Fluid environment){
		this.environment = environment;
	}

	@Override
	public Map<Ion, Integer> getIons(){
		return Collections.unmodifiableMap(ions);
	}

	@Override
	public int getAmount(Ion ion){
		Integer amount = ions.get(ion);
		return amount != null ? amount : 0;
	}

	@Override
	public void addIons(Ion ion, int amount){
		int current = getAmount(ion);
		ions.put(ion, Math.max(current + amount, 0));
	}

	@Override
	public NBTTagCompound serializeNBT(){
		NBTTagCompound nbt = new NBTTagCompound();
		nbt.setString("environment", this.environment.getName());
		NBTTagList ions = new NBTTagList();
		this.ions.forEach((ion, amount) -> {
			NBTTagCompound tag = new NBTTagCompound();
			tag.setString("ion", ion.getRegistryName().toString());
			tag.setInteger("amount", amount);
			ions.appendTag(tag);
		});
		nbt.setTag("ions", ions);
		return nbt;
	}

	@Override
	public void deserializeNBT(NBTTagCompound nbt){
		this.environment = FluidRegistry.getFluid(nbt.getString("environment"));
		this.ions.clear();
		nbt.getTagList("ions", 10).forEach(tag -> this.ions.put(AppEngCore.INSTANCE.getIonRegistry().getValue(new ResourceLocation(((NBTTagCompound) tag).getString("ion"))), ((NBTTagCompound) tag).getInteger("amount")));
	}
}
