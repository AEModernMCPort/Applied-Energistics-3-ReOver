package appeng.core.me.api.network.storage.caps;

import appeng.core.me.api.network.storage.atomic.SubtypedAtomicNetworkStorage;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Objects;

//TODO Unimplemented till 1.13 - we need full state properties coverage
@Deprecated
public interface BlockNetworkStorage extends SubtypedAtomicNetworkStorage<BlockNetworkStorage.Entry, Block> {

	final class Entry {

		public final IBlockState block;
		public final NBTTagCompound tile;

		public Entry(@Nonnull IBlockState block, @Nullable NBTTagCompound tile){
			this.block = block;
			this.tile = tile;
		}

		public IBlockState getBlock(){
			return block;
		}

		public NBTTagCompound getTile(){
			return tile;
		}

		public NBTTagCompound serializeNBT(){
			NBTTagCompound nbt = new NBTTagCompound();
			nbt.setTag("block", NBTUtil.writeBlockState(new NBTTagCompound(), block));
			if(tile != null) nbt.setTag("tile", tile);
			return nbt;
		}

		public static BlockNetworkStorage.Entry deserializeNBT(NBTTagCompound nbt){
			return new Entry(NBTUtil.readBlockState(nbt.getCompoundTag("block")), nbt.hasKey("tile") ? nbt.getCompoundTag("tile") : null);
		}

		/*
		 * TODO 1.13 See how equals & hash code evolves & implement correctly
		 */

		@Override
		public boolean equals(Object o){
			if(this == o) return true;
			if(o == null || getClass() != o.getClass()) return false;
			Entry entry = (Entry) o;
			return Objects.equals(block, entry.block) && Objects.equals(tile, entry.tile);
		}

		@Override
		public int hashCode(){
			return Objects.hash(block, tile);
		}

		@Override
		public String toString(){
			return block + "{, tile=" + tile + '}';
		}

	}

}
