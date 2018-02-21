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
import com.google.common.collect.Lists;
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

	protected Set<Node> nodes = new HashSet<>();
	protected Set<Link> links = new HashSet<>();

	//FIXME TMP
	Map<ConnectUUID, ConnectionPassthrough> passthroughs = new HashMap<>();
	Map<ConnectUUID, PhysicalDevice> devices = new HashMap<>();

	public void recalculateAll(World world, PhysicalDevice root){
		deviceConnectionParams.clear();
		nodes.clear();
		links.clear();
		Function<ResourceLocation, Optional<Pair<PartPositionRotation, Multimap<VoxelPosition, EnumFacing>>>> c2c = connection2voxels(root);
		if(c2c == null) return;
		Set<DeviceUUID> directLinks = new HashSet<>();
		//Graph generation
		AppEngME.INSTANCE.getDevicesHelper().forEachConnection(connection -> c2c.apply(connection.getId()).ifPresent(voxels -> {
			ResourceLocation cid = connection.getId();
			forEachTargetVoxel(voxels.getLeft(), voxels.getRight(), (voxel, dir) -> {
				getConnectionPassthrough(world, voxel, dir, cid).ifPresent(passthrough -> {
					passthroughs.put(passthrough.getUUIDForConnectionPassthrough(), passthrough);
					Node node = new Node(passthrough.getUUIDForConnectionPassthrough());
					nodes.add(node);
					exploreNode(node, world, passthrough, cid);
				});
				getDevice(world, voxel, dir, cid).ifPresent(device -> {
					devices.put(device.getUUIDForConnection(), device);
					directLinks.add(device.getNetworkCounterpart().getUUID());
					//TODO Params.
				});
			});
		}));
		System.out.println(nodes);
		System.out.println(links);
		System.out.println(passthroughs);
		System.out.println(devices);
	}

	protected void exploreNode(Node node, World world, ConnectionPassthrough passthrough, ResourceLocation connection){
		connection2voxels(passthrough).apply(connection).ifPresent(posRotVoxels -> {
			getAdjacentPTs(world, posRotVoxels.getLeft(), posRotVoxels.getRight(), connection).forEach(adjacent -> connection2voxels(adjacent).apply(connection).ifPresent(adjacentPRV -> {
				Set<ConnectionPassthrough> adjacentPTs = getAdjacentPTs(world, adjacentPRV.getLeft(), adjacentPRV.getRight(), connection);
				adjacentPTs.remove(passthrough);
				Set<PhysicalDevice> adjacentDevices = getAdjacentDevices(world, adjacentPRV.getLeft(), adjacentPRV.getRight(), connection);
				if(adjacentPTs.size() == 1 && adjacentDevices.isEmpty()){
					exploreLink(node, world, adjacent, connection);
				} else {
					Node end = new Node(adjacent.getUUIDForConnectionPassthrough());
					Link link = new Link(node, end);
					node.links.add(link);
					end.links.add(link);
					link.elements = new ArrayList<>();
					adjacentDevices.forEach(device -> {
						end.devices.add(device.getUUIDForConnection());
						devices.put(device.getUUIDForConnection(), device);
					});
					if(adjacentPTs.size() > 0) exploreNode(end, world, adjacent, connection);
				}
			}));
			getAdjacentDevices(world, posRotVoxels.getLeft(), posRotVoxels.getRight(), connection).forEach(device -> {
				node.devices.add(device.getUUIDForConnection());
				devices.put(device.getUUIDForConnection(), device);
			});
		});
	}

	protected void exploreLink(Node from, World world, ConnectionPassthrough e1st, ResourceLocation connection){
		List<ConnectionPassthrough> elements = new ArrayList<>();
		elements.add(e1st);
		MutableObject<ConnectionPassthrough> next = new MutableObject<>(e1st);
		MutableObject<Set<ConnectionPassthrough>> nextAdjacentPTs = new MutableObject<>(connection2voxels(next.getValue()).apply(connection).map(posRotVoxels -> getAdjacentPTs(world, posRotVoxels.getLeft(), posRotVoxels.getRight(), connection)).orElse(new HashSet<>()));
		while(nextAdjacentPTs.getValue() != null && next.getValue() != null){
			passthroughs.put(next.getValue().getUUIDForConnectionPassthrough(), next.getValue());
			ConnectionPassthrough prev = next.getValue();
			nextAdjacentPTs.getValue().forEach(adjacent -> connection2voxels(adjacent).apply(connection).ifPresent(adjacentPRV -> {
				Set<ConnectionPassthrough> adjacentPTs = getAdjacentPTs(world, adjacentPRV.getLeft(), adjacentPRV.getRight(), connection);
				adjacentPTs.remove(next);
				Set<PhysicalDevice> adjacentDevices = getAdjacentDevices(world, adjacentPRV.getLeft(), adjacentPRV.getRight(), connection);
				if(adjacentPTs.size() == 1 && adjacentDevices.isEmpty()){
					elements.add(adjacent);
					next.setValue(adjacent);
					nextAdjacentPTs.setValue(adjacentPTs);
				} else {
					Node end = new Node(adjacent.getUUIDForConnectionPassthrough());
					Link link = new Link(from, end);
					from.links.add(link);
					end.links.add(link);
					link.elements = Lists.transform(elements, ConnectionPassthrough::getUUIDForConnectionPassthrough);
					adjacentDevices.forEach(device -> {
						end.devices.add(device.getUUIDForConnection());
						devices.put(device.getUUIDForConnection(), device);
					});
					if(adjacentPTs.size() > 0) exploreNode(end, world, adjacent, connection);
					//break;
				}
			}));
			if(next.getValue() == prev) break;
		}
	}

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

	protected void forEachTargetVoxel(PartPositionRotation positionRotation, Multimap<VoxelPosition, EnumFacing> connections, BiConsumer<VoxelPosition, EnumFacing> targetVoxelConsumer){
		connections.forEach((v, inDir) -> targetVoxelConsumer.accept(v.offsetLocal(inDir), inDir.getOpposite()));
	}

	protected <T> Function<ResourceLocation, Optional<Pair<PartPositionRotation, Multimap<VoxelPosition, EnumFacing>>>> connection2voxels(T t){
		if(t instanceof Part.State) return c -> AppEngME.INSTANCE.getPartsHelper().getConnections(((Part.State) t).getPart(), ((Part.State) t).getAssignedPosRot(), c).map(cs -> new ImmutablePair<>(((Part.State) t).getAssignedPosRot(), cs));
		return c -> Optional.empty();
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
