package appeng.core.lib.pos;

import appeng.api.pos.Ref2WorldCap;
import appeng.api.pos.WorldReference;
import appeng.core.AppEng;
import appeng.core.core.AppEngCore;
import appeng.core.lib.capability.SingleCapabilityProvider;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;

import javax.annotation.Nonnull;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.WeakHashMap;

@Mod.EventBusSubscriber(modid = AppEng.MODID)
public class Ref2WorldCapability implements Ref2WorldCap {

	/*
	 * Instance(s)
	 */

	private static final Ref2WorldCapability[] caps = {new Ref2WorldCapability(), new Ref2WorldCapability()};

	public static Ref2WorldCapability getCapability(Side side){
		return caps[side.ordinal()];
	}

	public static Ref2WorldCapability getCapability(boolean remote){
		return getCapability(remote ? Side.CLIENT : Side.SERVER);
	}

	/*
	 * Util
	 */

	public static WorldReference compileReference(World world){
		int dimId = world.provider.getDimension();
		long seed = world.getSeed();

		long hash = ((long) Long.hashCode(seed) << 32) | dimId;
		return new WorldReference(hash);
	}

	/*
	 * Impl
	 */

	protected final Map<WorldReference, WeakReference<World>> ref2world = new HashMap<>();
	protected final Map<World, WorldReference> world2ref = new WeakHashMap<>();

	public void onWorldLoad(World world){
		WorldReference reference = compileReference(world);
		ref2world.put(reference, new WeakReference<>(world));
		world2ref.put(world, reference);
	}

	@Nonnull
	@Override
	public Optional<World> getWorld(@Nonnull WorldReference reference){
		return Optional.ofNullable(ref2world.get(reference)).map(WeakReference::get);
	}

	@Nonnull
	@Override
	public WorldReference getReference(@Nonnull World world){
		return world2ref.get(world);
	}

	/*
	 * Event
	 */

	@SubscribeEvent
	public static void attachWorldCap(AttachCapabilitiesEvent<World> event){
		event.addCapability(new ResourceLocation(AppEng.MODID, "ref2world"), new SingleCapabilityProvider<>(AppEngCore.ref2WorldCapCapability, getCapability(event.getObject().isRemote)));
	}

	@SubscribeEvent
	public static void onWorldLoad(WorldEvent.Load event){
		getCapability(event.getWorld().isRemote).onWorldLoad(event.getWorld());
	}
}
