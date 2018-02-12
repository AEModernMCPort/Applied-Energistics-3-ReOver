package appeng.core.skyfall.skyobject;

import appeng.core.AppEng;
import appeng.core.lib.capability.SingleCapabilityProvider;
import appeng.core.skyfall.AppEngSkyfall;
import appeng.core.skyfall.api.skyobject.Skyobject;
import appeng.core.skyfall.api.skyobject.SkyobjectPhysics;
import appeng.core.skyfall.api.skyobject.SkyobjectProvider;
import appeng.core.skyfall.api.skyobject.SkyobjectsManager;
import appeng.core.skyfall.config.SkyfallConfig;
import appeng.core.skyfall.net.SkyobjectMessage;
import com.google.common.base.Predicates;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Stream;

@Mod.EventBusSubscriber(modid = AppEng.MODID)
public class SkyobjectsManagerImpl implements SkyobjectsManager, SkyobjectsManager.WithDefaultSyncSupport {

	protected static final ExecutorService GENERATORSERVICE = Executors.newCachedThreadPool();

	@SubscribeEvent
	public static void attachToWorld(AttachCapabilitiesEvent<World> event){
		event.addCapability(new ResourceLocation(AppEng.MODID, "skyobjects_manager"), new SingleCapabilityProvider.Serializeable<>(AppEngSkyfall.skyobjectsManagerCapability, new SkyobjectsManagerImpl()));
	}

	@SubscribeEvent
	public static void tickSkyobjects(TickEvent.WorldTickEvent event){
		if(event.phase == TickEvent.Phase.END) event.world.getCapability(AppEngSkyfall.skyobjectsManagerCapability, null).tick(event.world);
	}

	@SubscribeEvent
	public static void syncWithNewPlayer(EntityJoinWorldEvent event){
		if(!event.getWorld().isRemote && event.getEntity() instanceof EntityPlayer) Optional.ofNullable(event.getWorld().getCapability(AppEngSkyfall.skyobjectsManagerCapability, null)).map(manager -> manager instanceof SkyobjectsManager.WithDefaultSyncSupport ? (SkyobjectsManager.WithDefaultSyncSupport) manager : null).ifPresent(manager -> manager.sendAll((EntityPlayerMP) event.getEntity()));
	}

	@SubscribeEvent
	public static void applyGravity(SkyobjectPhysics.GatherForcesEvent event){
		if(event.getSkyobject() instanceof Skyobject.PhysicsDriven){
			SkyfallConfig config = AppEngSkyfall.INSTANCE.config;
			SkyobjectPhysics physics = ((Skyobject.PhysicsDriven) event.getSkyobject()).getPhysics();
			event.addForce(new Vec3d(0, config.gravC * (config.overworldMass * physics.getMass())/Math.pow(config.overworldD0ToCenter + physics.getPos().y, 2), 0));
		}
	}

	protected World world;
	protected Supplier<Double> spawner;
	protected Map<UUID, Skyobject> skyobjects = new HashMap<>();
	protected Queue<Skyobject> toSpawn = new LinkedList<>();

	@Override
	public void tick(World world){
		if(this.world == null){
			this.world = world;
			this.spawner = AppEngSkyfall.INSTANCE.config.skyobjectFallingSupplierForWorld(world);
		}

		skyobjects.forEach((uuid, skyobject) -> skyobject.tick(world));

		if(!world.isRemote){
			double chance = spawner.get();
			if(world.rand.nextDouble() < chance) world.getMinecraftServer().sendMessage(new TextComponentString("Natural spawn now! - " + chance));

			removeSkyobjects(Skyobject::isDead);

			skyobjects.entrySet().stream().filter(skyobject -> skyobject.getValue() instanceof Skyobject.Syncable).map(skyobject -> (Map.Entry<UUID, Skyobject.Syncable<?, ?>>) (Map.Entry) skyobject).filter(skyobject -> skyobject.getValue().isDirty()).forEach(skyobject -> sendAddOrChange(skyobject.getKey(), skyobject.getValue(), false, Optional.empty()));

			while(toSpawn.peek() != null) addSkyobject(UUID.randomUUID(), toSpawn.poll());
		}
	}

	protected void addSkyobject(UUID uuid, Skyobject skyobject){
		skyobjects.put(uuid, skyobject);
		skyobject.onSpawn(world);
		if(!world.isRemote) sendAddOrChange(uuid, skyobject, true, Optional.empty());
	}

	protected void removeSkyobject(UUID uuid){
		skyobjects.remove(uuid);
		if(!world.isRemote) sendRemove(uuid, Optional.empty());
	}

