package appeng.core.me.network;

import appeng.core.me.AppEngME;
import appeng.core.me.api.network.*;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.server.MinecraftServer;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nonnull;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class GlobalNBDManagerImpl implements GlobalNBDManager {

	private static GlobalNBDManagerImpl INSTANCE;

	public static GlobalNBDManagerImpl getInstance(){
		return INSTANCE;
	}

	/*
	 * IO
	 */
	//TODO Save on game paused
	//TODO Backups

	protected static File getDataFile(MinecraftServer server){
		return server.getFile("ae3 networks.dat");
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
	protected boolean startNetworksImmediately = false;

	@Nonnull
	@Override
	public Optional<Network> getNetwork(@Nonnull NetworkUUID uuid){
		return Optional.ofNullable(networks.get(uuid));
	}

	@Override
	public <N extends Network> N networkCreated(N network){
		networks.put(network.getUUID(), network);
		if(startNetworksImmediately) network.startThreads();
		return network;
	}

	@Override
	public Network createDefaultNetwork(NetworkUUID uuid){
		return networkCreated(new NetworkImpl(uuid));
	}

	@Override
	public void networkDestroyed(NetworkUUID uuid){

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
	 * Life cycle sequences
	 */

	void startNetworks(){
		startNetworksImmediately = true;
		networks.values().forEach(Network::startThreads);
	}

	Runnable suspendNetworks(){
		startNetworksImmediately = false;
		List<Runnable> resume = networks.values().stream().map(Network::suspendThreads).collect(Collectors.toList());
		return () -> {
			startNetworksImmediately = true;
			resume.forEach(Runnable::run);
		};
	}

	void load(NBTTagCompound nbt){
		if(nbt != null) deserializeNBT(nbt);
		startNetworks();
	}

	void pause(Consumer<NBTTagCompound> nbt){
		Runnable resume = suspendNetworks();
		nbt.accept(serializeNBT());
		resume.run();
	}

	NBTTagCompound shutdown(){
		suspendNetworks();
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
		this.bfDevices.values().stream().map(AppEngME.INSTANCE.getNBDIO()::serializeDeviceWithArgs).forEach(tag -> devices.appendTag((NBTTagCompound) tag));
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
