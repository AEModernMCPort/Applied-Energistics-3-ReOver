package appeng.core.me.tile;

import appeng.core.me.parts.container.PartsContainer;
import appeng.core.me.parts.part.PartsHelperImpl;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;

import javax.annotation.Nullable;

public class PartsContainerTile extends TileEntity {

	private PartsContainer container = new PartsContainer(this::markDirty);

	public PartsContainerTile(){

	}

	@Override
	public void setWorld(World worldIn){
		super.setWorld(worldIn);
		container.setGlobalAccess(world.getCapability(PartsHelperImpl.worldPartsAccessCapability, null), world);
	}

	@Override
	public void setPos(BlockPos posIn){
		super.setPos(posIn);
		container.setGlobalPosition(pos);
	}

	/*
	 * Load-unload
	 */

	@Override
	public void onLoad(){
		container.onLoad();
	}

	//FIXME Not called when the world unloads (like exit world) - but container.onUnload must still be called.
	@Override
	public void onChunkUnload(){
		container.onUnload();
	}

	/*
	 * Caps
	 */

	@Override
	public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing){
		return capability == PartsHelperImpl.partsContainerCapability;
	}

	@Nullable
	@Override
	public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing){
		return capability == PartsHelperImpl.partsContainerCapability ? (T) container : null;
	}

	/*
	 * Dirty
	 */

	@Override
	public void markDirty(){
		super.markDirty();
	}

	/*
	 * IO
	 */

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

	@Override
	public void handleUpdateTag(NBTTagCompound tag){
		//This can be called when many blocks change, so might as well unload the container (to cleanup dynamic renderers) just in case
		container.onUnload();
		super.handleUpdateTag(tag);
		//this.onLoad, on client, is called before the update tag is handled :(
		container.onLoad();
	}

}
