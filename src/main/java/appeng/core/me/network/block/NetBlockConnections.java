package appeng.core.me.network.block;

import appeng.core.me.AppEngME;
import appeng.core.me.api.network.DeviceUUID;
import appeng.core.me.api.network.PhysicalDevice;
import appeng.core.me.api.network.block.ConnectUUID;
import appeng.core.me.api.network.block.ConnectionPassthrough;
import appeng.core.me.api.parts.PartPositionRotation;
import appeng.core.me.api.parts.VoxelPosition;
import appeng.core.me.api.parts.container.PartInfo;
import appeng.core.me.api.parts.part.Part;
import appeng.core.me.network.NetBlockImpl;
import appeng.core.me.parts.part.PartsHelper;
import com.google.common.collect.Multimap;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.common.util.INBTSerializable;
import org.apache.commons.lang3.mutable.MutableObject;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

public class NetBlockConnections implements INBTSerializable<NBTTagCompound> {

	protected NetBlockImpl netBlock;

	public NetBlockConnections(NetBlockImpl netBlock){
		this.netBlock = netBlock;
	}

	protected Map<DeviceUUID, Map<ResourceLocation, ?>> deviceConnectionParams = new HashMap<>();

	/*
	 * Path
	 */

	protected Map<ConnectUUID, Node> nodes = new HashMap<>();
	protected Set<Link> links = new HashSet<>();

	//FIXME TMP
	Map<ConnectUUID, ConnectionPassthrough> passthroughs = new HashMap<>();
	Map<ConnectUUID, PhysicalDevice> devices = new HashMap<>();

	public void recalculateAll(World world, PhysicalDevice root){
		long t = System.currentTimeMillis();
		deviceConnectionParams.clear();
		nodes.clear();
		links.clear();
		Function<ResourceLocation, Optional<Pair<PartPositionRotation, Multimap<VoxelPosition, EnumFacing>>>> c2c = connection2voxels(root);
		if(c2c == null) return;
		Set<DeviceUUID> directLinks = new HashSet<>();
		//Graph generation
		AppEngME.INSTANCE.getDevicesHelper().forEachConnection(connection -> c2c.apply(connection.getId()).ifPresent(voxels -> {
			ResourceLocation cid = connection.getId();
			getAdjacentPTs(world, voxels.getLeft(), voxels.getRight(), cid).forEach(passthrough -> exploreAdjacent(world, cid, passthrough, null));
			getAdjacentDevices(world, voxels.getLeft(), voxels.getRight(), cid).forEach(device -> {
				devices.put(device.getUUIDForConnection(), device);
				directLinks.add(device.getNetworkCounterpart().getUUID());
				//TODO Params.
			});
		}));
		exploreNodes();
		AppEngME.logger.info("Pathway calculation took " + (System.currentTimeMillis() - t) + "ms");
		AppEngME.logger.info(nodes.size() + " nodes");
		AppEngME.logger.info(links.size() + " links");
		AppEngME.logger.info(passthroughs.size() + " pts");
		AppEngME.logger.info(devices);
	}

	protected Queue<Runnable> nodesExplorer = new LinkedList<>();

	protected void exploreNodes(){
		while(nodesExplorer.peek() != null) nodesExplorer.poll().run();
	}

	protected ExplorationResult exploreAdjacent(World world, ResourceLocation connection, ConnectionPassthrough current, ConnectionPassthrough previous){
		final MutableObject<ExplorationResult> res = new MutableObject<>();
		final ConnectUUID currentCUUID = current.getUUIDForConnectionPassthrough();
		passthroughs.put(currentCUUID, current);
		connection2voxels(current).apply(connection).ifPresent(posRotVoxels -> {
			Set<ConnectionPassthrough> adjacentPTs = getAdjacentPTs(world, posRotVoxels.getLeft(), posRotVoxels.getRight(), connection);
			adjacentPTs.remove(previous);
			Set<PhysicalDevice> adjacentDevices = getAdjacentDevices(world, posRotVoxels.getLeft(), posRotVoxels.getRight(), connection);
			if(adjacentPTs.size() == 1 && adjacentDevices.isEmpty()){
				//return link;
				res.setValue(new ExplorationResult.Link(adjacentPTs.toArray(new ConnectionPassthrough[1])[0]));
			} else {
				//return node;
				getOrCreateNode(currentCUUID, nnode -> nodesExplorer.add(() -> {
					adjacentDevices.forEach(nnode::addDevice);
					adjacentPTs.stream().filter(adj -> !passthroughs.containsKey(adj.getUUIDForConnectionPassthrough())).forEach(adjacentPT -> {
						ConnectionPassthrough p = current;
						ConnectionPassthrough c = adjacentPT;
						List<ConnectUUID> es = new ArrayList<>();
						ExplorationResult explorationResult = exploreAdjacent(world, connection, c, p);
						while(explorationResult instanceof ExplorationResult.Link){
							es.add(c.getUUIDForConnectionPassthrough());
							p = c;
							c = ((ExplorationResult.Link) explorationResult).next;
							explorationResult = exploreAdjacent(world, connection, c, p);
						}
						createLink(nnode, getOrCreateNode(c.getUUIDForConnectionPassthrough(), nnnn -> {}), es);
					});
				}));
				res.setValue(new ExplorationResult.Node());
			}
		});
		return res.getValue();
	}

	protected static abstract class ExplorationResult {

		static class Link extends ExplorationResult {

			protected final ConnectionPassthrough next;

			public Link(ConnectionPassthrough next){
				this.next = next;
			}

		}

		static class Node extends ExplorationResult {

		}

	}

	/*
	 * Adjacent
	 */

