package appeng.core.me.parts.part.device;

import appeng.core.me.AppEngME;
import appeng.core.me.api.network.DeviceUUID;
import appeng.core.me.api.network.NetBlockUUID;
import appeng.core.me.api.network.NetworkUUID;
import appeng.core.me.api.network.PhysicalDevice;
import appeng.core.me.api.parts.PartPositionRotation;
import appeng.core.me.api.parts.VoxelPosition;
import appeng.core.me.api.parts.container.PartsAccess;
import appeng.core.me.network.device.NetDeviceBase;
import appeng.core.me.parts.part.PartBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Optional;

public abstract class PartDevice<P extends PartDevice<P, S, N>, S extends PartDevice.PartDeviceState<P, S, N>, N extends NetDeviceBase<N, S>> extends PartBase<P, S> {

	public PartDevice(){
	}

	public PartDevice(boolean supportsRotation){
		super(supportsRotation);
	}

	@Override
	public void onLoad(@Nullable S part, @Nonnull PartsAccess.Mutable world, @Nullable World theWorld, @Nonnull PartPositionRotation positionRotation){
		part.networkCounterpart.assignPhysicalCounterpart(part);
		part.world = theWorld;
	}

	@Override
	public void onPlaced(@Nullable S part, @Nonnull PartsAccess.Mutable world, @Nullable World theWorld, @Nullable EntityPlayer placer, @Nullable EnumHand hand){
		part.init();
		part.world = theWorld;
		if(theWorld != null) AppEngME.INSTANCE.getGlobalNBDManager().onDeviceCreatedTryToFindAdjacentNetBlock(theWorld, part);
	}

	@Override
	public void onBroken(@Nullable S part, @Nonnull PartsAccess.Mutable world, @Nullable World theWorld, @Nullable EntityPlayer breaker){
		part.networkCounterpart.destroy();
	}

	protected abstract static class PartDeviceState<P extends PartDevice<P, S, N>, S extends PartDevice.PartDeviceState<P, S, N>, N extends NetDeviceBase<N, S>> extends StateBase<P, S> implements PhysicalDevice<N, S> {

		public PartDeviceState(P part){
			super(part);
		}

		/*
		 * Network counterpart
		 */

		protected N networkCounterpart;
		protected World world;

		@Override
		public N getNetworkCounterpart(){
			return networkCounterpart;
		}

		protected N init(){
			networkCounterpart = createNewNetworkCounterpart();
			networkCounterpart.init((S) this);
			return networkCounterpart;
		}

		protected abstract N createNewNetworkCounterpart();

		@Override
		public VoxelPosition getPosition(){
			return getAssignedPosRot().getPosition();
		}

		/*
		 * IO
		 */

		@Override
		public NBTTagCompound serializeNBT(){
			NBTTagCompound nbt = new NBTTagCompound();
			nbt.setTag("duuid", networkCounterpart.getUUID().serializeNBT());
			networkCounterpart.getNetBlock().ifPresent(netBlock -> {
				nbt.setTag("buuid", netBlock.getUUID().serializeNBT());
				netBlock.getNetwork().ifPresent(network -> nbt.setTag("nuuid", network.getUUID().serializeNBT()));
			});
			return nbt;
		}

		@Override
		public void deserializeNBT(NBTTagCompound nbt){
			DeviceUUID duuid = DeviceUUID.fromNBT(nbt.getCompoundTag("duuid"));
			Optional<NetBlockUUID> buuidO = Optional.ofNullable(nbt.hasKey("buuid") ? nbt.getCompoundTag("buuid") : null).map(NetBlockUUID::fromNBT);
			Optional<NetworkUUID> nuuidO = Optional.ofNullable(nbt.hasKey("nuuid") ? nbt.getCompoundTag("nuuid") : null).map(NetworkUUID::fromNBT);
			this.networkCounterpart = AppEngME.INSTANCE.getGlobalNBDManager().locateOrCreateNetworkCounterpart(Optional.of(duuid), buuidO, nuuidO, this::createNewNetworkCounterpart);
		}

		@Override
		public NBTTagCompound serializeSyncNBT(){
			return new NBTTagCompound();
		}

		@Override
		public void deserializeSyncNBT(NBTTagCompound nbt){

		}

	}

}
