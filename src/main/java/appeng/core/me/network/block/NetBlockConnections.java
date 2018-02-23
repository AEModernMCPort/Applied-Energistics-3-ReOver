package appeng.core.me.network.block;

import appeng.core.me.AppEngME;
import appeng.core.me.api.network.DeviceUUID;
import appeng.core.me.api.network.NetDevice;
import appeng.core.me.api.network.PhysicalDevice;
import appeng.core.me.api.network.block.ConnectUUID;
import appeng.core.me.api.network.block.Connection;
import appeng.core.me.api.network.block.ConnectionPassthrough;
import appeng.core.me.api.parts.PartPositionRotation;
import appeng.core.me.api.parts.VoxelPosition;
import appeng.core.me.api.parts.container.PartInfo;
import appeng.core.me.api.parts.part.Part;
import appeng.core.me.network.NetBlockImpl;
import appeng.core.me.network.connect.ConnectionsParams;
import appeng.core.me.parts.part.PartsHelper;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraftforge.common.util.INBTSerializable;
import org.apache.commons.lang3.mutable.MutableObject;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.ImmutableTriple;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;
import org.apache.logging.log4j.util.TriConsumer;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class NetBlockConnections implements INBTSerializable<NBTTagCompound> {

	protected NetBlockImpl netBlock;

	public NetBlockConnections(NetBlockImpl netBlock){
		this.netBlock = netBlock;
	}

	/*
	 * Path
	 */

	Map<ConnectUUID, ConnectionPassthrough> passthroughs = new HashMap<>();

	public void recalculateAll(World world, PhysicalDevice root){
		long t = System.currentTimeMillis();
		generateGraph(world, root);
		AppEngME.logger.info("CR took " + (System.currentTimeMillis() - t) + "ms");
		AppEngME.logger.info(passthroughs.size() + " PTs");
	}

	/*
	 * Graph Gen
	 */

	protected Map<ConnectUUID, Node> nodes = new HashMap<>();
	protected Set<Link> links = new HashSet<>();

	transient Set<NetDevice> devicesToRoute;
	transient Multimap<DeviceUUID, Node> dtr2n;

	protected void generateGraph(World world, PhysicalDevice root){
		long t = System.currentTimeMillis();
		nodes.clear();
		links.clear();
		devicesToRoute = new HashSet<>();
		dtr2n = HashMultimap.create();
		Optional<Triple<PartPositionRotation, ConnectionsParams, Multimap<Pair<VoxelPosition, EnumFacing>, Connection>>> oPrCsVs = voxels(root);
		if(!oPrCsVs.isPresent()) return;
		Set<DeviceUUID> directLinks = new HashSet<>();
		//Graph generation
		Multimap<Pair<VoxelPosition, EnumFacing>, Connection> rootVoxels = oPrCsVs.get().getRight();
		getAdjacentPTs(world, rootVoxels).keySet().forEach(passthrough -> exploreAdjacent(world, passthrough, null));
		getAdjacentDevices(world, rootVoxels).keySet().forEach(device -> {
			addDevice(device.getNetworkCounterpart());
			directLinks.add(device.getNetworkCounterpart().getUUID());
		});
		exploreNodes();
		AppEngME.logger.info("GC took " + (System.currentTimeMillis() - t) + "ms");
		AppEngME.logger.info(nodes.size() + " nodes");
		AppEngME.logger.info(links.size() + " links");
	}

	protected Queue<Runnable> nodesExplorer = new LinkedList<>();

	protected void exploreNodes(){
		while(nodesExplorer.peek() != null) nodesExplorer.poll().run();
	}

	protected ExplorationResult exploreAdjacent(World world, ConnectionPassthrough current, ConnectionPassthrough previous){
		final MutableObject<ExplorationResult> res = new MutableObject<>();
		final ConnectUUID currentCUUID = current.getUUIDForConnectionPassthrough();
		addPassthrough(current);
		voxels(current).ifPresent(prCsVs -> {
			Multimap<ConnectionPassthrough, Connection> adjacentPTs = getAdjacentPTs(world, prCsVs.getRight());
			adjacentPTs.removeAll(previous);
			Multimap<PhysicalDevice, Connection> adjacentDevices = getAdjacentDevices(world, prCsVs.getRight());
			if(adjacentPTs.keySet().size() == 1 && adjacentDevices.isEmpty()){
				//return link;
				ConnectionPassthrough adjN0 = adjacentPTs.keySet().toArray(new ConnectionPassthrough[1])[0];
				res.setValue(new ExplorationResult.Link(prCsVs.getMiddle(), current.getLength(), adjN0));
			} else {
				//return node;
				getOrCreateNode(currentCUUID, current.getLength(), prCsVs.getMiddle(), nnode -> nodesExplorer.add(() -> {
					adjacentDevices.keySet().forEach(device -> nnode.addDevice(device.getNetworkCounterpart(), adjacentDevices.get(device)));
					adjacentPTs.keySet().stream().filter(adj -> !passthroughs.containsKey(adj.getUUIDForConnectionPassthrough())).forEach(adjacentPT -> {
						ConnectionPassthrough p = current;
						ConnectionPassthrough c = adjacentPT;
						List<ConnectUUID> es = new ArrayList<>();
						double length = 0;
						ConnectionsParams params = null;
						ExplorationResult explorationResult = exploreAdjacent(world, c, p);
						while(explorationResult instanceof ExplorationResult.Link){
							es.add(c.getUUIDForConnectionPassthrough());
							length += explorationResult.length;
							params = ConnectionsParams.join(params, explorationResult.connectionsParams);
							p = c;
							c = ((ExplorationResult.Link) explorationResult).next;
							explorationResult = exploreAdjacent(world, c, p);
						}
						createLink(nnode, getOrCreateNode(c.getUUIDForConnectionPassthrough(), c.getLength(), explorationResult.connectionsParams, nnnn -> {}), es, length, params);
					});
				}));
				res.setValue(new ExplorationResult.Node(prCsVs.getMiddle(), current.getLength()));
			}
		});
		return res.getValue();
	}

	protected static abstract class ExplorationResult {

		protected final ConnectionsParams connectionsParams;
		protected final double length;

		public ExplorationResult(ConnectionsParams connectionsParams, double length){
			this.connectionsParams = connectionsParams;
			this.length = length;
		}

		static class Link extends ExplorationResult {

			protected final ConnectionPassthrough next;

			Link(ConnectionsParams connectionsParams, double length, ConnectionPassthrough next){
				super(connectionsParams, length);
				this.next = next;
			}
		}

		static class Node extends ExplorationResult {

			public Node(ConnectionsParams connectionsParams, double length){
				super(connectionsParams, length);
			}
		}

	}

	/*
	 * Adjacent
	 */

	protected Multimap<ConnectionPassthrough, Connection> getAdjacentPTs(World world, Multimap<Pair<VoxelPosition, EnumFacing>, Connection> voxels){
		Multimap<ConnectionPassthrough, Connection> adjacentPTs = HashMultimap.create();
		forEachTargetVoxel(voxels, (v, dir, cs) -> getConnectionPassthrough(world, v, dir, cs).ifPresent(cptCs -> adjacentPTs.putAll(cptCs.getLeft(), cptCs.getRight())));
		return adjacentPTs;
	}

	protected Multimap<PhysicalDevice, Connection> getAdjacentDevices(World world, Multimap<Pair<VoxelPosition, EnumFacing>, Connection> voxels){
		Multimap<PhysicalDevice, Connection> adjacentDevices = HashMultimap.create();
		forEachTargetVoxel(voxels, (v, dir, cs) -> getDevice(world, v, dir, cs).ifPresent(phdCs -> adjacentDevices.putAll(phdCs.getLeft(), phdCs.getRight())));
		return adjacentDevices;
	}

	/*
	 * Get at
	 */

	protected Optional<Pair<ConnectionPassthrough, Collection<Connection>>> getConnectionPassthrough(World world, VoxelPosition position, EnumFacing from, Collection<Connection> connections){
		MutableObject<Pair<ConnectionPassthrough, Collection<Connection>>> passthrough = new MutableObject<>();
		world.getCapability(PartsHelper.worldPartsAccessCapability, null).getPart(position).flatMap(PartInfo::getState).ifPresent(s -> {
			if(s instanceof ConnectionPassthrough){
				List<Connection> cs = connections.stream().filter(c -> AppEngME.INSTANCE.getPartsHelper().canConnect(s.getPart(), s.getAssignedPosRot(), c, position, from)).collect(Collectors.toList());
				if(!cs.isEmpty()) passthrough.setValue(new ImmutablePair<>((ConnectionPassthrough) s, cs));
			}
		});
		return Optional.ofNullable(passthrough.getValue());
	}

	protected Optional<Pair<PhysicalDevice, Collection<Connection>>> getDevice(World world, VoxelPosition position, EnumFacing from, Collection<Connection> connections){
		MutableObject<Pair<PhysicalDevice, Collection<Connection>>> device = new MutableObject<>();
		world.getCapability(PartsHelper.worldPartsAccessCapability, null).getPart(position).flatMap(PartInfo::getState).ifPresent(s -> {
			if(s instanceof PhysicalDevice){
				List<Connection> cs = connections.stream().filter(c -> AppEngME.INSTANCE.getPartsHelper().canConnect(s.getPart(), s.getAssignedPosRot(), c, position, from)).collect(Collectors.toList());
				if(!cs.isEmpty()) device.setValue(new ImmutablePair<>((PhysicalDevice) s, cs));
			}
		});
		return Optional.ofNullable(device.getValue());
	}

	/*
	 * C->V
	 */

	protected void forEachTargetVoxel(Multimap<Pair<VoxelPosition, EnumFacing>, Connection> connections, TriConsumer<VoxelPosition, EnumFacing, Collection<Connection>> targetVoxelConsumer){
		connections.keySet().forEach(vS -> targetVoxelConsumer.accept(vS.getLeft().offsetLocal(vS.getRight()), vS.getRight().getOpposite(), connections.get(vS)));
	}

	protected <T> Optional<Triple<PartPositionRotation, ConnectionsParams, Multimap<Pair<VoxelPosition, EnumFacing>, Connection>>> voxels(T t){
		if(t instanceof Part.State) return Optional.of(new ImmutableTriple<>(((Part.State) t).getAssignedPosRot(), AppEngME.INSTANCE.getPartsHelper().getConnectionParams((Part.State) t, t instanceof ConnectionPassthrough ? c -> ((ConnectionPassthrough) t).getPassthroughConnectionParameter(c) : c -> 0), AppEngME.INSTANCE.getPartsHelper().getConnections(((Part.State) t).getPart(), ((Part.State) t).getAssignedPosRot())));
		return Optional.empty();
	}

	/*
	 * Links, Nodes, Devices...
	 */

	protected void addPassthrough(ConnectionPassthrough passthrough){
		this.passthroughs.put(passthrough.getUUIDForConnectionPassthrough(), passthrough);
		passthrough.assignNetBlock(netBlock);
	}

	protected void addDevice(NetDevice device){
		devicesToRoute.add(device);
		device.switchNetBlock(netBlock);
	}

	protected void createLink(Node from, Node to, List<ConnectUUID> elements, double length, ConnectionsParams params){
		Link link = new Link(from, to, length, params);
		links.add(link);
		from.links.add(link);
		to.links.add(link);
		link.elements = elements;
	}

	protected Node getOrCreateNode(ConnectUUID uuid, double length, ConnectionsParams params, Consumer<Node> newlyCreated){
		Node node = nodes.get(uuid);
		if(node == null){
			nodes.put(uuid, node = new Node(uuid, length, params));
			newlyCreated.accept(node);
		}
		return node;
	}

	protected class PathwayElement {

		protected List<Pathway> pathways;

	}

	protected class Node extends PathwayElement {

		protected ConnectUUID uuid;
		protected double length;
		protected List<Link> links = new ArrayList<>();
		protected Multimap<ConnectUUID, Connection> devices = HashMultimap.create();

		protected ConnectionsParams params;

		public Node(ConnectUUID uuid, double length, ConnectionsParams params){
			this.uuid = uuid;
			this.length = length;
			this.params = params;
		}

		void addDevice(NetDevice device, Collection<Connection> connections){
			this.devices.putAll(device.getUUIDForConnection(), connections);
			dtr2n.put(device.getUUID(), this);
			NetBlockConnections.this.addDevice(device);
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
			return "Node{" + "uuid=" + uuid + ", " + links.size() + " links, " + devices.size() + " devices, params=" + params + '}';
		}
	}

	protected class Link extends PathwayElement {

		protected Node from, to;
		protected List<ConnectUUID> elements;
		protected double length;

		protected ConnectionsParams params;

		public Link(Node from, Node to, double length, ConnectionsParams params){
			this.from = from;
			this.to = to;
			this.length = length;
			this.params = params;
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
			return "Link{" + "from=" + from.uuid + ", to=" + to.uuid + ", " + elements.size() + " elements, params=" + params + '}';
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
