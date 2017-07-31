package appeng.core.me.tile;

import appeng.core.me.parts.container.PartsContainer;
import appeng.core.me.parts.part.PartsHelper;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;

import javax.annotation.Nullable;

public class PartsContainerTile extends TileEntity {

	private PartsContainer container = new PartsContainer();

	public PartsContainerTile(){

	}

	@Override
	public void setWorld(World worldIn){
		super.setWorld(worldIn);
		container.setWorld(world);
	}

	@Override
	public void setPos(BlockPos posIn){
		super.setPos(posIn);
		container.setGlobalPosition(pos);
	}

	@Override
	public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing){
		return capability == PartsHelper.partsContainerCapability;
	}

	@Nullable
	@Override
	public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing){
		return capability == PartsHelper.partsContainerCapability ? (T) container : null;
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt){
		super.readFromNBT(nbt);
		container.deserializeNBT(nbt.getCompoundTag("parts"));
		setPos(pos);
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt){
		super.writeToNBT(nbt);
		nbt.setTag("parts", container.serializeNBT());
		return nbt;
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
