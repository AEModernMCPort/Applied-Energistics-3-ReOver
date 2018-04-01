package appeng.core.me.network;

import appeng.core.me.AppEngME;
import appeng.core.me.api.network.*;
import appeng.core.me.api.network.block.ConnectionPassthrough;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.FMLCommonHandler;
import org.apache.commons.lang3.mutable.MutableObject;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nonnull;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class GlobalNBDManagerImpl implements GlobalNBDManager {

	private static GlobalNBDManagerImpl INSTANCE;

	public static GlobalNBDManagerImpl getInstance(){
		return INSTANCE;
	}

	@Override
	public long currentServerTick(){
		return FMLCommonHandler.instance().getMinecraftServerInstance().getTickCounter();
	}

	/*
	 * IO
	 */
	//TODO Save on game paused
	//TODO Backups

	protected static File getDataFile(MinecraftServer server){
		return server.getActiveAnvilConverter().getFile(server.getFolderName(), "ae3 networks.dat");
	}

	public static void serverStarting(MinecraftServer server){
		NBTTagCompound nbt = null;
		File file = getDataFile(server);
		if(file.exists()) try{
			nbt = CompressedStreamTools.readCompressed(new FileInputStream(file));
		} catch(IOException e){
			//TODO Log?
		}
		INSTANCE = new GlobalNBDManagerImpl();
		INSTANCE.load(nbt);
	}

	public static void serverStopping(MinecraftServer server){
		NBTTagCompound nbt = INSTANCE.shutdown();
		File file = getDataFile(server);
		if(!file.exists()) try{
			file.createNewFile();
		} catch(IOException e){
			//TODO Log?
		}
		try{
			CompressedStreamTools.writeCompressed(nbt, new FileOutputStream(file));
		} catch(IOException e){
			//TODO Log?
		}
		INSTANCE = null;
	}

	/*
	 * Instance
	 */

	public GlobalNBDManagerImpl(){
	}

	/*
	 * Networks
	 */

	protected Map<NetworkUUID, Network> networks = new HashMap<>();
	protected Collection<Network> networksAwaitingStartup = new ArrayList<>();

	@Nonnull
	@Override
	public Optional<Network> getNetwork(@Nonnull NetworkUUID uuid){
		return Optional.ofNullable(networks.get(uuid));
	}

	@Override
	public <N extends Network> N networkCreated(N network){
		networks.put(network.getUUID(), network);
		if(networksAwaitingStartup != null) networksAwaitingStartup.add(network);
		else network.start();
		return network;
	}

	@Override
	public Network createDefaultNetwork(NetworkUUID uuid){
		return networkCreated(new NetworkImpl(uuid));
	}

	@Override
	public void networkDestroyed(NetworkUUID uuid){
		networks.remove(uuid);
	}

	//Threads

	protected GlobalTasksManager globalTasksManager = new GlobalTasksManager();

	@Override
	public TasksManager requestTasksManager(Network network){
		return globalTasksManager.requestTasksManager(network);
	}

	/*
	 * Network-Free blocks
	 */

	protected Map<NetBlockUUID, NetBlock> nfBlocks = new HashMap<>();

	@Override
	public Optional<NetBlock> getFreeBlock(NetBlockUUID uuid){
		return Optional.ofNullable(nfBlocks.get(uuid));
	}

	@Override
	public void registerFreeBlock(NetBlock block){
		nfBlocks.put(block.getUUID(), block);
	}

	@Override
	public void removeFreeBlock(NetBlock block){
		nfBlocks.remove(block.getUUID());
	}

	/*
	 * Block-Free devices
	 */

	protected Map<DeviceUUID, NetDevice> bfDevices = new HashMap<>();

	@Override
	public <N extends NetDevice<N, P>, P extends PhysicalDevice<N, P>> Optional<N> getFreeDevice(DeviceUUID uuid){
		return Optional.ofNullable((N) bfDevices.get(uuid));
	}

	@Override
	public <N extends NetDevice<N, P>, P extends PhysicalDevice<N, P>> void registerFreeDevice(N device){
		bfDevices.put(device.getUUID(), device);
	}

	@Override
	public <N extends NetDevice<N, P>, P extends PhysicalDevice<N, P>> void removeFreeDevice(N device){
		bfDevices.remove(device.getUUID());
	}

	/*
	 * Creation
	 */

	@Override
	public Optional<NetBlock> onDeviceCreatedTryToFindAdjacentNetBlock(@Nonnull World world, @Nonnull PhysicalDevice device){
		MutableObject<NetBlock> netBlock = new MutableObject<>();
		AppEngME.INSTANCE.getDevicesHelper().voxels(device).ifPresent(prCsVs -> {
			List<NetBlock> adjNB = Stream.concat(AppEngME.INSTANCE.getDevicesHelper().getAdjacentPTs(world, prCsVs.getRight()).keySet().stream().map(pt -> pt.getAssignedNetBlock().orElse(null)), AppEngME.INSTANCE.getDevicesHelper().getAdjacentDevices(world, prCsVs.getRight()).keySet().stream().map(d -> d.getNetworkCounterpart()).map(d -> ((Optional<NetBlock>) d.getNetBlock()).filter(nb -> nb.getRoot() == d).orElse(null))).filter(Objects::nonNull).distinct().collect(Collectors.toList());
			if(adjNB.size() == 1) netBlock.setValue(adjNB.get(0));
		});
		if(netBlock.getValue() != null) netBlock.getValue().<NetDevice, PhysicalDevice>deviceCreatedAdjacentToAssigned(world, device);
		return Optional.ofNullable(netBlock.getValue());
	}

	@Override
	public Optional<NetBlock> onPTCreatedTryToFindAdjacentNetBlock(@Nonnull World world, @Nonnull ConnectionPassthrough passthrough){
		MutableObject<NetBlock> netBlock = new MutableObject<>();
		AppEngME.INSTANCE.getDevicesHelper().voxels(passthrough).ifPresent(prCsVs -> {
			List<NetBlock> adjNB = Stream.concat(AppEngME.INSTANCE.getDevicesHelper().getAdjacentPTs(world, prCsVs.getRight()).keySet().stream().map(pt -> pt.getAssignedNetBlock().orElse(null)), AppEngME.INSTANCE.getDevicesHelper().getAdjacentDevices(world, prCsVs.getRight()).keySet().stream().map(device -> device.getNetworkCounterpart()).map(device -> ((Optional<NetBlock>) device.getNetBlock()).filter(nb -> nb.getRoot() == device).orElse(null))).filter(Objects::nonNull).distinct().collect(Collectors.toList());
			if(adjNB.size() == 1) netBlock.setValue(adjNB.get(0));
		});
		if(netBlock.getValue() != null) netBlock.getValue().passthroughCreatedAdjacentToAssigned(world, passthrough);
		return Optional.ofNullable(netBlock.getValue());
	}

	/*
	 * Life cycle sequences
	 */

	void startNetworks(){
		networks.values().forEach(Network::start);
	}

	Runnable suspendNetworks(){
		networksAwaitingStartup = new ArrayList<>();
		Runnable resumeTasks = globalTasksManager.suspend();
		return () -> {
			resumeTasks.run();
			networksAwaitingStartup.forEach(Network::start);
			networksAwaitingStartup = null;
		};
	}

	void load(NBTTagCompound nbt){
		if(nbt != null) deserializeNBT(nbt);
		networksAwaitingStartup = null;
		startNetworks();
	}

	void pause(Consumer<NBTTagCompound> nbt){
		Runnable resume = suspendNetworks();
		nbt.accept(serializeNBT());
		resume.run();
	}

	NBTTagCompound shutdown(){
		suspendNetworks();
		globalTasksManager.shutdown();
		return serializeNBT();
	}

	/*
	 * IO
	 */

	NBTTagCompound serializeNBT(){
		NBTTagCompound nbt = new NBTTagCompound();

		NBTTagList networks = new NBTTagList();
		this.networks.values().stream().map(AppEngME.INSTANCE.getNBDIO()::serializeNetworkWithArgs).forEach(networks::appendTag);
		nbt.setTag("networks", networks);

		NBTTagList blocks = new NBTTagList();
		this.nfBlocks.values().stream().map(AppEngME.INSTANCE.getNBDIO()::serializeNetBlockWithArgs).forEach(blocks::appendTag);
		nbt.setTag("blocks", blocks);

		NBTTagList devices = new NBTTagList();
		this.bfDevices.values().stream().map(AppEngME.INSTANCE.getNBDIO()::<NetDevice, PhysicalDevice>serializeDeviceWithArgs).forEach(tag -> devices.appendTag((NBTTagCompound) tag));
		nbt.setTag("devices", devices);

		return nbt;
	}

	GlobalNBDManagerImpl deserializeNBT(NBTTagCompound nbt){
		this.networks.clear();
		NBTTagList networks = (NBTTagList) nbt.getTag("networks");
		networks.forEach(next -> {
			Pair<NetworkUUID, Network> uuidNetwork = AppEngME.INSTANCE.getNBDIO().deserializeNetworkWithArgs((NBTTagCompound) next);
			this.networks.put(uuidNetwork.getLeft(), uuidNetwork.getRight());
		});

		this.nfBlocks.clear();
		NBTTagList blocks = (NBTTagList) nbt.getTag("blocks");
		blocks.forEach(next -> {
			Pair<NetBlockUUID, NetBlock> uuidBlock = AppEngME.INSTANCE.getNBDIO().deserializeNetBlockWithArgs(null, (NBTTagCompound) next);
			this.nfBlocks.put(uuidBlock.getLeft(), uuidBlock.getRight());
		});

		this.bfDevices.clear();
		NBTTagList devices = (NBTTagList) nbt.getTag("devices");
		devices.forEach(next -> {
			Pair<DeviceUUID, NetDevice> uuidDevice = (Pair) AppEngME.INSTANCE.getNBDIO().deserializeDeviceWithArgs(null, (NBTTagCompound) next);
			this.bfDevices.put(uuidDevice.getLeft(), uuidDevice.getRight());
		});

		return this;
	}

}
