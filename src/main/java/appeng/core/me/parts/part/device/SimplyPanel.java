package appeng.core.me.parts.part.device;

import appeng.core.me.api.network.DeviceUUID;
import appeng.core.me.api.network.NetBlock;
import appeng.core.me.api.network.block.Connection;
import appeng.core.me.api.network.device.DeviceRegistryEntry;
import appeng.core.me.api.network.storage.caps.ItemNetworkStorage;
import appeng.core.me.api.parts.container.PartsAccess;
import appeng.core.me.network.storage.caps.NetworkStorageCaps;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;

public interface SimplyPanel {

	class Network extends PartDeviceD2N.Network<Part, Physical, Network> {

		public Network(@Nonnull DeviceRegistryEntry<Network, Physical> registryEntry, @Nonnull DeviceUUID uuid, @Nullable NetBlock netBlock){
			super(registryEntry, uuid, netBlock);
		}

		ItemNetworkStorage.Entry selected;

		int getStored(){
			return selected != null ? getNetBlock().flatMap(NetBlock::getNetwork).map(network -> network.getCapability(NetworkStorageCaps.item, null).getStoredAmount(selected)).orElse(0) : 0;
		}

		int extractSelected(){
			return selected != null ? getNetBlock().flatMap(NetBlock::getNetwork).map(network -> -network.getCapability(NetworkStorageCaps.item, null).store(selected, -1, -3)).orElse(0) : 0;
		}

		@Override
		public NBTTagCompound serializeNBT(){
			NBTTagCompound nbt = super.serializeNBT();
			if(selected != null) nbt.setTag("selected", selected.serializeNBT());
			return nbt;
		}

		@Override
		protected void deserializeNBT(NBTTagCompound nbt){
			super.deserializeNBT(nbt);
			if(nbt.hasKey("selected")) selected = ItemNetworkStorage.Entry.deserializeNBT(nbt.getCompoundTag("selected"));
		}

	}

	class Part extends PartDevice<Part, Physical, Network> {

		public Part(){
			super(true);
		}

		@Override
		public Physical createNewState(){
			return new Physical(this);
		}

		@Override
		public EnumActionResult onRightClick(@Nullable Physical part, @Nonnull PartsAccess.Mutable world, @Nonnull World theWorld, @Nonnull EntityPlayer player, @Nonnull EnumHand hand){
			if(!theWorld.isRemote){
				Network net = part.networkCounterpart;
				ItemNetworkStorage.Entry held = ItemNetworkStorage.Entry.ofItemStack(player.getHeldItem(hand));
				if(!net.hasNetwork());
				else if(!net.satisfied()) player.sendMessage(new TextComponentString(";("));
				else if(held == null && net.selected != null){
					ItemStack res = net.selected.asStack(net.extractSelected());
					if(!player.addItemStackToInventory(res)) player.dropItem(res, false);
				} else {
					if(held != null) net.selected = held;
					if(net.selected != null) player.sendMessage(new TextComponentString(String.format("%s of %s stored", net.getStored(), net.selected.asStack(1).getDisplayName())));
				}
			}
			return EnumActionResult.SUCCESS;
		}

	}

	class Physical extends PartDeviceD2N.Physical<Part, Physical, Network> {

		public Physical(Part part){
			super(part);
		}

		@Override
		protected Network createNewNetworkCounterpart(){
			return new Network(getReg(), new DeviceUUID(), null);
		}

	}

}
