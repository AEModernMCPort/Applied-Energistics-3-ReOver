package appeng.core.me.network;

import appeng.api.bootstrap.InitializationComponent;
import appeng.core.AppEng;
import appeng.core.me.AppEngME;
import appeng.core.me.api.network.NetDevice;
import appeng.core.me.api.network.PhysicalDevice;
import appeng.core.me.api.network.block.Connection;
import appeng.core.me.api.network.block.ConnectionPassthrough;
import appeng.core.me.api.parts.PartPositionRotation;
import appeng.core.me.api.parts.VoxelPosition;
import appeng.core.me.api.parts.container.PartInfo;
import appeng.core.me.api.parts.part.Part;
import appeng.core.me.network.connect.ConnectionsParams;
import appeng.core.me.network.connect.DataConnection;
import appeng.core.me.network.connect.SPIntConnection;
import appeng.core.me.parts.part.PartsHelperImpl;
import appeng.core.me.parts.part.device.Controller;
import com.google.common.collect.*;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import org.apache.commons.lang3.mutable.MutableObject;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.ImmutableTriple;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;
import org.apache.logging.log4j.util.TriConsumer;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

public class DevicesHelper implements InitializationComponent {

	protected Map<ResourceLocation, Connection> connections = new HashMap<>();

	public void registerConnection(Connection connection){
		connections.put(connection.getId(), connection);
	}

	public <P extends Comparable<P>> Connection<P, ?> getConnection(ResourceLocation id){
		return connections.get(id);
	}

	public void forEachConnection(Consumer<Connection> consumer){
		connections.values().forEach(consumer);
	}

	@Deprecated
	public Connection ENERGY = new SPIntConnection(new ResourceLocation(AppEng.MODID, "energy"), AppEngME.INSTANCE.config.energyMaxDistance);
	@Deprecated
	public Connection DATA = new DataConnection(new ResourceLocation(AppEng.MODID, "data"), AppEngME.INSTANCE.config.dataMaxDistance);

	@Override
	public void init(){
		AppEngME.INSTANCE.getPartsHelper().registerCustomPartDataLoader(CONNECTIVITYLOADER, (part, meshLoader, voxelizer, rootMeshVoxels) -> {
			ImmutableSet.Builder<ResourceLocation> connectionsBuilder = new ImmutableSet.Builder<>();
			ImmutableMultimap.Builder<Pair<VoxelPosition, EnumFacing>, ResourceLocation> connectivityBuilder = new ImmutableMultimap.Builder<>();
			AppEngME.INSTANCE.getDevicesHelper().forEachConnection(connection -> meshLoader.apply(connection.getId().toString().replace(":", "-_-")).ifPresent(cmesh -> forEachInterface(voxelizer.apply(cmesh), rootMeshVoxels, (voxel, side) -> {
				connectionsBuilder.add(connection.getId());
				connectivityBuilder.put(new ImmutablePair<>(voxel, side), connection.getId());
			})));
			return Optional.of(new PartConnectivity(connectionsBuilder.build(), connectivityBuilder.build()));
		});

		AppEngME.INSTANCE.registerConnection(ENERGY);
		AppEngME.INSTANCE.registerConnection(DATA);
	}

	protected void forEachInterface(Set<VoxelPosition> inter2faces, Set<VoxelPosition> in, BiConsumer<VoxelPosition, EnumFacing> interfaceConsumer){
		inter2faces.stream().filter(in::contains).forEach(inVoxel -> {
			for(EnumFacing side : EnumFacing.values()){
				VoxelPosition outVoxel = inVoxel.offsetLocal(side);
				if(inter2faces.contains(outVoxel) && !in.contains(outVoxel))interfaceConsumer.accept(inVoxel, side);
			}
		});
	}

	/*
	 * Adjacent
	 */

