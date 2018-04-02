package appeng.core.me.network.storage.caps;

import appeng.core.me.api.network.storage.caps.NetworkStorageSpace;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.INBTSerializable;
import org.apache.commons.lang3.mutable.MutableBoolean;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class NetworkStorageSpaceImpl implements NetworkStorageSpace, INBTSerializable<NBTTagCompound> {

	public static final double SAFETYFREEMARGIN = 0.05;

	protected AtomicInteger totalSpace = new AtomicInteger();
	protected ConcurrentHashMap<ResourceLocation, AtomicInteger> partitions = new ConcurrentHashMap<>();

	protected ConcurrentHashMap<ResourceLocation, AtomicInteger> occupied = new ConcurrentHashMap<>();

	public NetworkStorageSpaceImpl(){}

	@Override
	public int getAllocated(ResourceLocation storage){
		return partitions.get(storage).get();
	}

	@Override
	public int getFree(ResourceLocation storage){
		AtomicInteger total = this.partitions.get(storage);
		AtomicInteger occupied = this.occupied.get(storage);
		return Math.max(total.get() - (int) (SAFETYFREEMARGIN * total.get()) - occupied.get(), 0);
	}

	@Override
	public boolean occupy(ResourceLocation storage, int su){
		AtomicInteger total = this.partitions.get(storage);
		int safeTotal = total.get() - (int) (SAFETYFREEMARGIN * total.get());

		MutableBoolean res = new MutableBoolean();
		occupied.get(storage).updateAndGet(current -> {
			res.setFalse();
			int n = current + su;
			if(n <= safeTotal){
				res.setTrue();
				return n;
			} else return current;
		});
		return res.booleanValue();
	}

	@Override
	public void free(ResourceLocation storage, int su){
		occupied.get(storage).updateAndGet(current -> Math.max(current - su, 0));
	}

	/*
	 * IO
	 */

	@Override
	public NBTTagCompound serializeNBT(){
		NBTTagCompound nbt = new NBTTagCompound();
		nbt.setInteger("total", totalSpace.get());

		NBTTagList partitions = new NBTTagList();
		this.partitions.forEach((storage, alloc) -> {
			NBTTagCompound tag = new NBTTagCompound();
			tag.setString("storage", storage.toString());
			tag.setInteger("alloc", alloc.get());
			partitions.appendTag(tag);
		});
		nbt.setTag("partitions", partitions);

		NBTTagList occupied = new NBTTagList();
		this.occupied.forEach((storage, alloc) -> {
			NBTTagCompound tag = new NBTTagCompound();
			tag.setString("storage", storage.toString());
			tag.setInteger("occupied", alloc.get());
			occupied.appendTag(tag);
		});
		nbt.setTag("occupied", occupied);

		return nbt;
	}

	@Override
	public void deserializeNBT(NBTTagCompound nbt){
		totalSpace.set(nbt.getInteger("total"));
		partitions.clear();
		((Iterable<NBTTagCompound>) nbt.getTag("partitions")).forEach(tag -> partitions.put(new ResourceLocation(tag.getString("storage")), new AtomicInteger(tag.getInteger("alloc"))));
		occupied.clear();
		((Iterable<NBTTagCompound>) nbt.getTag("occupied")).forEach(tag -> occupied.put(new ResourceLocation(tag.getString("storage")), new AtomicInteger(tag.getInteger("occupied"))));
	}

}
