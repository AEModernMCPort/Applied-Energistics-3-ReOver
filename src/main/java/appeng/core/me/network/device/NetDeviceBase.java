package appeng.core.me.network.device;

import appeng.core.me.AppEngME;
import appeng.core.me.api.network.DeviceUUID;
import appeng.core.me.api.network.NetBlock;
import appeng.core.me.api.network.NetDevice;
import appeng.core.me.api.network.PhysicalDevice;
import appeng.core.me.api.network.block.Connection;
import appeng.core.me.api.network.device.DeviceRegistryEntry;
import appeng.core.me.api.network.event.NCEventBus;
import appeng.core.me.network.connect.ConnectionsParams;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityDispatcher;
import net.minecraftforge.event.AttachCapabilitiesEvent;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.ref.WeakReference;
import java.util.Collection;
import java.util.Optional;

public class NetDeviceBase<N extends NetDeviceBase<N, P>, P extends PhysicalDevice<N, P>> implements NetDevice<N, P> {

	public NetDeviceBase(@Nonnull DeviceRegistryEntry<N, P> registryEntry, @Nonnull DeviceUUID uuid, @Nullable NetBlock netBlock){
		this.registryEntry = registryEntry;
		this.uuid = uuid;
		this.netBlock = netBlock;
		this.params = AppEngME.INSTANCE.getDevicesHelper().gatherConnectionsParams(this);

		initCapabilities();
	}
	/*
	 * UUID
	 */

	protected final DeviceUUID uuid;

	@Nonnull
	@Override
	public DeviceUUID getUUID(){
		return uuid;
	}

	/*
	 * Init
	 */

	public void init(P p){
		assignPhysicalCounterpart(p);
		AppEngME.INSTANCE.getGlobalNBDManager().<NetDevice, PhysicalDevice>registerFreeDevice(this);
	}

	public void destroy(){
		if(netBlock != null) netBlock.<NetDevice, PhysicalDevice>removeDestroyedDevice(this);
		else AppEngME.INSTANCE.getGlobalNBDManager().<NetDevice, PhysicalDevice>removeFreeDevice(this);
		netBlock = null;
	}

	/*
	 * P
	 */

	protected WeakReference<P> physicalCounterpart;

	@Override
	public Optional<P> getPhysicalCounterpart(){
		return Optional.ofNullable(physicalCounterpart.get());
	}

	public void assignPhysicalCounterpart(P p){
		this.physicalCounterpart = new WeakReference<>(p);
	}

	/*
	 * Connection
	 */

	private ConnectionsParams<?> params;

	@Override
	public <Param extends Comparable<Param>> Param getConnectionRequirement(Connection<Param, ?> connection){
		return params.getParam(connection);
	}

	@Override
	public boolean fulfill(Collection<Connection> connectionsFulfilled){
		return params.getAllConnections().containsAll(connectionsFulfilled);
	}

	/*
	 * Network blocks
	 */

	protected NetBlock netBlock;

	@Nonnull
	@Override
	public Optional<NetBlock> getNetBlock(){
		return Optional.ofNullable(netBlock);
	}

	@Override
	public void switchNetBlock(@Nullable NetBlock block){
		if(this.netBlock != null && block == null) AppEngME.INSTANCE.getGlobalNBDManager().<NetDevice, PhysicalDevice>registerFreeDevice(this);
		if(this.netBlock == null && block != null) AppEngME.INSTANCE.getGlobalNBDManager().<NetDevice, PhysicalDevice>removeFreeDevice(this);
		this.netBlock = block;
	}

	/*
	 * Events
	 */

	@Nonnull
	@Override
	public NCEventBus<N, NetDeviceEvent<N, P>> getEventBus(){
		//TODO Events
		return null;
	}

	/*
	 * Capabilities
	 */

	protected CapabilityDispatcher capabilities;

	protected void initCapabilities(){
		AttachCapabilitiesEvent<NetDevice> event = new AttachCapabilitiesEvent<>(NetDevice.class, this);
		MinecraftForge.EVENT_BUS.post(event);
		this.capabilities = new CapabilityDispatcher(event.getCapabilities());
	}

	@Override
	public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing){
		return capabilities != null && capabilities.hasCapability(capability, facing);
	}

	@Nullable
	@Override
	public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing){
		return capabilities == null ? null : capabilities.getCapability(capability, facing);
	}

	/*
	 * IO
	 */

	protected final DeviceRegistryEntry<N, P> registryEntry;

	@Override
	public DeviceRegistryEntry<N, P> getRegistryEntry(){
		return registryEntry;
	}

	@Override
	public NBTTagCompound serializeNBT(){
		NBTTagCompound nbt = new NBTTagCompound();
		if(capabilities != null) nbt.setTag("capabilities", capabilities.serializeNBT());
		return nbt;
	}

	protected void deserializeNBT(NBTTagCompound nbt){
		if(capabilities != null && nbt.hasKey("capabilities")) capabilities.deserializeNBT(nbt.getCompoundTag("capabilities"));
	}

}