	public Multimap<ConnectionPassthrough, Connection> getAdjacentPTs(World world, Multimap<Pair<VoxelPosition, EnumFacing>, Connection> voxels){
		Multimap<ConnectionPassthrough, Connection> adjacentPTs = HashMultimap.create();
		forEachTargetVoxel(voxels, (v, dir, cs) -> getConnectionPassthrough(world, v, dir, cs).ifPresent(cptCs -> adjacentPTs.putAll(cptCs.getLeft(), cptCs.getRight())));
		return adjacentPTs;
	}

	public Multimap<PhysicalDevice, Connection> getAdjacentDevices(World world, Multimap<Pair<VoxelPosition, EnumFacing>, Connection> voxels){
		Multimap<PhysicalDevice, Connection> adjacentDevices = HashMultimap.create();
		forEachTargetVoxel(voxels, (v, dir, cs) -> getDevice(world, v, dir, cs).ifPresent(phdCs -> adjacentDevices.putAll(phdCs.getLeft(), phdCs.getRight())));
		return adjacentDevices;
	}

	/*
	 * Get at
	 */

	public Optional<Pair<ConnectionPassthrough, Collection<Connection>>> getConnectionPassthrough(World world, VoxelPosition position, EnumFacing from, Collection<Connection> connections){
		MutableObject<Pair<ConnectionPassthrough, Collection<Connection>>> passthrough = new MutableObject<>();
		world.getCapability(PartsHelperImpl.worldPartsAccessCapability, null).getPart(position).flatMap(PartInfo::getState).ifPresent(s -> {
			if(s instanceof ConnectionPassthrough){
				List<Connection> cs = connections.stream().filter(c -> canConnect(s.getPart(), s.getAssignedPosRot(), c, position, from)).collect(Collectors.toList());
				if(!cs.isEmpty()) passthrough.setValue(new ImmutablePair<>((ConnectionPassthrough) s, cs));
			}
		});
		return Optional.ofNullable(passthrough.getValue());
	}

	public Optional<Pair<PhysicalDevice, Collection<Connection>>> getDevice(World world, VoxelPosition position, EnumFacing from, Collection<Connection> connections){
		MutableObject<Pair<PhysicalDevice, Collection<Connection>>> device = new MutableObject<>();
		world.getCapability(PartsHelperImpl.worldPartsAccessCapability, null).getPart(position).flatMap(PartInfo::getState).ifPresent(s -> {
			if(s instanceof PhysicalDevice){
				List<Connection> cs = connections.stream().filter(c -> canConnect(s.getPart(), s.getAssignedPosRot(), c, position, from)).collect(Collectors.toList());
				if(!cs.isEmpty()) device.setValue(new ImmutablePair<>((PhysicalDevice) s, cs));
			}
		});
		return Optional.ofNullable(device.getValue());
	}

	/*
	 * C->V
	 */

	public void forEachTargetVoxel(Multimap<Pair<VoxelPosition, EnumFacing>, Connection> connections, TriConsumer<VoxelPosition, EnumFacing, Collection<Connection>> targetVoxelConsumer){
		connections.keySet().forEach(vS -> targetVoxelConsumer.accept(vS.getLeft().offsetLocal(vS.getRight()), vS.getRight().getOpposite(), connections.get(vS)));
	}

	public <T> Optional<Triple<PartPositionRotation, ConnectionsParams, Multimap<Pair<VoxelPosition, EnumFacing>, Connection>>> voxels(T t){
		if(t instanceof Part.State) return Optional.of(new ImmutableTriple<>(((Part.State) t).getAssignedPosRot(), getConnectionParams((Part.State) t, t instanceof ConnectionPassthrough ? c -> ((ConnectionPassthrough) t).getPassthroughConnectionParameter(c) : c -> 0), getConnections(((Part.State) t).getPart(), ((Part.State) t).getAssignedPosRot())));
		return Optional.empty();
	}

	public Optional<ConnectionsParams<?>> getConnectionsParams(ConnectionPassthrough passthrough){
		if(passthrough instanceof Part.State) return Optional.of(getConnectionParams((Part.State) passthrough, passthrough::getPassthroughConnectionParameter));
		return Optional.empty();
	}