	protected void removeSkyobjects(Predicate<Skyobject> predicate){
		Iterator<Map.Entry<UUID, Skyobject>> iterator = skyobjects.entrySet().iterator();
		while(iterator.hasNext()){
			Map.Entry<UUID, Skyobject> next = iterator.next();
			if(predicate.test(next.getValue())){
				iterator.remove();
				if(!world.isRemote) sendRemove(next.getKey(), Optional.empty());
			}
		}
	}

	@Override
	public Stream<Skyobject> getAllSkyobjects(){
		return skyobjects.values().stream();
	}

	@Override
	public void killall(){
		removeSkyobjects(Predicates.alwaysTrue());
		AppEngSkyfall.logger.info("Killed all skyobjects");
	}

	@Override
	public void spawn(){
		SkyobjectProvider provider = AppEngSkyfall.INSTANCE.config.getNextWeightedSkyobjectProvider(world.rand);
		GENERATORSERVICE.submit(() -> toSpawn.add(provider.generate(world.rand.nextLong())));
	}

	/*
	 * Sync
	 */

	protected void sendAddOrChange(UUID uuid, Skyobject skyobject, boolean add, Optional<EntityPlayerMP> target){
		if(skyobject instanceof Skyobject.Syncable) ((Skyobject.Syncable<?, ?>) skyobject).getSyncCompounds(add).forEach(nbt -> sendMessage(new SkyobjectMessage.AddOrChange(uuid, skyobject.getProvider().getRegistryName(), nbt), target));
		else sendMessage(new SkyobjectMessage.AddOrChange(uuid, skyobject.getProvider().getRegistryName(), skyobject.getProvider().serializeNBT(skyobject)), target);
	}

	protected void sendRemove(UUID uuid, Optional<EntityPlayerMP> target){
		sendMessage(new SkyobjectMessage.Remove(uuid), target);
	}

	protected void sendMessage(SkyobjectMessage message, Optional<EntityPlayerMP> target){
		if(target.isPresent()) AppEngSkyfall.INSTANCE.net.sendTo(message, target.get());
		else AppEngSkyfall.INSTANCE.net.sendToDimension(message, world.provider.getDimension());
	}

	@Override
	public void receiveAddOrChange(UUID uuid, ResourceLocation id, NBTTagCompound nbt){
		Skyobject existing = skyobjects.get(uuid);
		if(existing == null){
			existing = AppEngSkyfall.INSTANCE.getSkyobjectProvidersRegistry().getValue(id).get();
			if(!(existing instanceof Skyobject.Syncable)){
				skyobjects.put(uuid, AppEngSkyfall.INSTANCE.getSkyobjectProvidersRegistry().getValue(id).deserializeNBT(nbt));
				return;
			}
			skyobjects.put(uuid, existing);
		}
		if(existing instanceof Skyobject.Syncable) ((Skyobject.Syncable) existing).readNextSyncCompound(nbt);
	}

	@Override
	public void receiveRemove(UUID uuid){
		skyobjects.remove(uuid);
	}

	@Override
	public void sendAll(EntityPlayerMP target){
		skyobjects.forEach((uuid, skyobject) -> sendAddOrChange(uuid, skyobject, true, Optional.of(target)));
	}

	/*
	 * NBT
	 */

	@Override
	public NBTTagCompound serializeNBT(){
		NBTTagCompound nbt = new NBTTagCompound();
		NBTTagList skyobjects = new NBTTagList();
		this.skyobjects.forEach((uuid, skyobject) -> skyobjects.appendTag(serializeSkyobject(uuid, skyobject)));
		nbt.setTag("skyobjects", skyobjects);
		return nbt;
	}

	@Override
	public void deserializeNBT(NBTTagCompound nbt){
		this.skyobjects.clear();
		NBTTagList skyobjects = nbt.getTagList("skyobjects", 10);
		skyobjects.forEach(tag -> {
			Pair<UUID, Skyobject> skyobject = (Pair) deserializeSkyobject((NBTTagCompound) tag);
			this.skyobjects.put(skyobject.getKey(), skyobject.getValue());
		});
	}

	public static NBTTagCompound serializeSkyobject(UUID uuid, Skyobject skyobject){
		NBTTagCompound tag = new NBTTagCompound();
		tag.setTag("uuid", NBTUtil.createUUIDTag(uuid));
		tag.setString("id", skyobject.getProvider().getRegistryName().toString());
		tag.setTag("data", skyobject.getProvider().serializeNBT(skyobject));
		return tag;
	}

	public static <S extends Skyobject<S, P>, P extends SkyobjectProvider<S, P>> Pair<UUID, S> deserializeSkyobject(NBTTagCompound tag){
		return new ImmutablePair<>(NBTUtil.getUUIDFromTag(tag.getCompoundTag("uuid")), AppEngSkyfall.INSTANCE.<S, P>getSkyobjectProvidersRegistry().getValue(new ResourceLocation(tag.getString("id"))).deserializeNBT(tag.getCompoundTag("data")));
	}

}