	protected Set<ConnectionPassthrough> getAdjacentPTs(World world, PartPositionRotation positionRotation, Multimap<VoxelPosition, EnumFacing> voxels, ResourceLocation connection){
		Set<ConnectionPassthrough> adjacentPTs = new HashSet<>();
		forEachTargetVoxel(positionRotation, voxels, (v, dir) -> getConnectionPassthrough(world, v, dir, connection).ifPresent(adjacentPTs::add));
		return adjacentPTs;
	}

	protected Set<PhysicalDevice> getAdjacentDevices(World world, PartPositionRotation positionRotation, Multimap<VoxelPosition, EnumFacing> voxels, ResourceLocation connection){
		Set<PhysicalDevice> adjacentDevices = new HashSet<>();
		forEachTargetVoxel(positionRotation, voxels, (v, dir) -> getDevice(world, v, dir, connection).ifPresent(adjacentDevices::add));
		return adjacentDevices;
	}

	/*
	 * Get at
	 */

	protected Optional<ConnectionPassthrough> getConnectionPassthrough(World world, VoxelPosition position, EnumFacing from, ResourceLocation connection){
		MutableObject<ConnectionPassthrough> passthrough = new MutableObject<>();
		world.getCapability(PartsHelper.worldPartsAccessCapability, null).getPart(position).flatMap(PartInfo::getState).ifPresent(s -> {
			if(s instanceof ConnectionPassthrough)
				if(AppEngME.INSTANCE.getPartsHelper().canConnect(s.getPart(), s.getAssignedPosRot(), connection, position, from))
					passthrough.setValue((ConnectionPassthrough) s);
		});
		return Optional.ofNullable(passthrough.getValue());
	}

	protected Optional<PhysicalDevice> getDevice(World world, VoxelPosition position, EnumFacing from, ResourceLocation connection){
		MutableObject<PhysicalDevice> device = new MutableObject<>();
		world.getCapability(PartsHelper.worldPartsAccessCapability, null).getPart(position).flatMap(PartInfo::getState).ifPresent(s -> {
			if(s instanceof PhysicalDevice)
				if(AppEngME.INSTANCE.getPartsHelper().canConnect(s.getPart(), s.getAssignedPosRot(), connection, position, from))
					device.setValue((PhysicalDevice) s);
		});
		return Optional.ofNullable(device.getValue());
	}

	/*
	 * C->V
	 */

	protected void forEachTargetVoxel(PartPositionRotation positionRotation, Multimap<VoxelPosition, EnumFacing> connections, BiConsumer<VoxelPosition, EnumFacing> targetVoxelConsumer){
		connections.forEach((v, inDir) -> targetVoxelConsumer.accept(v.offsetLocal(inDir), inDir.getOpposite()));
	}

	protected <T> Function<ResourceLocation, Optional<Pair<PartPositionRotation, Multimap<VoxelPosition, EnumFacing>>>> connection2voxels(T t){
		if(t instanceof Part.State) return c -> AppEngME.INSTANCE.getPartsHelper().getConnections(((Part.State) t).getPart(), ((Part.State) t).getAssignedPosRot(), c).map(cs -> new ImmutablePair<>(((Part.State) t).getAssignedPosRot(), cs));
		return c -> Optional.empty();
	}

	/*
	 * Links & Nodes
	 */

	protected void createLink(Node from, Node to, List<ConnectUUID> elements){
		Link link = new Link(from, to);
		links.add(link);
		from.links.add(link);
		to.links.add(link);
		link.elements = elements;
	}

	protected Node getOrCreateNode(ConnectUUID uuid, Consumer<Node> newlyCreated){
		Node node = nodes.get(uuid);
		if(node == null){
			nodes.put(uuid, node = new Node(uuid));
			newlyCreated.accept(node);
		}
		return node;
	}

	protected class PathwayElement {

		protected List<Pathway> pathways;

	}

	protected class Node extends PathwayElement {

		protected ConnectUUID uuid;
		protected List<Link> links = new ArrayList<>();
		protected Set<ConnectUUID> devices = new HashSet<>();

		public Node(ConnectUUID uuid){
			this.uuid = uuid;
		}

		void addDevice(PhysicalDevice device){
			this.devices.add(device.getUUIDForConnection());
			NetBlockConnections.this.devices.put(device.getUUIDForConnection(), device);
		}

		NBTTagCompound serializeNBT(){
			return uuid.serializeNBT();
		}

		@Override
		public boolean equals(Object o){
			if(this == o) return true;
			if(!(o instanceof Node)) return false;
			Node node = (Node) o;
			return Objects.equals(uuid, node.uuid);
		}

		@Override
		public int hashCode(){
			return Objects.hash(uuid);
		}

		@Override
		public String toString(){
			return "Node{" + "uuid=" + uuid + ", links=" + links + ", devices=" + devices + '}';
		}

	}

	protected class Link extends PathwayElement {

		protected Node from, to;
		protected List<ConnectUUID> elements;

		public Link(Node from, Node to){
			this.from = from;
			this.to = to;
		}

		@Override
		public boolean equals(Object o){
			if(this == o) return true;
			if(!(o instanceof Link)) return false;
			Link link = (Link) o;
			return Objects.equals(from, link.from) && Objects.equals(to, link.to) && Objects.equals(elements, link.elements);
		}

		@Override
		public int hashCode(){
			return Objects.hash(from, to, elements);
		}

		@Override
		public String toString(){
			return "Link{" + "from=" + from + ", to=" + to + ", elements=" + elements + '}';
		}

	}

	protected class Pathway {

		protected List<PathwayElement> elements;
		protected boolean dormant;

	}

	/*
	 * IO
	 */

	@Override
	public NBTTagCompound serializeNBT(){
		NBTTagCompound nbt = new NBTTagCompound();
		return nbt;
	}

	@Override
	public void deserializeNBT(NBTTagCompound nbt){

	}

}
