package appeng.core.core.tile;

import appeng.core.core.AppEngCore;
import appeng.core.core.crafting.ion.IonEnvironment;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.Fluid;

import javax.annotation.Nullable;

public class IonEnvironmentTile extends TileEntity {

	protected IonEnvironment environment = new IonEnvironment();

	public IonEnvironmentTile(){
	}

	public IonEnvironmentTile(Fluid fluid){
		this.environment.setEnvironment(fluid);
	}

	@Override
	public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing){
		return capability == AppEngCore.ionEnvironmentCapability || super.hasCapability(capability, facing);
	}

	@Nullable
	@Override
	public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing){
		return capability == AppEngCore.ionEnvironmentCapability ? (T) environment : super.getCapability(capability, facing);
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
