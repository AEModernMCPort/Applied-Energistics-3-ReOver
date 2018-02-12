package appeng.core.skyfall.api.skyobject;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.World;

import java.util.stream.Stream;

public interface Skyobject<S extends Skyobject<S, P>, P extends SkyobjectProvider<S, P>> {

	P getProvider();

	default void onSpawn(World world){}

	void tick(World world);

	boolean isDead();


	//Client only

	AxisAlignedBB getRendererBoundingBox(float partialTicks);

	void render(float partialTicks);

	interface Syncable<S extends Skyobject.Syncable<S, P>, P extends SkyobjectProvider<S, P>> extends Skyobject<S, P> {

		boolean isDirty();

		Stream<NBTTagCompound> getSyncCompounds(boolean sendEverything);

		void readNextSyncCompound(NBTTagCompound nbt);

		/**
		 * Quick hash of this sky object to inform the server whether or not the sync with this client is complete
		 * @return hash of this sky object
		 */
		long hash();

		/**
		 * Called, after spawning this object, once it has been successfully synced with all clients
		 */
		default void allClientsReceived(){

		}

	}

	interface PhysicsDriven<S extends Skyobject.PhysicsDriven<S, P>, P extends SkyobjectProvider<S, P>> extends Skyobject<S, P> {

		SkyobjectPhysics getPhysics();

	}

}
