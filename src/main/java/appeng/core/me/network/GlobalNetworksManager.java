package appeng.core.me.network;

import appeng.core.AppEng;
import appeng.core.me.api.network.Network;
import appeng.core.me.api.network.NetworkUUID;
import appeng.core.me.api.network.W2NInterface;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class GlobalNetworksManager implements W2NInterface {

	public static final ResourceLocation DEFAULTLOADER = new ResourceLocation(AppEng.MODID, "default");
	private static Map<ResourceLocation, BiFunction<NetworkUUID, NBTTagCompound, ? extends Network>> loaders = new HashMap<>();

	public static void registerNetworkLoader(ResourceLocation id, BiFunction<NetworkUUID, NBTTagCompound, ? extends Network> loader){
		loaders.put(id, loader);
	}

	public static BiFunction<NetworkUUID, NBTTagCompound, ? extends Network> getNetworkLoader(ResourceLocation id){
		return loaders.get(id != null && loaders.containsKey(id) ? id : DEFAULTLOADER);
	}

	private static GlobalNetworksManager INSTANCE;

	public static GlobalNetworksManager getInstance(){
		return INSTANCE;
	}

	/*
	 * IO
	 */
	//TODO 1.12.2-dhcp - Save on game paused
	//TODO 1.12.2-dhcp - Backups

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
		INSTANCE = new GlobalNetworksManager();
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

	private Map<NetworkUUID, Network> networks = new HashMap<>();
	private boolean startNetworksImmediately = false;

	public GlobalNetworksManager(){
	}

	/*
	 * Impl
	 */

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
		this.networks.entrySet().forEach(e -> {
			NBTTagCompound next = new NBTTagCompound();
			next.setTag("uuid", e.getKey().serializeNBT());
			NBTTagCompound netTag = e.getValue().serializeNBT();
			if(netTag.hasKey("loader")) next.setTag("loader", netTag.getTag("loader"));
			next.setTag("network", netTag);
			networks.appendTag(next);
		});
		nbt.setTag("networks", networks);
		return nbt;
	}

	GlobalNetworksManager deserializeNBT(NBTTagCompound nbt){
		this.networks.clear();
		NBTTagList networks = (NBTTagList) nbt.getTag("networks");
		networks.forEach(nbtBase -> {
			NBTTagCompound next = (NBTTagCompound) nbtBase;
			NetworkUUID uuid = NetworkUUID.fromNBT(next.getCompoundTag("uuid"));
			this.networks.put(uuid, getNetworkLoader(next.hasKey("loader") ? new ResourceLocation(next.getString("loader")) : null).apply(uuid, next.getCompoundTag("network")));
		});
		return this;
	}

}
