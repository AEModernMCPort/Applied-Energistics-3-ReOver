package appeng.core.core.tile;

import appeng.core.core.crafting.ion.IonEnvironment;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.fluids.Fluid;

public class IonEnvironmentTile extends TileEntity {

	protected IonEnvironment environment = new IonEnvironment();

	public IonEnvironmentTile(){
	}

	public IonEnvironmentTile(Fluid fluid){
		this.environment.setEnvironment(fluid);
	}

	@Override
	public void readFromNBT(NBTTagCompound compound){
		super.readFromNBT(compound);
		this.environment.deserializeNBT(compound.getCompoundTag("environment"));
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound){
		compound = super.writeToNBT(compound);
		compound.setTag("environment", this.environment.serializeNBT());
		return compound;
	}
}
