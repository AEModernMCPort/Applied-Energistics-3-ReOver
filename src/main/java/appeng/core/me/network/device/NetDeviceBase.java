package appeng.core.me.network.device;

import appeng.core.me.AppEngME;
import appeng.core.me.api.network.DeviceUUID;
import appeng.core.me.api.network.NetBlock;
import appeng.core.me.api.network.NetDevice;
import appeng.core.me.api.network.PhysicalDevice;
import appeng.core.me.api.network.block.Connection;
import appeng.core.me.api.network.device.DeviceRegistryEntry;
import appeng.core.me.api.network.event.EventBusInitializeEvent;
import appeng.core.me.api.network.event.NCEventBus;
import appeng.core.me.network.connect.ConnectionsParams;
import appeng.core.me.network.event.EventBusImpl;
import code.elix_x.excomms.optional.NullableOptional;
import com.google.common.collect.ImmutableList;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityDispatcher;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.ref.WeakReference;
import java.util.Collection;
import java.util.Comparator;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class NetDeviceBase<N extends NetDeviceBase<N, P>, P extends PhysicalDevice<N, P>> implements NetDevice<N, P> {

	public NetDeviceBase(@Nonnull DeviceRegistryEntry<N, P> registryEntry, @Nonnull DeviceUUID uuid, @Nullable NetBlock netBlock){
		this.registryEntry = registryEntry;
		this.uuid = uuid;
		this.netBlock = netBlock;
		this.params = AppEngME.INSTANCE.getDevicesHelper().gatherConnectionsParams(this);
		initCapabilities();
		initEvents();
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

	protected boolean satisfied = false;

	@Override
	public boolean fulfill(Collection<Connection> connectionsFulfilled){
		return satisfied = connectionsFulfilled.containsAll(params.getAllConnections());
	}

	@Override
	public boolean satisfied(){
		return satisfied;
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
	 * Events
	 */

	protected NCEventBus<N, NetDeviceEvent<N, P>> eventBus;

	protected void initEvents(){
		EventBusInitializeEvent<N, NetDeviceEvent<N, P>> event = new EventBusInitializeEvent<>((N) this, NetDevice.class);
		MinecraftForge.EVENT_BUS.post(event);
		this.eventBus = new EventBusImpl<>(event);
	}

	@Nonnull
	@Override
	public NCEventBus<N, NetDeviceEvent<N, P>> getEventBus(){
		return eventBus;
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
		nbt.setBoolean("satisfied", satisfied);
		if(capabilities != null) nbt.setTag("capabilities", capabilities.serializeNBT());
		return nbt;
	}

	protected void deserializeNBT(NBTTagCompound nbt){
		satisfied = nbt.getBoolean("satisfied");
		if(capabilities != null && nbt.hasKey("capabilities")) capabilities.deserializeNBT(nbt.getCompoundTag("capabilities"));
	}

	public static abstract class BehaviorDriven<N extends BehaviorDriven<N, P, B>, P extends PhysicalDevice<N, P>, B> extends NetDeviceBase<N, P> implements appeng.core.me.api.network.device.behavior.BehaviorDriven<B> {

		public BehaviorDriven(@Nonnull DeviceRegistryEntry<N, P> registryEntry, @Nonnull DeviceUUID uuid, @Nullable NetBlock netBlock){
			super(registryEntry, uuid, netBlock);
			initBehaviors();
		}

		protected ImmutableList<B> behaviors;

		protected void initBehaviors(){
			AttachDeviceBehaviorEvent<N> event = new AttachDeviceBehaviorEvent<>((N) this);
			MinecraftForge.EVENT_BUS.post(event);
			behaviors = ImmutableList.copyOf(Stream.concat(defaultBehavior().map(b -> Stream.of(new ImmutablePair<>(b, 0d))).orElse(Stream.empty()), event.<B>getBehaviors().stream()).sorted(Comparator.<Pair<B, Double>>comparingDouble(Pair::getRight).reversed()).map(Pair::getLeft).collect(Collectors.toList()));
		}

		protected Optional<B> defaultBehavior(){
			return Optional.empty();
		}

		public Optional<BehaviorOperationResult> behaviorDrivenAction(Function<B, BehaviorOperationResult> action){
			return behaviors.stream().map(action).filter(BehaviorOperationResult::stopIt).findFirst();
		}

		public <R> NullableOptional<R> behaviorDrivenGetAction(Function<B, NullableOptional<R>> action){
			return behaviors.stream().map(action).filter(NullableOptional::isPresent).findFirst().orElse(NullableOptional.empty());
		}

	}

}
