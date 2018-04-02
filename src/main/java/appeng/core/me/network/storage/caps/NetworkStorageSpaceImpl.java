package appeng.core.me.network.storage.caps;

import appeng.core.me.api.network.storage.caps.NetworkStorageSpace;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.INBTSerializable;
import org.apache.commons.lang3.mutable.MutableInt;

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
	public int occupy(ResourceLocation storage, int min, int max, int unit){
		if(unit <= 0) return 0;
		int minsu = min * unit;
		int maxsu = max * unit;

		AtomicInteger total = this.partitions.get(storage);
		int safeTotal = total.get() - (int) (SAFETYFREEMARGIN * total.get());

		MutableInt ores = new MutableInt();
		occupied.get(storage).updateAndGet(current -> {
			ores.setValue(0);
			int mo = safeTotal - current;
			if(mo < minsu) return current;
			else if(mo > maxsu){
				ores.setValue(max);
				return current + maxsu;
			} else {
				int o = Math.floorDiv(mo, unit);
				ores.setValue(o);
				return current + o * unit;
			}
		});
		return ores.intValue();
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
