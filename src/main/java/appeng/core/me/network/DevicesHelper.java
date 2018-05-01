package appeng.core.me.network;

import appeng.api.bootstrap.InitializationComponent;
import appeng.core.AppEng;
import appeng.core.lib.capability.SSCapabilityProviderDelegate;
import appeng.core.me.AppEngME;
import appeng.core.me.api.network.NetDevice;
import appeng.core.me.api.network.PhysicalDevice;
import appeng.core.me.api.network.block.Connection;
import appeng.core.me.api.network.block.ConnectionPassthrough;
import appeng.core.me.api.parts.PartPositionRotation;
import appeng.core.me.api.parts.VoxelPosition;
import appeng.core.me.api.parts.VoxelPositionSide;
import appeng.core.me.api.parts.container.PartInfo;
import appeng.core.me.api.parts.container.PartsAccess;
import appeng.core.me.api.parts.part.Part;
import appeng.core.me.network.connect.ConnectionsParams;
import appeng.core.me.network.connect.DataConnection;
import appeng.core.me.network.connect.SPIntConnection;
import appeng.core.me.parts.part.PartsHelperImpl;
import appeng.core.me.parts.part.device.Controller;
import com.google.common.collect.*;
import com.owens.oobjloader.builder.Mesh;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
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
import java.util.stream.Stream;

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
			Optional<Mesh> allConnectionsMesh = meshLoader.apply("connections");
			ImmutableSet.Builder<ResourceLocation> connectionsBuilder = new ImmutableSet.Builder<>();
			ImmutableMultimap.Builder<VoxelPositionSide, ResourceLocation> connectivityBuilder = new ImmutableMultimap.Builder<>();
			AppEngME.INSTANCE.getDevicesHelper().forEachConnection(connection -> {
				Consumer<VoxelPositionSide> addConnectivity = voxelSide -> {
					connectionsBuilder.add(connection.getId());
					connectivityBuilder.put(voxelSide, connection.getId());
				};
				allConnectionsMesh.ifPresent(cmesh -> forEachInterface(voxelizer.apply(cmesh, g -> g.equals(connection.getId().toString()) || g.equals(AppEng.MODID + ":all")), rootMeshVoxels, addConnectivity));
				meshLoader.apply(connection.getId().toString().replace(":", "-_-")).ifPresent(cmesh -> forEachInterface(voxelizer.apply(cmesh, null), rootMeshVoxels, addConnectivity));
			});
			return Optional.of(new PartConnectivity(connectionsBuilder.build(), connectivityBuilder.build()));
		});
		AppEngME.INSTANCE.getPartsHelper().registerCustomPartDataLoader(WORLDINTERFACELOADER, (part, meshLoader, voxelizer, rootMeshVoxels) -> {
			ImmutableSet.Builder<VoxelPositionSide> interfaces = new ImmutableSet.Builder<>();
			meshLoader.apply("wi").ifPresent(wimesh -> forEachInterface(voxelizer.apply(wimesh, null), rootMeshVoxels, interfaces::add));
			Set<VoxelPositionSide> ifm = interfaces.build();
			return ifm.isEmpty() ? Optional.empty() : Optional.of(new WorldInterface(ifm));
		});

		AppEngME.INSTANCE.registerConnection(ENERGY);
		AppEngME.INSTANCE.registerConnection(DATA);
	}

	protected void forEachInterface(Set<VoxelPosition> inter2faces, Set<VoxelPosition> in, Consumer<VoxelPositionSide> interfaceConsumer){
		inter2faces.stream().filter(in::contains).forEach(inVoxel -> {
			for(EnumFacing side : EnumFacing.values()){
				VoxelPosition outVoxel = inVoxel.offsetLocal(side);
				if(inter2faces.contains(outVoxel) && !in.contains(outVoxel)) interfaceConsumer.accept(new VoxelPositionSide(inVoxel, side));
			}
		});
	}

	/*
	 * Adjacent
	 */

	public Multimap<ConnectionPassthrough, Connection> getAdjacentPTs(World world, Multimap<VoxelPositionSide, Connection> voxels){
		Multimap<ConnectionPassthrough, Connection> adjacentPTs = HashMultimap.create();
		forEachTargetVoxel(voxels, (vDir, cs) -> getConnectionPassthrough(world, vDir, cs).ifPresent(cptCs -> adjacentPTs.putAll(cptCs.getLeft(), cptCs.getRight())));
		return adjacentPTs;
	}

	public Multimap<PhysicalDevice, Connection> getAdjacentDevices(World world, Multimap<VoxelPositionSide, Connection> voxels){
		Multimap<PhysicalDevice, Connection> adjacentDevices = HashMultimap.create();
		forEachTargetVoxel(voxels, (vDir, cs) -> getDevice(world, vDir, cs).ifPresent(phdCs -> adjacentDevices.putAll(phdCs.getLeft(), phdCs.getRight())));
		return adjacentDevices;
	}

	/*
	 * Get at
	 */

	public Optional<Pair<ConnectionPassthrough, Collection<Connection>>> getConnectionPassthrough(World world, VoxelPositionSide posFrom, Collection<Connection> connections){
		MutableObject<Pair<ConnectionPassthrough, Collection<Connection>>> passthrough = new MutableObject<>();
		world.getCapability(PartsHelperImpl.worldPartsAccessCapability, null).getPart(posFrom.getVoxel()).flatMap(PartInfo::getState).ifPresent(s -> {
			if(s instanceof ConnectionPassthrough){
				List<Connection> cs = connections.stream().filter(c -> canConnect(s.getPart(), s.getAssignedPosRot(), c, posFrom)).collect(Collectors.toList());
				if(!cs.isEmpty()) passthrough.setValue(new ImmutablePair<>((ConnectionPassthrough) s, cs));
			}
		});
		return Optional.ofNullable(passthrough.getValue());
	}

	public Optional<Pair<PhysicalDevice, Collection<Connection>>> getDevice(World world, VoxelPositionSide posFrom, Collection<Connection> connections){
		MutableObject<Pair<PhysicalDevice, Collection<Connection>>> device = new MutableObject<>();
		world.getCapability(PartsHelperImpl.worldPartsAccessCapability, null).getPart(posFrom.getVoxel()).flatMap(PartInfo::getState).ifPresent(s -> {
			if(s instanceof PhysicalDevice){
				List<Connection> cs = connections.stream().filter(c -> canConnect(s.getPart(), s.getAssignedPosRot(), c, posFrom)).collect(Collectors.toList());
				if(!cs.isEmpty()) device.setValue(new ImmutablePair<>((PhysicalDevice) s, cs));
			}
		});
		return Optional.ofNullable(device.getValue());
	}

	/*
	 * C->V
	 */

	public void forEachTargetVoxel(Multimap<VoxelPositionSide, Connection> connections, BiConsumer<VoxelPositionSide, Collection<Connection>> targetVoxelConsumer){
		connections.keySet().forEach(vS -> targetVoxelConsumer.accept(vS.flipFromTo(), connections.get(vS)));
	}

	public <T> Optional<Triple<PartPositionRotation, ConnectionsParams, Multimap<VoxelPositionSide, Connection>>> voxels(T t){
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

	public boolean canConnect(Part part, PartPositionRotation positionRotation, Connection connection, VoxelPositionSide voxelSide){
		return getConnectivity(part).connectivity.containsEntry(positionRotation.untransform(voxelSide), connection.getId());
	}

	public Multimap<VoxelPositionSide, Connection> getConnections(Part part, PartPositionRotation positionRotation){
		Multimap<VoxelPositionSide, ResourceLocation> connections = getConnectivity(part).connectivity;
		Multimap<VoxelPositionSide, Connection> rotated = HashMultimap.create();
		connections.keySet().forEach(voxelSide -> rotated.putAll(positionRotation.transform(voxelSide), Iterables.transform(connections.get(voxelSide), AppEngME.INSTANCE.getDevicesHelper()::getConnection)));
		return rotated;
	}

	protected static final ResourceLocation CONNECTIVITYLOADER = new ResourceLocation(AppEng.MODID, "connectivity");

	protected PartConnectivity getConnectivity(Part part){
		return AppEngME.INSTANCE.getPartsHelper().<PartConnectivity>getCustomPartData(part, CONNECTIVITYLOADER).orElse(EMPTYPC);
	}

	protected final PartConnectivity EMPTYPC = new PartConnectivity(ImmutableSet.of(), ImmutableMultimap.of());

	protected class PartConnectivity {

		final Set<ResourceLocation> connections;
		final Multimap<VoxelPositionSide, ResourceLocation> connectivity;

		public PartConnectivity(Set<ResourceLocation> connections, Multimap<VoxelPositionSide, ResourceLocation> connectivity){
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

	/*
	 * World interface
	 */

	public Stream<ICapabilityProvider> getAllWITargetCPs(Part.State part, World world){
		return Stream.concat(getAllWITargetParts(part, world.getCapability(PartsHelperImpl.worldPartsAccessCapability, null)).map(s -> s instanceof ICapabilityProvider ? (ICapabilityProvider) s : null), getAllWITargetBlocks(part).map(gpFrom -> Optional.ofNullable(world.getTileEntity(gpFrom.getLeft())).map(tile -> new SSCapabilityProviderDelegate(tile, gpFrom.getRight())).orElse(null))).filter(Objects::nonNull);
	}

	public <P extends Part<P, S>, S extends Part.State<P, S>> Stream<S> getAllWITargetParts(Part.State part, PartsAccess.Mutable partsAccess){
		return getWorldInterfaces(part.getPart()).map(wi -> wi.interfaces.stream().map(part.getAssignedPosRot()::transform).map(vs -> vs.flipFromTo().getVoxel()).map(tp -> partsAccess.<P, S>getPart(tp).flatMap(PartInfo::getState).orElse(null)).filter(Objects::nonNull).distinct()).orElse(Stream.empty());
	}

	public Stream<? extends Pair<BlockPos, EnumFacing>> getAllWITargetBlocks(Part.State part){
		return getWorldInterfaces(part.getPart()).map(wi -> wi.interfaces.stream().map(part.getAssignedPosRot()::transform).map(p -> Optional.of(p.flipFromTo().getVoxel().getGlobalPosition()).filter(gp -> !gp.equals(p.getVoxel().getGlobalPosition())).map(gp -> new ImmutablePair<>(gp, p.getSide().getOpposite())).orElse(null)).filter(Objects::nonNull).distinct()).orElse(Stream.empty());
	}

	public void forEachWI(Part.State part, Consumer<VoxelPositionSide> vsc){
		getWorldInterfaces(part.getPart()).ifPresent(wi -> wi.interfaces.stream().map(part.getAssignedPosRot()::transform).forEach(vsc));
	}

	protected static final ResourceLocation WORLDINTERFACELOADER = new ResourceLocation(AppEng.MODID, "wi");

	protected Optional<WorldInterface> getWorldInterfaces(Part part){
		return AppEngME.INSTANCE.getPartsHelper().getCustomPartData(part, WORLDINTERFACELOADER);
	}

	protected class WorldInterface {

		final Set<VoxelPositionSide> interfaces;

		public WorldInterface(Set<VoxelPositionSide> interfaces){
			this.interfaces = interfaces;
		}

	}

}
