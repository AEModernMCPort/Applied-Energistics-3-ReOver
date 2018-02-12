package appeng.core.skyfall.api.skyobject;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.fml.common.eventhandler.Event;

import java.util.List;

public interface SkyobjectPhysics extends INBTSerializable<NBTTagCompound> {

	Vec3d getPos();
	Vec3d getRotation();
	double getMass();

	boolean tick(World world);

	interface LocalBlockAccessDriven extends SkyobjectPhysics {

		IBlockAccess getLocalBlockAccess();

	}

	class GatherForcesEvent extends Event {

		private final Skyobject.PhysicsDriven skyobject;
		private final List<Vec3d> forces;
		private final List<Vec3d> torques;

		public GatherForcesEvent(Skyobject.PhysicsDriven skyobject, List<Vec3d> forces, List<Vec3d> torques){
			this.skyobject = skyobject;
			this.forces = forces;
			this.torques = torques;
		}

		public <S extends Skyobject.PhysicsDriven<S, P>, P extends SkyobjectProvider<S, P>> S getSkyobject(){
			return (S) skyobject;
		}

		public void addForce(Vec3d force){
			forces.add(force);
		}

		public void addTorque(Vec3d torque){
			torques.add(torque);
		}

	}

}