	/*
	 * Connectivity
	 */

	public boolean haveConnectionsInCommon(Part p1, Part p2){
		PartConnectivity d2 = getConnectivity(p2);
		for(ResourceLocation c1 : getConnectivity(p1).connections) if(d2.connections.contains(c1)) return true;
		return false;
	}

	public ConnectionsParams getConnectionParams(Part.State part, Function<Connection, Comparable<?>> c2p){
		Map<Connection, Comparable<?>> params = new HashMap<>();
		getConnectivity(part.getPart()).connections.forEach(cId -> {
			Connection c = AppEngME.INSTANCE.getDevicesHelper().getConnection(cId);
			params.put(c, c2p.apply(c));
		});
		return new ConnectionsParams(params);
	}

	public boolean canConnect(Part part, PartPositionRotation positionRotation, Connection connection, VoxelPosition voxel, EnumFacing sideFrom){
		return getConnectivity(part).connectivity.containsEntry(new ImmutablePair<>(positionRotation.getRotation().inverse().rotate(voxel.substract(positionRotation.getRotationCenterPosition())), positionRotation.getRotation().inverse().rotate(sideFrom)), connection.getId());
	}

	public Multimap<Pair<VoxelPosition, EnumFacing>, Connection> getConnections(Part part, PartPositionRotation positionRotation){
		Multimap<Pair<VoxelPosition, EnumFacing>, ResourceLocation> connections = getConnectivity(part).connectivity;
		Multimap<Pair<VoxelPosition, EnumFacing>, Connection> rotated = HashMultimap.create();
		connections.keySet().forEach(voxelSide -> rotated.putAll(applyTransforms(voxelSide.getLeft(), voxelSide.getRight(), positionRotation), Iterables.transform(connections.get(voxelSide), AppEngME.INSTANCE.getDevicesHelper()::getConnection)));
		return rotated;
	}

	protected Pair<VoxelPosition, EnumFacing> applyTransforms(VoxelPosition voxel, EnumFacing sideFrom, PartPositionRotation positionRotation){
		return new ImmutablePair<>(positionRotation.getRotation().rotate(voxel).add(positionRotation.getRotationCenterPosition()), positionRotation.getRotation().rotate(sideFrom));
	}

	protected static final ResourceLocation CONNECTIVITYLOADER = new ResourceLocation(AppEng.MODID, "connectivity");

	protected PartConnectivity getConnectivity(Part part){
		return AppEngME.INSTANCE.getPartsHelper().<PartConnectivity>getCustomPartData(part, CONNECTIVITYLOADER).orElse(EMPTYPC);
	}

	protected final PartConnectivity EMPTYPC = new PartConnectivity(ImmutableSet.of(), ImmutableMultimap.of());

	protected class PartConnectivity {

		final Set<ResourceLocation> connections;
		final Multimap<Pair<VoxelPosition, EnumFacing>, ResourceLocation> connectivity;

		public PartConnectivity(Set<ResourceLocation> connections, Multimap<Pair<VoxelPosition, EnumFacing>, ResourceLocation> connectivity){
			this.connections = connections;
			this.connectivity = connectivity;
		}

	}

	/*
	 * Connections Params
	 */

	public ConnectionsParams gatherConnectionsParams(NetDevice device){
		if(device instanceof Controller.Network) return new ConnectionsParams(ImmutableMap.of(ENERGY, 100, DATA, new DataConnection.Params(192, 500)));
		else return new ConnectionsParams(ImmutableMap.of(ENERGY, 10, DATA, new DataConnection.Params(1, 10)));
	}

	public ConnectionsParams<?> getConnectionParams(NetDevice device){
		Map<Connection, Comparable<?>> params = new HashMap<>();
		forEachConnection(c -> {
			if(device.getConnectionRequirement(c) != null) params.put(c, device.getConnectionRequirement(c));
		});
		return new ConnectionsParams(params);
	}

}
