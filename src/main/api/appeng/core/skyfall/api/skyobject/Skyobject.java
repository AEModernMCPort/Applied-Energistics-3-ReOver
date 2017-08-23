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

	AxisAlignedBB getRendererBoundingBox();

	void render(float partialTicks);

	interface Syncable<S extends Skyobject.Syncable<S, P>, P extends SkyobjectProvider<S, P>> extends Skyobject<S, P> {

		boolean isDirty();

		Stream<NBTTagCompound> getSyncCompounds(boolean sendEverything);

		void readNextSyncCompound(NBTTagCompound nbt);

	}

	interface PhysicsDriven<S extends Skyobject.PhysicsDriven<S, P>, P extends SkyobjectProvider<S, P>> extends Skyobject<S, P> {

		SkyobjectPhysics getPhysics();

	}

}
