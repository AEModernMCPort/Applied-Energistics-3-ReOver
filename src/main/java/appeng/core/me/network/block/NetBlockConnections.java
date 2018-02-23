package appeng.core.me.network.block;

import appeng.core.me.AppEngME;
import appeng.core.me.api.network.DeviceUUID;
import appeng.core.me.api.network.NetDevice;
import appeng.core.me.api.network.PhysicalDevice;
import appeng.core.me.api.network.block.ConnectUUID;
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
import net.minecraft.util.ResourceLocation;
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

	protected Map<DeviceUUID, Map<ResourceLocation, ?>> deviceConnectionParams = new HashMap<>();

	/*
	 * Path
	 */

	protected Map<ConnectUUID, Node> nodes = new HashMap<>();
	protected Set<Link> links = new HashSet<>();

	//FIXME TMP
	Map<ConnectUUID, ConnectionPassthrough> passthroughs = new HashMap<>();
	Map<ConnectUUID, NetDevice> devices = new HashMap<>();

	public void recalculateAll(World world, PhysicalDevice root){
		long t = System.currentTimeMillis();
		deviceConnectionParams.clear();
		nodes.clear();
		links.clear();
		Optional<Triple<PartPositionRotation, ConnectionsParams, Multimap<Pair<VoxelPosition, EnumFacing>, ResourceLocation>>> oPrCsVs = voxels(root);
		if(!oPrCsVs.isPresent()) return;
		Set<DeviceUUID> directLinks = new HashSet<>();
		//Graph generation
		Multimap<Pair<VoxelPosition, EnumFacing>, ResourceLocation> rootVoxels = oPrCsVs.get().getRight();
		getAdjacentPTs(world, rootVoxels).keySet().forEach(passthrough -> exploreAdjacent(world, passthrough, null));
		getAdjacentDevices(world, rootVoxels).keySet().forEach(device -> {
			addDevice(device.getNetworkCounterpart());
			directLinks.add(device.getNetworkCounterpart().getUUID());
		});
		exploreNodes();
		AppEngME.logger.info("GC took " + (System.currentTimeMillis() - t) + "ms");
		AppEngME.logger.info(nodes.size() + " nodes");
		AppEngME.logger.info(links.size() + " links");
		AppEngME.logger.info(passthroughs.size() + " PTs");
		AppEngME.logger.info(devices);
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
			Multimap<ConnectionPassthrough, ResourceLocation> adjacentPTs = getAdjacentPTs(world, prCsVs.getRight());
			adjacentPTs.removeAll(previous);
			Multimap<PhysicalDevice, ResourceLocation> adjacentDevices = getAdjacentDevices(world, prCsVs.getRight());
			if(adjacentPTs.keySet().size() == 1 && adjacentDevices.isEmpty()){
				//return link;
				ConnectionPassthrough adjN0 = adjacentPTs.keySet().toArray(new ConnectionPassthrough[1])[0];
				res.setValue(new ExplorationResult.Link(prCsVs.getMiddle(), adjN0));
			} else {
				//return node;
				getOrCreateNode(currentCUUID, prCsVs.getMiddle(), nnode -> nodesExplorer.add(() -> {
					adjacentDevices.keySet().forEach(device -> nnode.addDevice(device.getNetworkCounterpart(), adjacentDevices.get(device)));
					adjacentPTs.keySet().stream().filter(adj -> !passthroughs.containsKey(adj.getUUIDForConnectionPassthrough())).forEach(adjacentPT -> {
						ConnectionPassthrough p = current;
						ConnectionPassthrough c = adjacentPT;
						List<ConnectUUID> es = new ArrayList<>();
						ConnectionsParams params = null;
						ExplorationResult explorationResult = exploreAdjacent(world, c, p);
						while(explorationResult instanceof ExplorationResult.Link){
							es.add(c.getUUIDForConnectionPassthrough());
							params = ConnectionsParams.join(params, explorationResult.connectionsParams);
							p = c;
							c = ((ExplorationResult.Link) explorationResult).next;
							explorationResult = exploreAdjacent(world, c, p);
						}
						createLink(nnode, getOrCreateNode(c.getUUIDForConnectionPassthrough(), explorationResult.connectionsParams, nnnn -> {}), es, params);
					});
				}));
				res.setValue(new ExplorationResult.Node(prCsVs.getMiddle()));
			}
		});
		return res.getValue();
	}

	protected static abstract class ExplorationResult {

		protected final ConnectionsParams connectionsParams;

		ExplorationResult(ConnectionsParams connectionsParams){
			this.connectionsParams = connectionsParams;
		}

		static class Link extends ExplorationResult {

			protected final ConnectionPassthrough next;

			Link(ConnectionsParams connectionsParams, ConnectionPassthrough next){
				super(connectionsParams);
				this.next = next;
			}
		}

		static class Node extends ExplorationResult {

			Node(ConnectionsParams connectionsParams){
				super(connectionsParams);
			}
		}

	}

	/*
	 * Adjacent
	 */

	protected Multimap<ConnectionPassthrough, ResourceLocation> getAdjacentPTs(World world, Multimap<Pair<VoxelPosition, EnumFacing>, ResourceLocation> voxels){
		Multimap<ConnectionPassthrough, ResourceLocation> adjacentPTs = HashMultimap.create();
		forEachTargetVoxel(voxels, (v, dir, cs) -> getConnectionPassthrough(world, v, dir, cs).ifPresent(cptCs -> adjacentPTs.putAll(cptCs.getLeft(), cptCs.getRight())));
		return adjacentPTs;
	}

	protected Multimap<PhysicalDevice, ResourceLocation> getAdjacentDevices(World world, Multimap<Pair<VoxelPosition, EnumFacing>, ResourceLocation> voxels){
		Multimap<PhysicalDevice, ResourceLocation> adjacentDevices = HashMultimap.create();
		forEachTargetVoxel(voxels, (v, dir, cs) -> getDevice(world, v, dir, cs).ifPresent(phdCs -> adjacentDevices.putAll(phdCs.getLeft(), phdCs.getRight())));
		return adjacentDevices;
	}

	/*
	 * Get at
	 */

	protected Optional<Pair<ConnectionPassthrough, Collection<ResourceLocation>>> getConnectionPassthrough(World world, VoxelPosition position, EnumFacing from, Collection<ResourceLocation> connections){
		MutableObject<Pair<ConnectionPassthrough, Collection<ResourceLocation>>> passthrough = new MutableObject<>();
		world.getCapability(PartsHelper.worldPartsAccessCapability, null).getPart(position).flatMap(PartInfo::getState).ifPresent(s -> {
			if(s instanceof ConnectionPassthrough){
				List<ResourceLocation> cs = connections.stream().filter(c -> AppEngME.INSTANCE.getPartsHelper().canConnect(s.getPart(), s.getAssignedPosRot(), c, position, from)).collect(Collectors.toList());
				if(!cs.isEmpty()) passthrough.setValue(new ImmutablePair<>((ConnectionPassthrough) s, cs));
			}
		});
		return Optional.ofNullable(passthrough.getValue());
	}

	protected Optional<Pair<PhysicalDevice, Collection<ResourceLocation>>> getDevice(World world, VoxelPosition position, EnumFacing from, Collection<ResourceLocation> connections){
		MutableObject<Pair<PhysicalDevice, Collection<ResourceLocation>>> device = new MutableObject<>();
		world.getCapability(PartsHelper.worldPartsAccessCapability, null).getPart(position).flatMap(PartInfo::getState).ifPresent(s -> {
			if(s instanceof PhysicalDevice){
				List<ResourceLocation> cs = connections.stream().filter(c -> AppEngME.INSTANCE.getPartsHelper().canConnect(s.getPart(), s.getAssignedPosRot(), c, position, from)).collect(Collectors.toList());
				if(!cs.isEmpty()) device.setValue(new ImmutablePair<>((PhysicalDevice) s, cs));
			}
		});
		return Optional.ofNullable(device.getValue());
	}

	/*
	 * C->V
	 */

	protected void forEachTargetVoxel(Multimap<Pair<VoxelPosition, EnumFacing>, ResourceLocation> connections, TriConsumer<VoxelPosition, EnumFacing, Collection<ResourceLocation>> targetVoxelConsumer){
		connections.keySet().forEach(vS -> targetVoxelConsumer.accept(vS.getLeft().offsetLocal(vS.getRight()), vS.getRight().getOpposite(), connections.get(vS)));
	}

	protected <T> Optional<Triple<PartPositionRotation, ConnectionsParams, Multimap<Pair<VoxelPosition, EnumFacing>, ResourceLocation>>> voxels(T t){
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
		this.devices.put(device.getUUIDForConnection(), device);
		device.switchNetBlock(netBlock);
	}

	protected void createLink(Node from, Node to, List<ConnectUUID> elements, ConnectionsParams params){
		Link link = new Link(from, to, params);
		links.add(link);
		from.links.add(link);
		to.links.add(link);
		link.elements = elements;
	}

	protected Node getOrCreateNode(ConnectUUID uuid, ConnectionsParams params, Consumer<Node> newlyCreated){
		Node node = nodes.get(uuid);
		if(node == null){
			nodes.put(uuid, node = new Node(uuid, params));
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
		protected Multimap<ConnectUUID, ResourceLocation> devices = HashMultimap.create();

		protected ConnectionsParams params;

		public Node(ConnectUUID uuid, ConnectionsParams params){
			this.uuid = uuid;
			this.params = params;
		}

		void addDevice(NetDevice device, Collection<ResourceLocation> connections){
			this.devices.putAll(device.getUUIDForConnection(), connections);
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

		protected ConnectionsParams params;

		public Link(Node from, Node to, ConnectionsParams params){
			this.from = from;
			this.to = to;
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
