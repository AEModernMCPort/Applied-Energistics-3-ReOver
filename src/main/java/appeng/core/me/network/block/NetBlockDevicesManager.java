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
import appeng.core.me.network.connect.ConnectionsParams;
import com.google.common.collect.*;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
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

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.ref.WeakReference;
import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class NetBlockDevicesManager implements INBTSerializable<NBTTagCompound> {

	protected NetBlockImpl netBlock;

	public NetBlockDevicesManager(NetBlockImpl netBlock){
		this.netBlock = netBlock;
	}

	/*
	 * Steady-state
	 */

	protected Map<ConnectUUID, WeakReference<ConnectionPassthrough>> passthroughs = new HashMap<>();

	public void notifyPassthroughLoaded(ConnectionPassthrough passthrough){
		passthroughs.put(passthrough.getUUIDForConnectionPassthrough(), new WeakReference<>(passthrough));
	}

	protected Map<DeviceUUID, DeviceInformation> devices = new HashMap<>();

	@Nonnull
	public <N extends NetDevice<N, P>, P extends PhysicalDevice<N, P>> Optional<N> getDevice(DeviceUUID device){
		return Optional.ofNullable(devices.get(device)).map(DeviceInformation::getDevice);
	}

	@Nonnull
	public <N extends NetDevice<N, P>, P extends PhysicalDevice<N, P>> Stream<N> getDevices(){
		return devices.values().stream().map(DeviceInformation::getDevice);
	}

	public <N extends NetDevice<N, P>, P extends PhysicalDevice<N, P>> void removeDestroyedDevice(N device){
		if(device != netBlock.root) devices.remove(device.getUUID()).active.forEach((c, p) -> p.replenish(c, device.getConnectionRequirement(c)));
	}

	protected ConnectionsParams remainingRootParams;
	protected Set<Node> rootAdjacent = new HashSet<>();

	/*
	 * Change
	 */

	public void recalculateAll(World world, PhysicalDevice root){
		long t = System.currentTimeMillis();
		passthroughs.values().forEach(ptRef -> Optional.ofNullable(ptRef.get()).ifPresent(pt -> pt.assignNetBlock(null)));
		passthroughs.clear();
		Multimap<NetDevice, Node> dtr2n = generateGraph(world, root);
		recomputePathways(dtr2n);
		recomputeDSects();
		AppEngME.logger.info("TC took " + (System.currentTimeMillis() - t) + "ms");
		AppEngME.logger.info(passthroughs.size() + " PTs");
		AppEngME.logger.info(dsects.size() + " disjoint sections");
		AppEngME.logger.info(devices.size() + " devices");
	}

	public Optional<PathwayElement> getElement(ConnectUUID cuuid){
		MutableObject<PathwayElement> e = new MutableObject<>(nodes.get(cuuid));
		if(e.getValue() == null) links.parallelStream().filter(link -> link.elements.contains(cuuid)).findAny().ifPresent(e::setValue);
		return Optional.ofNullable(e.getValue());
	}

	public void passthroughCreated(World world, ConnectionPassthrough passthrough){
		long t = System.currentTimeMillis();
		int pts = this.passthroughs.size();
		int dsects = this.dsects.size();
		int devices = this.devices.size();
		Triple<Set<DeviceInformation>, Triple<Set<Node>, Set<Node>, Set<Link>>, Multimap<NetDevice, Node>> recompRedsect = regenGraphSectionPTCreated(world, passthrough);
		recompNewDSects(recompRedsect.getMiddle());
		recompute(recompRedsect.getLeft());
		computePathways(recompRedsect.getRight());
		AppEngME.logger.info("TPC took " + (System.currentTimeMillis() - t) + "ms");
		AppEngME.logger.info(pts + " -> " + this.passthroughs.size() + " PTs");
		AppEngME.logger.info(dsects + " -> " + this.dsects.size() + " disjoint sections");
		AppEngME.logger.info(devices + " -> " + this.devices.size() + " devices");
	}

	public void passthroughDestroyed(ConnectionPassthrough passthrough){
		long t = System.currentTimeMillis();
		int pts = this.passthroughs.size();
		int dsects = this.dsects.size();
		int devices = this.devices.size();
		getElement(passthrough.getUUIDForConnectionPassthrough()).ifPresent(e -> {
			DSect eDSect = getDSect(e);
			Set<DeviceInformation> recomp = destroyPathways(e.pathways);
			regenGraphSectionPTDestroyed(passthrough, e);
			recompElemDSectAndDisconnect(eDSect, recomp);
			this.passthroughs.remove(passthrough.getUUIDForConnectionPassthrough());
			this.recompute(recomp);
		});
		AppEngME.logger.info("TPD took " + (System.currentTimeMillis() - t) + "ms");
		AppEngME.logger.info(pts + " -> " + this.passthroughs.size() + " PTs");
		AppEngME.logger.info(dsects + " -> " + this.dsects.size() + " disjoint sections");
		AppEngME.logger.info(devices + " -> " + this.devices.size() + " devices");
	}

	/*
	 * Destruction
	 */

	protected void onBlockDestroyed(){
		getDevices().forEach(d -> d.switchNetBlock(null));
		notifyPTsUnassign(new HashSet<>(nodes.values()), links);
	}

	protected void notifyPTsUnassign(Collection<Node> nodes, Collection<Link> links){
		notifyPTsUnassign(Stream.concat(nodes.stream().map(node -> node.uuid), links.stream().flatMap(link -> link.elements.stream())));
		nodes.stream().map(n -> n.uuid).forEach(this.nodes::remove);
		this.links.removeAll(links);
	}

	protected void notifyPTsUnassign(Stream<ConnectUUID> pts){
		pts.forEach(cuuid -> Optional.ofNullable(passthroughs.remove(cuuid).get()).ifPresent(pt -> pt.assignNetBlock(null)));
	}

	/*
	 * Graph Gen
	 */

	protected Map<ConnectUUID, Node> nodes = new HashMap<>();
	protected Set<Link> links = new HashSet<>();

	protected Multimap<NetDevice, Node> generateGraph(World world, PhysicalDevice proot){
		long t = System.currentTimeMillis();
		nodes.clear();
		links.clear();
		Multimap<NetDevice, Node> dtr2n = HashMultimap.create();
		Optional<Triple<PartPositionRotation, ConnectionsParams, Multimap<Pair<VoxelPosition, EnumFacing>, Connection>>> oPrCsVs = AppEngME.INSTANCE.getDevicesHelper().voxels(proot);
		if(!oPrCsVs.isPresent()) throw new IllegalArgumentException("Something is very very wrong");
		Set<DeviceUUID> directLinks = new HashSet<>();
		//Graph generation
		Multimap<Pair<VoxelPosition, EnumFacing>, Connection> rootVoxels = oPrCsVs.get().getRight();
		AppEngME.INSTANCE.getDevicesHelper().getAdjacentPTs(world, rootVoxels).keySet().forEach(passthrough -> exploreAdjacent(world, passthrough, null, passthroughs, nodes, links, dtr2n));
		AppEngME.INSTANCE.getDevicesHelper().getAdjacentDevices(world, rootVoxels).keySet().forEach(device -> {
			dtr2n.put(device.getNetworkCounterpart(), null);
			directLinks.add(device.getNetworkCounterpart().getUUID());
		});
		exploreNodes();
		rootAdjacent.addAll(dtr2n.get(netBlock.root));
		AppEngME.logger.info("GC took " + (System.currentTimeMillis() - t) + "ms");
		AppEngME.logger.info(nodes.size() + " nodes");
		AppEngME.logger.info(links.size() + " links");
		return dtr2n;
	}

	protected Triple<Set<DeviceInformation>, Triple<Set<Node>, Set<Node>, Set<Link>>, Multimap<NetDevice, Node>> regenGraphSectionPTCreated(World world, ConnectionPassthrough passthrough){
		Set<Node> affectedNodes = new HashSet<>();
		Set<Node> createdNodes = new HashSet<>();
		Set<Link> createdLinks = new HashSet<>();

		Consumer<Node> affectNode = node -> {
			if(nodes.containsKey(node.uuid)) affectedNodes.add(node);
			else createdNodes.add(node);
		};
		Function<ConnectionPassthrough, Node> createNode = pt -> {
//			Node node = getOrCreateNode(cuuid, pt.getLength(), AppEngME.INSTANCE.getDevicesHelper().getConnectionsParams(pt).get(), ncn -> {});
			Node node = new Node(pt.getUUIDForConnectionPassthrough(), pt.getLength(), AppEngME.INSTANCE.getDevicesHelper().getConnectionsParams(pt).get());
			createdNodes.add(node);
			return node;
		};
		BiFunction<Pair<Node, Node>, Triple<List<ConnectUUID>, Double, ConnectionsParams>, Link> createLinkFull = (fromTo, elementsLengthParams) -> {
			Link link = new Link(fromTo.getLeft(), fromTo.getRight(), elementsLengthParams.getMiddle(), elementsLengthParams.getRight());
			fromTo.getLeft().links.add(link);
			fromTo.getRight().links.add(link);
			link.elements = elementsLengthParams.getLeft();
			createdLinks.add(link);
			affectNode.accept(fromTo.getLeft());
			affectNode.accept(fromTo.getRight());
			return link;
		};
		TriConsumer<Node, Node, List<ConnectUUID>> createLink = (from, to, elements) -> {
			createLinkFull.apply(new ImmutablePair<>(from, to), new ImmutableTriple<>(elements, elements.stream().mapToDouble(cuuid -> passthroughs.get(cuuid).get().getLength()).sum(), elements.isEmpty() ? null : elements.stream().map(cuuid -> AppEngME.INSTANCE.getDevicesHelper().getConnectionsParams(passthroughs.get(cuuid).get()).get()).reduce(ConnectionsParams::intersect).get()));
		};
		Consumer<Link> removeLink = link -> {
			links.remove(link);
			link.from.links.remove(link);
			link.to.links.remove(link);
			affectedNodes.add(link.from);
			affectedNodes.add(link.to);
		};
		long t = System.currentTimeMillis();

		Optional<Triple<PartPositionRotation, ConnectionsParams, Multimap<Pair<VoxelPosition, EnumFacing>, Connection>>> oPrCsVs = AppEngME.INSTANCE.getDevicesHelper().voxels(passthrough);
		if(!oPrCsVs.isPresent()) throw new IllegalArgumentException("Something is very very wrong");
		Triple<PartPositionRotation, ConnectionsParams, Multimap<Pair<VoxelPosition, EnumFacing>, Connection>> prCsVs = oPrCsVs.get();

		Node nnode = createNode.apply(passthrough);
		passthroughs.put(passthrough.getUUIDForConnectionPassthrough(), new WeakReference<>(passthrough));
		passthrough.assignNetBlock(netBlock);

		Map<ConnectionPassthrough, Optional<PathwayElement>> adjacentPTs = new HashMap<>();
		Set<Pathway> pathwaysToDestroy = new HashSet<>();
		AppEngME.INSTANCE.getDevicesHelper().getAdjacentPTs(world, prCsVs.getRight()).keySet().forEach(adj -> {
			Optional<PathwayElement> adjeO = getElement(adj.getUUIDForConnectionPassthrough());
			adjacentPTs.put(adj, adjeO);
			adjeO.ifPresent(adje -> { if(adje instanceof Link) pathwaysToDestroy.addAll(adje.pathways); });
		});
		Multimap<PhysicalDevice, Connection> adjacentDevices = AppEngME.INSTANCE.getDevicesHelper().getAdjacentDevices(world, prCsVs.getRight());

		long dt1 = System.currentTimeMillis() - t;
		Set<DeviceInformation> recomp = destroyPathways(pathwaysToDestroy);
		t = System.currentTimeMillis();

		Map<ConnectUUID, Node> nodesCopy = new HashMap<>(nodes);
		Set<Link> linksCopy = new HashSet<>(links);
		Multimap<NetDevice, Node> dtr2n = HashMultimap.create();
		adjacentDevices.keySet().forEach(device -> {
			nnode.addDevice(device.getNetworkCounterpart(), adjacentDevices.get(device));
			dtr2n.put(device.getNetworkCounterpart(), nnode);
		});
		adjacentPTs.forEach((adjacentPT, pteO) -> {
			if(pteO.isPresent()){
				PathwayElement pte = pteO.get();
				Node node;
				if(pte instanceof Link){
					Link link = (Link) pte;
					removeLink.accept(link);
					int ei = link.elements.indexOf(adjacentPT.getUUIDForConnectionPassthrough());
					if(ei < 0) throw new IllegalArgumentException("Something went very very badly...");
					node = createNode.apply(adjacentPT);
					createLink.accept(link.from, node, link.elements.subList(0, ei));
					createLink.accept(node, link.to, link.elements.subList(ei + 1, link.elements.size()));
				} else {
					node = (Node) pte;
					affectedNodes.add(node);
				}
				createLink.accept(nnode, node, new ArrayList<>());
				//TODO On-place reduction?
			} else nodesExplorer.add(() -> {
				ConnectionPassthrough p = passthrough;
				ConnectionPassthrough c = adjacentPT;
				List<ConnectUUID> es = new ArrayList<>();
				double length = 0;
				ConnectionsParams params = null;
				ExplorationResult explorationResult = exploreAdjacent(world, c, p, passthroughs, nodesCopy, linksCopy, dtr2n);
				while(explorationResult instanceof ExplorationResult.Link){
					es.add(c.getUUIDForConnectionPassthrough());
					length += ((ExplorationResult.Link) explorationResult).length;
					params = ConnectionsParams.intersect(params, ((ExplorationResult.Link) explorationResult).connectionsParams);
					p = c;
					c = ((ExplorationResult.Link) explorationResult).next;
					explorationResult = exploreAdjacent(world, c, p, passthroughs, nodesCopy, linksCopy, dtr2n);
				}
				createLinkFull.apply(new ImmutablePair<>(nnode, ((ExplorationResult.Node) explorationResult).node), new ImmutableTriple<>(es, length, params));
			});
		});
		exploreNodes();
		createdNodes.addAll(Maps.difference(nodesCopy, nodes).entriesOnlyOnLeft().values());
		createdLinks.addAll(Sets.difference(linksCopy, new HashSet<>(links)));

		createdNodes.forEach(cn -> nodes.put(cn.uuid, cn));
		links.addAll(createdLinks);
		AppEngME.logger.info("GC took " + (dt1 + System.currentTimeMillis() - t) + "ms");

		return new ImmutableTriple<>(recomp, new ImmutableTriple<>(affectedNodes, createdNodes, createdLinks), dtr2n);
	}

	protected void regenGraphSectionPTDestroyed(ConnectionPassthrough passthrough, PathwayElement e){
		BiFunction<ConnectUUID, PathwayElement, Node> createNode = (cuuid, pe) -> {
			ConnectionPassthrough pt = passthroughs.get(cuuid).get();
			if(pt == null) throw new IllegalArgumentException("Cannot recalculate paths when the entirety of block is not loaded!");
			Node node = nodes.get(cuuid);
			if(node == null) nodes.put(cuuid, node = new Node(cuuid, pt.getLength(), AppEngME.INSTANCE.getDevicesHelper().getConnectionsParams(pt).get()));
			getDSect(pe).nodes.add(node);
			return node;
		};
		Consumer<Node> removeNode = node -> {
			nodes.remove(node.uuid);
			getDSect(node).nodes.remove(node);
		};
		TriConsumer<Node, Node, List<ConnectUUID>> createLink = (from, to, elements) -> {
			Link link = new Link(from, to, elements.stream().mapToDouble(cuuid -> passthroughs.get(cuuid).get().getLength()).sum(), elements.isEmpty() ? null : elements.stream().map(cuuid -> AppEngME.INSTANCE.getDevicesHelper().getConnectionsParams(passthroughs.get(cuuid).get()).get()).reduce(ConnectionsParams::intersect).get());
			links.add(link);
			from.links.add(link);
			to.links.add(link);
			link.elements = elements;
			getDSect(from).links.add(link);
		};
		Consumer<Link> removeLink = link -> {
			links.remove(link);
			link.from.links.remove(link);
			link.to.links.remove(link);
			getDSect(link).links.remove(link);
		};
		long t = System.currentTimeMillis();
		if(e instanceof Node){
			Node node = (Node) e;
			removeNode.accept(node);
			ArrayList<Link> links = new ArrayList<>(node.links);
			links.forEach(link -> {
				removeLink.accept(link);
				if(!link.elements.isEmpty()){
					if(link.from == node){
						Node to = link.to;
						Node from = createNode.apply(link.elements.get(0), to);
						List<ConnectUUID> elements = link.elements.subList(1, link.elements.size());
						createLink.accept(from, to, elements);
					}
					if(link.to == node){
						Node from = link.from;
						Node to = createNode.apply(link.elements.get(link.elements.size() - 1), from);
						List<ConnectUUID> elements = link.elements.subList(0, link.elements.size() - 1);
						createLink.accept(from, to, elements);
					}
				}
			});
		}
		if(e instanceof Link){
			Link link = (Link) e;
			removeLink.accept(link);
			int ei = link.elements.indexOf(passthrough.getUUIDForConnectionPassthrough());
			if(ei > 0){
				Node from = link.from;
				Node to = createNode.apply(link.elements.get(ei - 1), from);
				List<ConnectUUID> elements = link.elements.subList(0, ei - 1);
				createLink.accept(from, to, elements);
			}
			if(ei < link.elements.size() - 1){
				Node to = link.to;
				Node from = createNode.apply(link.elements.get(ei + 1), to);
				List<ConnectUUID> elements = link.elements.subList(ei + 2, link.elements.size());
				createLink.accept(from, to, elements);
			}
		}
		AppEngME.logger.info("GC took " + (System.currentTimeMillis() - t) + "ms");
	}

	protected Queue<Runnable> nodesExplorer = new LinkedList<>();

	protected void exploreNodes(){
		while(nodesExplorer.peek() != null) nodesExplorer.poll().run();
	}

	protected ExplorationResult exploreAdjacent(World world, ConnectionPassthrough current, ConnectionPassthrough previous, Map<ConnectUUID, WeakReference<ConnectionPassthrough>> passthroughs, Map<ConnectUUID, Node> nodes, Collection<Link> links, Multimap<NetDevice, Node> dtr2n){
		Consumer<ConnectionPassthrough> addPassthrough = passthrough -> {
			passthroughs.put(passthrough.getUUIDForConnectionPassthrough(), new WeakReference<>(passthrough));
			passthrough.assignNetBlock(netBlock);
		};
		BiFunction<Pair<Node, Node>, Triple<List<ConnectUUID>, Double, ConnectionsParams>,Link> createLink = (fromTo, elementsLengthParams) -> {
			Link link = new Link(fromTo.getLeft(), fromTo.getRight(), elementsLengthParams.getMiddle(), elementsLengthParams.getRight());
			links.add(link);
			fromTo.getLeft().links.add(link);
			fromTo.getRight().links.add(link);
			link.elements = elementsLengthParams.getLeft();
			return link;
		};
		BiFunction<Triple<ConnectUUID, Double, ConnectionsParams>, Consumer<Node>, Node> getOrCreateNode = (uuidLengthParams, newlyCreated) -> {
			Node node = nodes.get(uuidLengthParams.getLeft());
			if(node == null){
				nodes.put(uuidLengthParams.getLeft(), node = new Node(uuidLengthParams.getLeft(), uuidLengthParams.getMiddle(), uuidLengthParams.getRight()));
				newlyCreated.accept(node);
			}
			return node;
		};

		final MutableObject<ExplorationResult> res = new MutableObject<>();
		final ConnectUUID currentCUUID = current.getUUIDForConnectionPassthrough();
		addPassthrough.accept(current);
		AppEngME.INSTANCE.getDevicesHelper().voxels(current).ifPresent(prCsVs -> {
			Multimap<ConnectionPassthrough, Connection> adjacentPTs = AppEngME.INSTANCE.getDevicesHelper().getAdjacentPTs(world, prCsVs.getRight());
			adjacentPTs.removeAll(previous);
			Multimap<PhysicalDevice, Connection> adjacentDevices = AppEngME.INSTANCE.getDevicesHelper().getAdjacentDevices(world, prCsVs.getRight());
			if(adjacentPTs.keySet().size() == 1 && adjacentDevices.isEmpty()){
				//return link;
				ConnectionPassthrough adjN0 = adjacentPTs.keySet().toArray(new ConnectionPassthrough[1])[0];
				res.setValue(new ExplorationResult.Link(prCsVs.getMiddle(), current.getLength(), adjN0));
			} else {
				//return node;
				Node resNode = getOrCreateNode.apply(new ImmutableTriple<>(currentCUUID, current.getLength(), prCsVs.getMiddle()), nnode -> nodesExplorer.add(() -> {
					adjacentDevices.keySet().forEach(device -> {
						nnode.addDevice(device.getNetworkCounterpart(), adjacentDevices.get(device));
						dtr2n.put(device.getNetworkCounterpart(), nnode);
					});
					adjacentPTs.keySet().forEach(adjacentPT -> {
						if(!passthroughs.containsKey(adjacentPT.getUUIDForConnectionPassthrough())){
							ConnectionPassthrough p = current;
							ConnectionPassthrough c = adjacentPT;
							List<ConnectUUID> es = new ArrayList<>();
							double length = 0;
							ConnectionsParams params = null;
							ExplorationResult explorationResult = exploreAdjacent(world, c, p, passthroughs, nodes, links, dtr2n);
							while(explorationResult instanceof ExplorationResult.Link){
								es.add(c.getUUIDForConnectionPassthrough());
								length += ((ExplorationResult.Link) explorationResult).length;
								params = ConnectionsParams.intersect(params, ((ExplorationResult.Link) explorationResult).connectionsParams);
								p = c;
								c = ((ExplorationResult.Link) explorationResult).next;
								explorationResult = exploreAdjacent(world, c, p, passthroughs, nodes, links, dtr2n);
							}
							createLink.apply(new ImmutablePair<>(nnode, ((ExplorationResult.Node) explorationResult).node), new ImmutableTriple<>(es, length, params));
						} else Optional.ofNullable(nodes.get(adjacentPT.getUUIDForConnectionPassthrough())).ifPresent(adjENode -> {
							if(adjENode.links.stream().noneMatch(link -> (link.from == nnode && link.to == adjENode) || (link.from == adjENode && link.to == nnode)))
								createLink.apply(new ImmutablePair<>(nnode, adjENode), new ImmutableTriple<>(new ArrayList<>(), 0d, null));
						});
					});
				}));
				res.setValue(new ExplorationResult.Node(resNode));
			}
		});
		return res.getValue();
	}

	protected static abstract class ExplorationResult {

		static class Link extends ExplorationResult {

			protected final ConnectionsParams connectionsParams;
			protected final double length;
			protected final ConnectionPassthrough next;

			public Link(ConnectionsParams connectionsParams, double length, ConnectionPassthrough next){
				this.connectionsParams = connectionsParams;
				this.length = length;
				this.next = next;
			}
		}

		static class Node extends ExplorationResult {

			protected final NetBlockDevicesManager.Node node;

			public Node(NetBlockDevicesManager.Node node){
				this.node = node;
			}
		}

	}

	/*
	 * Links, Nodes, Devices...
	 */

	public class PathwayElement {

		protected ConnectionsParams params;
		protected ConnectionsParams leftover;
		protected double length;
		protected List<Pathway> pathways = new ArrayList<>();

		PathwayElement(){
		}

		public PathwayElement(ConnectionsParams params, double length){
			this.params = params;
			this.leftover = params;
			this.length = length;
		}

		public ConnectionsParams getLeftoverParams(){
			return leftover;
		}

		public double getLength(){
			return length;
		}

		public <P extends Comparable<P>> void consume(Connection<P, ?> connection, P params){
			if(leftover != null) leftover = ConnectionsParams.subtractContained(leftover, new ConnectionsParams<>(ImmutableMap.of(connection, params)));
		}

		public <P extends Comparable<P>> void replenish(Connection<P, ?> connection, P params){
			if(leftover != null) leftover = ConnectionsParams.addContained(leftover, new ConnectionsParams<>(ImmutableMap.of(connection, params)));
		}

		/*
		 * IO
		 */

		protected NBTTagCompound serializeNBT(){
			NBTTagCompound nbt = new NBTTagCompound();
			if(params != null) nbt.setTag("params", AppEngME.INSTANCE.getNBDIO().serializeConnectionsParams(params));
			if(leftover != null) nbt.setTag("leftover", AppEngME.INSTANCE.getNBDIO().serializeConnectionsParams(leftover));
			nbt.setDouble("length", length);
			return nbt;
		}

		protected void deserializeNBT(NBTTagCompound nbt){
			if(nbt.hasKey("params")) params = AppEngME.INSTANCE.getNBDIO().deserializeConnectionsParams(nbt.getCompoundTag("params"));
			if(nbt.hasKey("leftover")) leftover = AppEngME.INSTANCE.getNBDIO().deserializeConnectionsParams(nbt.getCompoundTag("leftover"));
			length = nbt.getDouble("length");
		}

	}

	protected class Node extends PathwayElement {

		protected final ConnectUUID uuid;
		protected List<Link> links = new ArrayList<>();
		protected Multimap<DeviceUUID, Connection> devices = HashMultimap.create();

		Node(ConnectUUID uuid){
			this.uuid = uuid;
		}

		public Node(ConnectUUID uuid, double length, ConnectionsParams params){
			super(params, length);
			this.uuid = uuid;
			this.length = length;
			this.params = params;
		}

		void addDevice(NetDevice device, Collection<Connection> connections){
			this.devices.putAll(device.getUUID(), connections);
		}

		/*
		 * IO
		 */

		protected NBTTagCompound serializeNBT(Map<Link, Integer> l2i){
			NBTTagCompound nbt = super.serializeNBT();
			nbt.setIntArray("links", this.links.stream().mapToInt(l2i::get).toArray());
			NBTTagList devices = new NBTTagList();
			this.devices.forEach((duuid, c) -> {
				NBTTagCompound tag = new NBTTagCompound();
				tag.setTag("duuid", duuid.serializeNBT());
				tag.setString("connection", c.getId().toString());
				devices.appendTag(tag);
			});
			nbt.setTag("devices", devices);
			return nbt;
		}

		protected void deserializeNBT(NBTTagCompound nbt, Map<Integer, Link> i2l){
			super.deserializeNBT(nbt);
			this.links = Arrays.stream(nbt.getIntArray("links")).mapToObj(i2l::get).collect(Collectors.toList());
			this.devices.clear();
			((NBTTagList) nbt.getTag("devices")).forEach(base -> {
				NBTTagCompound tag = (NBTTagCompound) base;
				devices.put(DeviceUUID.fromNBT(tag.getCompoundTag("duuid")), AppEngME.INSTANCE.getDevicesHelper().getConnection(new ResourceLocation(tag.getString("connection"))));
			});
		}

		/*
		 * EH2S
		 */

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
		/**
		 * All PTs through which this link passes. => Excludes from & to.
		 */
		protected List<ConnectUUID> elements;

		Link(){
		}

		public Link(Node from, Node to, double length, ConnectionsParams params){
			super(params, length);
			this.from = from;
			this.to = to;
			this.length = length;
			this.params = params;
		}

		/*
		 * IO
		 */

		protected NBTTagCompound serializeNBT(){
			NBTTagCompound nbt = super.serializeNBT();
			nbt.setTag("from", from.uuid.serializeNBT());
			nbt.setTag("to", to.uuid.serializeNBT());
			NBTTagList elements = new NBTTagList();
			for(ConnectUUID cuuid : this.elements) elements.appendTag(cuuid.serializeNBT());
			nbt.setTag("elements", elements);
			return nbt;
		}

		protected void deserializeNBT(NBTTagCompound nbt){
			super.deserializeNBT(nbt);
			from = nodes.get(ConnectUUID.fromNBT(nbt.getCompoundTag("from")));
			to = nodes.get(ConnectUUID.fromNBT(nbt.getCompoundTag("to")));
			this.elements = new ArrayList<>();
			for(NBTTagCompound base : (Iterable<NBTTagCompound>) nbt.getTag("elements")) this.elements.add(ConnectUUID.fromNBT(base));
		}

		/*
		 * EH2S
		 */

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

	/*
	 * Graph reduction
	 * TODO
	 */

	/*
	protected void reduceNodesToLinks(){
		long t = System.currentTimeMillis();
		int c = 0;
		Optional<Node> next = nodes.values().stream().filter(n -> n.links.size() == 2 && n.devices.isEmpty()).findAny();
		while(next.isPresent()){
			Node node = next.get();
			Link al1 = node.links.get(0);
			Link al2 = node.links.get(1);
			Link l1 = al1.to == node ? al1 : al2;
			Link l2 = al1.from == node ? al1 : al2;
			removeNode.accept(node);
			removeLink.accept(l1);
			removeLink.accept(l2);
			ArrayList<ConnectUUID> elements = new ArrayList<>();
			elements.addAll(l1.elements);
			elements.add(node.uuid);
			elements.addAll(l2.elements);
			createLink.accept(l1.from, l2.to, elements);

			c++;
			next = nodes.values().stream().filter(n -> n.links.size() == 2 && n.devices.isEmpty()).findAny();
		}
		AppEngME.logger.info("NR took " + (System.currentTimeMillis() - t) + "ms");
		AppEngME.logger.info("Reduced " + c + " nodes");
	}
	*/

	/*
	 * Devices
	 */

	protected void recomputePathways(Multimap<NetDevice, Node> dtr2n){
		devices.values().stream().filter(d -> d.device != netBlock.root).forEach(info -> info.device.switchNetBlock(null));
		devices.clear();
		remainingRootParams = AppEngME.INSTANCE.getDevicesHelper().getConnectionParams(netBlock.root);
		compute(netBlock.root, rootAdjacent.stream());
		computePathways(dtr2n);
	}

	protected void computePathways(Multimap<NetDevice, Node> dtr2n){
		long t = System.currentTimeMillis();
		dtr2n.keySet().forEach(device -> compute(device, dtr2n.get(device).stream()));
		AppEngME.logger.info("CP took " + (System.currentTimeMillis() - t) + "ms");
	}

	protected Set<DeviceInformation> destroyPathways(Collection<Pathway> toDestroy){
		long t = System.currentTimeMillis();
		Set<DeviceInformation> recalc = new HashSet<>();
		new ArrayList<>(toDestroy).forEach(pathway -> {
			pathway.elements.forEach(pE -> pE.pathways.remove(pathway));
			DeviceInformation concernedDevice = pathway.device;
			concernedDevice.remove(pathway);
			if(pathway.active) recalc.add(concernedDevice);
		});
		AppEngME.logger.info("PD took " + (System.currentTimeMillis() - t) + "ms");
		return recalc;
	}

	protected void compute(NetDevice device, Stream<Node> adj){
		Map<Connection, Pathway> active;
		Set<Pathway> dormant;
		if(device != netBlock.root){
			List<Pathway> pathways = new ArrayList<>();
			adj.filter(Objects::nonNull)/*FIXME Implement direct links root-device*/.forEach(node -> nextStep(pathways, node, new ArrayList<>()));
			Multimap<Connection, Pathway> c2ps = HashMultimap.create();
			pathways.forEach(pathway -> AppEngME.INSTANCE.getDevicesHelper().forEachConnection(connection -> {
				Comparable req = device.getConnectionRequirement(connection);
				Comparable provided = pathway.computeParams(remainingRootParams).getParam(connection);
				if(req != null && provided != null){
					Comparable decayed = connection.mul(provided, AppEngME.INSTANCE.config.lossFactor(connection, pathway.length));
					if(decayed.compareTo(req) >= 0) c2ps.put(connection, pathway);
				}
			}));
			active = new HashMap<>();
			dormant = new HashSet<>();
			AppEngME.INSTANCE.getDevicesHelper().forEachConnection(connection -> c2ps.get(connection).stream().sorted(Comparator.comparingDouble(Pathway::getLength)).forEachOrdered(pathway -> {
				if(!active.containsKey(connection)) active.put(connection, pathway);
				else dormant.add(pathway);
			}));
			if(device.fulfill(active.keySet())) active.forEach((c, p) -> p.markActive(true).consume(c, device.getConnectionRequirement(c)));
			else {
				active.values().forEach(dormant::add);
				active.clear();
			}
			Stream.concat(active.values().stream(), dormant.stream()).forEach(Pathway::assignToElements);
		} else {
			active = null;
			dormant = null;
		}
		device.switchNetBlock(netBlock);
		devices.put(device.getUUID(), new DeviceInformation(device, active, dormant));
	}

	protected void recompute(Set<DeviceInformation> devices){
		long t = System.currentTimeMillis();
		//TODO Use dormant paths. I mean, we don't keep them for nothing...
		devices.forEach(info -> compute(info.device, nodes.values().stream().filter(node -> node.devices.containsKey(info.device.getUUID()))));
		AppEngME.logger.info("DC took " + (System.currentTimeMillis() - t) + "ms");
	}

	protected void nextStep(Collection<Pathway> pathways, PathwayElement current, List<PathwayElement> previous){
		if(current instanceof Link){
			Link link = (Link) current;
			if(!previous.contains(link.from)) nextStep(pathways, link.from, addCurrent(previous, current));
			if(!previous.contains(link.to)) nextStep(pathways, link.to, addCurrent(previous, current));
		}
		if(current instanceof Node){
			Node node = (Node) current;
			if(node.devices.containsKey(netBlock.root.getUUID())) pathways.add(new Pathway(addCurrent(previous, current)));
			node.links.stream().filter(link -> !previous.contains(link)).forEach(link -> nextStep(pathways, link, addCurrent(previous, current)));
		}
	}

	protected List<PathwayElement> addCurrent(List<PathwayElement> previous, PathwayElement current){
		List<PathwayElement> list = new ArrayList<>(previous);
		list.add(current);
		return list;
	}

	class DeviceInformation {

		NetDevice device;
		Map<Connection, Pathway> active;
		Set<Pathway> dormant;

		public DeviceInformation(@Nonnull NetDevice device, @Nullable Map<Connection, Pathway> active, @Nullable Set<Pathway> dormant){
			this.device = device;
			this.active = active;
			this.dormant = dormant;
			//In any case, they're either both null or both not null.
			if(active != null && dormant != null) Stream.concat(active.values().stream(), dormant.stream()).forEach(pathway -> pathway.device = this);
		}

		public <N extends NetDevice<N, P>, P extends PhysicalDevice<N, P>> N getDevice(){
			return (N) device;
		}

		protected void remove(Pathway pathway){
			if(pathway.active) active.entrySet().removeIf(e -> {
					if(e.getValue() == pathway){
						pathway.replenish(e.getKey(), device.getConnectionRequirement(e.getKey()));
						return true;
					}
					return false;
				});
			else dormant.remove(pathway);
		}

		protected NBTTagCompound serializeNBT(Map<Link, Integer> l2i){
			NBTTagCompound nbt = new NBTTagCompound();
			nbt.setTag("device", AppEngME.INSTANCE.getNBDIO().<NetDevice, PhysicalDevice>serializeDeviceWithArgs(device));
			if(active != null){
				NBTTagList active = new NBTTagList();
				this.active.forEach((c, p) -> {
					NBTTagCompound tag = new NBTTagCompound();
					tag.setString("connection", c.getId().toString());
					tag.setTag("pathway", p.serializeNBT(l2i));
					active.appendTag(tag);
				});
				nbt.setTag("active", active);
			}
			if(dormant != null){
				NBTTagList dormant = new NBTTagList();
				this.dormant.forEach(p -> dormant.appendTag(p.serializeNBT(l2i)));
				nbt.setTag("dormant", dormant);
			}
			return nbt;
		}

	}

	protected void deserializeDeviceInfoNBT(NBTTagCompound nbt, Map<Integer, Link> i2l){
		Pair<DeviceUUID, NetDevice> device = AppEngME.INSTANCE.getNBDIO().<NetDevice, PhysicalDevice>deserializeDeviceWithArgs(netBlock, nbt.getCompoundTag("device"));
		Map<Connection, Pathway> active = nbt.hasKey("active") ? new HashMap<>() : null;
		if(active != null) ((NBTTagList) nbt.getTag("active")).forEach(base -> {
			NBTTagCompound tag = (NBTTagCompound) base;
			Pathway pathway = new Pathway();
			pathway.deserializeNBT(tag.getCompoundTag("pathway"), i2l);
			active.put(AppEngME.INSTANCE.getDevicesHelper().getConnection(new ResourceLocation(tag.getString("connection"))), pathway.markActive(true));
		});
		Set<Pathway> dormant = nbt.hasKey("dormant") ? new HashSet<>() : null;
		if(dormant != null) ((NBTTagList) nbt.getTag("dormant")).forEach(base -> {
			Pathway pathway = new Pathway();
			pathway.deserializeNBT((NBTTagCompound) base, i2l);
			dormant.add(pathway);
		});
		devices.put(device.getLeft(), new DeviceInformation(device.getRight(), active, dormant));
	}

	protected class Pathway {

		protected List<PathwayElement> elements;
		protected double length;
		protected ConnectionsParams params;
		protected DeviceInformation device;
		protected boolean active = false;

		Pathway(){
		}

		public Pathway(List<PathwayElement> elements){
			this.elements = elements;
			this.length = elements.stream().mapToDouble(PathwayElement::getLength).sum();
		}

		protected ConnectionsParams computeParams(ConnectionsParams rootParams){
			return params = elements.stream().map(PathwayElement::getLeftoverParams).reduce(rootParams, ConnectionsParams::intersect);
		}

		public double getLength(){
			return length;
		}

		public <P extends Comparable<P>> void consume(Connection<P, ?> connection, P params){
			remainingRootParams = ConnectionsParams.subtractContained(remainingRootParams, new ConnectionsParams<>(ImmutableMap.of(connection, params)));
			elements.forEach(e -> e.consume(connection, params));
		}

		protected <P extends Comparable<P>> void replenish(Connection<P, ?> connection, P params){
			remainingRootParams = ConnectionsParams.addContained(remainingRootParams, new ConnectionsParams<>(ImmutableMap.of(connection, params)));
			elements.forEach(e -> e.replenish(connection, params));
		}

		protected Pathway markActive(boolean active){
			this.active = active;
			return this;
		}

		protected void assignToElements(){
			elements.forEach(e -> e.pathways.add(this));
		}

		/*
		 * IO
		 */

		protected NBTTagCompound serializeNBT(Map<Link, Integer> l2i){
			NBTTagCompound nbt = new NBTTagCompound();
			NBTTagList elements = new NBTTagList();
			for(PathwayElement e : this.elements){
				NBTTagCompound tag = new NBTTagCompound();
				if(e instanceof Node) tag.setTag("node", ((Node) e).uuid.serializeNBT());
				if(e instanceof Link) tag.setInteger("link", l2i.get(e));
				elements.appendTag(tag);
			}
			nbt.setTag("elements", elements);
			nbt.setTag("params", AppEngME.INSTANCE.getNBDIO().serializeConnectionsParams(params));
			nbt.setDouble("length", length);
			return nbt;
		}

		protected void deserializeNBT(NBTTagCompound nbt, Map<Integer, Link> i2l){
			this.elements = new ArrayList<>();
			for(NBTTagCompound tag : (Iterable<NBTTagCompound>) nbt.getTag("elements")){
				if(tag.hasKey("node")) elements.add(nodes.get(ConnectUUID.fromNBT(tag.getCompoundTag("node"))));
				if(tag.hasKey("link")) elements.add(i2l.get(tag.getInteger("link")));
			}
			assignToElements();
			params = AppEngME.INSTANCE.getNBDIO().deserializeConnectionsParams(nbt.getCompoundTag("params"));
			length = nbt.getDouble("length");
		}

	}

	/*
	 * DSects
	 */

	protected Set<DSect> dsects = new HashSet<>();

	public DSect getDSect(PathwayElement e){
		return dsects.stream().filter(e instanceof Node ? (ds -> ds.nodes.contains(e)) : (ds -> ds.links.contains(e)))./*There can only be one*/findAny()./*And there must be*/get();
	}

	protected void recomputeDSects(){
		long t = System.currentTimeMillis();
		dsects = computeDSects(this.nodes.values());
		AppEngME.logger.info("CD took " + (System.currentTimeMillis() - t) + "ms");
	}

	protected void recompNewDSects(Triple<Set<Node>, Set<Node>, Set<Link>> affectedCreatedCreated){
		long t = System.currentTimeMillis();
		Set<DSect> affectedDSects = dsects.stream().filter(dsect -> !Collections.disjoint(affectedCreatedCreated.getLeft(), dsect.nodes)).collect(Collectors.toSet());
		this.dsects.removeAll(affectedDSects);
		this.dsects.addAll(computeDSects(Stream.concat(affectedCreatedCreated.getMiddle().stream(), affectedDSects.stream().flatMap(dsect -> dsect.nodes.stream())).filter(node -> nodes.containsKey(node.uuid)).collect(Collectors.toSet())));
		AppEngME.logger.info("CD took " + (System.currentTimeMillis() - t) + "ms");
	}

	protected void recompElemDSectAndDisconnect(DSect eDSect, Set<DeviceInformation> recalcQ){
		long t = System.currentTimeMillis();
		this.dsects.remove(eDSect);
		Set<DeviceUUID> remove = new HashSet<>();
		Set<DeviceUUID> keep = new HashSet<>();
		computeDSects(eDSect.nodes).forEach(nndSect -> {
			if(Collections.disjoint(rootAdjacent, nndSect.nodes)){
				nndSect.nodes.forEach(node -> remove.addAll(node.devices.keySet()));
				notifyPTsUnassign(nndSect.nodes, nndSect.links);
			} else {
				nndSect.nodes.forEach(node -> keep.addAll(node.devices.keySet()));
				this.dsects.add(nndSect);
			}
		});
		remove.removeAll(keep);
		recalcQ.removeIf(d -> remove.contains(d.device.getUUID()));
		remove.forEach(d -> this.devices.remove(d).device.switchNetBlock(null));
		AppEngME.logger.info("CD took " + (System.currentTimeMillis() - t) + "ms");
	}

	protected Set<DSect> computeDSects(Collection<Node> nodesToDSect){
		Set<DSect> dsects = new HashSet<>();
		Set<Node> nodes = new HashSet<>(nodesToDSect);
		while(!nodes.isEmpty()){
			Node next = nodes.stream().findAny().get();
			Set<Node> nextNodes = new HashSet<>();
			Set<Link> nextLinks = new HashSet<>();
			floodfill(next, nextNodes, nextLinks);
			floodfill();
			nodes.removeAll(nextNodes);
			dsects.add(new DSect(new ArrayList<>(nextNodes), new ArrayList<>(nextLinks)));
		}
		return dsects;
	}

	protected Queue<Runnable> ffq = new LinkedList<>();

	protected void floodfill(){
		while(ffq.peek() != null) ffq.poll().run();
	}

	protected void floodfill(Node next, Set<Node> nodes, Set<Link> links){
		if(nodes.contains(next)) return;
		nodes.add(next);
		next.links.forEach(link -> {
			if(links.contains(link)) return;
			links.add(link);
			if(link.from != next) ffq.add(() -> floodfill(link.from, nodes, links));
			if(link.to != next) ffq.add(() -> floodfill(link.to, nodes, links));
		});
	}

	class DSect {

		protected List<Node> nodes;
		protected List<Link> links;

		DSect(){
		}

		public DSect(List<Node> nodes, List<Link> links){
			this.nodes = nodes;
			this.links = links;
		}

		protected NBTTagCompound serializeNBT(Map<Link, Integer> l2i){
			NBTTagCompound nbt = new NBTTagCompound();
			NBTTagList nodes = new NBTTagList();
			this.nodes.forEach(node -> nodes.appendTag(node.uuid.serializeNBT()));
			nbt.setTag("nodes", nodes);
			nbt.setIntArray("links", this.links.stream().mapToInt(l2i::get).toArray());
			return nbt;
		}

		protected void deserializeNBT(NBTTagCompound nbt, Map<Integer, Link> i2l){
			this.nodes = StreamSupport.stream(((Iterable<NBTTagCompound>) nbt.getTag("nodes")).spliterator(), false).map(ConnectUUID::fromNBT).map(NetBlockDevicesManager.this.nodes::get).collect(Collectors.toList());
			this.links = Arrays.stream(nbt.getIntArray("links")).mapToObj(i2l::get).collect(Collectors.toList());
		}
	}

	/*
	 * IO
	 */

	@Override
	public NBTTagCompound serializeNBT(){
		NBTTagCompound nbt = new NBTTagCompound();
		Map<Link, Integer> l2i = new HashMap<>();
		{
			NBTTagList links = new NBTTagList();
			int next = 0;
			Iterator<Link> lit = this.links.iterator();
			while(lit.hasNext()){
				Link link = lit.next();
				l2i.put(link, next);
				links.appendTag(link.serializeNBT());
				next++;
			}
			nbt.setTag("links", links);
		}
		{
			NBTTagList nodes = new NBTTagList();
			this.nodes.forEach((uuid, node) -> {
				NBTTagCompound tag = new NBTTagCompound();
				tag.setTag("cuuid", uuid.serializeNBT());
				tag.setTag("node", node.serializeNBT(l2i));
				nodes.appendTag(tag);
			});
			nbt.setTag("nodes", nodes);
		}
		{
			NBTTagList rootAdjacent = new NBTTagList();
			this.rootAdjacent.forEach(node -> rootAdjacent.appendTag(node.uuid.serializeNBT()));
			nbt.setTag("rootadj", rootAdjacent);
		}
		{
			NBTTagList devices = new NBTTagList();
			this.devices.values().forEach(info -> devices.appendTag(info.serializeNBT(l2i)));
			nbt.setTag("devices", devices);
		}
		{
			NBTTagList dsects = new NBTTagList();
			this.dsects.forEach(dSect -> dsects.appendTag(dSect.serializeNBT(l2i)));
			nbt.setTag("dsects", dsects);
		}
		return nbt;
	}

	@Override
	public void deserializeNBT(NBTTagCompound nbt){
		this.links.clear();
		Map<Integer, Link> i2l = new HashMap<>();
		Queue<Pair<Link, NBTTagCompound>> ldq = new LinkedList<>();
		{
			int next = 0;
			for(NBTTagCompound tag : (Iterable<NBTTagCompound>) nbt.getTag("links")){
				Link link = new Link();
				ldq.add(new ImmutablePair<>(link, tag));
				i2l.put(next, link);
				next++;
			}
		}
		this.nodes.clear();
		((NBTTagList) nbt.getTag("nodes")).forEach(base -> {
			NBTTagCompound tag = (NBTTagCompound) base;
			ConnectUUID cuuid = ConnectUUID.fromNBT(tag.getCompoundTag("cuuid"));
			Node node = new Node(cuuid);
			node.deserializeNBT(tag.getCompoundTag("node"), i2l);
			this.nodes.put(cuuid, node);
		});
		this.rootAdjacent = StreamSupport.stream(((Iterable<NBTTagCompound>) nbt.getTag("rootadj")).spliterator(), false).map(ConnectUUID::fromNBT).map(this.nodes::get).collect(Collectors.toSet());
		this.devices.clear();
		((NBTTagList) nbt.getTag("devices")).forEach(base -> deserializeDeviceInfoNBT((NBTTagCompound) base, i2l));
		this.dsects.clear();
		((Iterable<NBTTagCompound>) nbt.getTag("dsects")).forEach(tag -> {
			DSect dSect = new DSect();
			dSect.deserializeNBT(tag, i2l);
			dsects.add(dSect);
		});
		ldq.forEach(lt -> {
			Link link = lt.getKey();
			link.deserializeNBT(lt.getValue());
			links.add(link);
		});
	}

}
