package appeng.core.core.tile;

import appeng.core.core.AppEngCore;
import appeng.core.core.api.tick.IHasChildrenTickables;
import appeng.core.core.crafting.ion.CraftingIonRegistry;
import appeng.core.core.crafting.ion.IonEnvironment;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.Fluid;

import javax.annotation.Nullable;

public class IonEnvironmentTile extends TileEntity implements ITickable, IHasChildrenTickables {

	protected IonEnvironment environment = new IonEnvironment();

	public IonEnvironmentTile(){
	}

	public IonEnvironmentTile(Fluid fluid){
		this.environment.setEnvironment(fluid);
	}

	@Override
	public void update(){
		if(hasCapability(AppEngCore.tickablesCapability, null)) getCapability(AppEngCore.tickablesCapability, null).tick(this);
	}

	@Override
	public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing){
		return capability == CraftingIonRegistry.ionEnvironmentCapability || super.hasCapability(capability, facing);
	}

	@Nullable
	@Override
	public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing){
		return capability == CraftingIonRegistry.ionEnvironmentCapability ? (T) environment : super.getCapability(capability, facing);
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

	@Override
	public NBTTagCompound getUpdateTag(){
		return serializeNBT();
	}

	@Nullable
	@Override
	public SPacketUpdateTileEntity getUpdatePacket(){
		return new SPacketUpdateTileEntity(pos, 0, getUpdateTag());
	}

	@Override
	public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt){
		deserializeNBT(pkt.getNbtCompound());
	}

}