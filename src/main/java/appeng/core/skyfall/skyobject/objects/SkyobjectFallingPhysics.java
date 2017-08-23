package appeng.core.skyfall.skyobject.objects;

import appeng.core.lib.util.NbtUtils;
import appeng.core.skyfall.api.skyobject.Skyobject;
import appeng.core.skyfall.api.skyobject.SkyobjectPhysics;
import code.elix_x.excore.utils.world.MutableBlockAccess;
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import org.apache.commons.lang3.mutable.MutableDouble;
import org.apache.commons.lang3.mutable.MutableObject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class SkyobjectFallingPhysics implements SkyobjectPhysics {

	protected final Skyobject skyobject;

	protected boolean dirty = false;

	protected Vec3d pos = Vec3d.ZERO;
	protected Vec3d rot = Vec3d.ZERO;

	protected Vec3d prevTickPos = Vec3d.ZERO;
	protected Vec3d prevTickRot = Vec3d.ZERO;

	protected Vec3d force = Vec3d.ZERO;
	protected Vec3d torque = Vec3d.ZERO;

	protected double mass;

	public SkyobjectFallingPhysics(Skyobject skyobject){
		this.skyobject = skyobject;
	}

	/*
	 * Dirty
	 */

	public boolean isDirty(){
		return dirty;
	}

	public void setDirty(boolean dirty){
		this.dirty = dirty;
	}

	/*
	 * Get-set
	 */

	@Override
	public Vec3d getPos(){
		return pos;
	}

	public void setPos(Vec3d pos){
		this.pos = pos;
		setDirty(true);
	}

	public void addPos(Vec3d pos){
		setPos(getPos().add(pos));
	}

	@Override
	public Vec3d getRotation(){
		return rot;
	}

	public void setRot(Vec3d rot){
		this.rot = rot;
		setDirty(true);
	}

	public void addRot(Vec3d rot){
		setRot(getRotation().add(rot));
	}

	public Vec3d getPrevTickPos(){
		return prevTickPos;
	}

	public Vec3d getPrevTickRot(){
		return prevTickRot;
	}

	public double getMass(){
		return mass;
	}

	public void setMass(double mass){
		this.mass = mass;
	}

	public Vec3d getForce(){
		return force;
	}

	public void setForce(Vec3d force){
		this.force = force;
	}

	public Vec3d getTorque(){
		return torque;
	}

	public void setTorque(Vec3d torque){
		this.torque = torque;
	}

	@Override
	public boolean tick(World world){
		prevTickPos = pos;
		prevTickRot = rot;

		List<Vec3d> forces = new ArrayList<>();
		List<Vec3d> torques = new ArrayList<>();
		MinecraftForge.EVENT_BUS.post(new GatherForcesEvent(skyobject, forces, torques));
		if(!forces.isEmpty()) force = force.add(sumVecs(forces));
		if(!torques.isEmpty()) torque = torque.add(sumVecs(torques));
		if(force.lengthSquared() != 0) addPos(force.scale(1/getMass()));
		if(torque.lengthSquared() != 0) addRot(torque.scale(1/getMass()));

		return getPos().y < 0;
	}

	protected Vec3d sumVecs(Collection<Vec3d> vecs){
		MutableObject<Vec3d> res = new MutableObject<>(Vec3d.ZERO);
		vecs.forEach(vec -> res.setValue(res.getValue().add(vec)));
		return res.getValue();
	}

	@Override
	public NBTTagCompound serializeNBT(){
		NBTTagCompound nbt = new NBTTagCompound();
		nbt.setTag("pos", NbtUtils.serializeVec3d(pos));
		nbt.setTag("rot", NbtUtils.serializeVec3d(rot));
		nbt.setTag("prevPos", NbtUtils.serializeVec3d(prevTickPos));
		nbt.setTag("prevRot", NbtUtils.serializeVec3d(prevTickRot));
		nbt.setTag("force", NbtUtils.serializeVec3d(force));
		nbt.setTag("torque", NbtUtils.serializeVec3d(torque));
		return nbt;
	}

	@Override
	public void deserializeNBT(NBTTagCompound nbt){
		pos = NbtUtils.deserializeVec3d(nbt.getCompoundTag("pos"));
		rot = NbtUtils.deserializeVec3d(nbt.getCompoundTag("rot"));
		prevTickPos = NbtUtils.deserializeVec3d(nbt.getCompoundTag("prevPos"));
		prevTickRot = NbtUtils.deserializeVec3d(nbt.getCompoundTag("prevRot"));
		force = NbtUtils.deserializeVec3d(nbt.getCompoundTag("force"));
		torque = NbtUtils.deserializeVec3d(nbt.getCompoundTag("torque"));
	}

	public static class WorldDriven extends SkyobjectFallingPhysics implements LocalBlockAccessDriven {

		protected final MutableBlockAccess localBlockAccess;
		protected final AxisAlignedBB blockAccessBox;

		public WorldDriven(Skyobject skyobject, MutableBlockAccess localBlockAccess, AxisAlignedBB blockAccessBox){
			super(skyobject);
			this.localBlockAccess = localBlockAccess;
			this.blockAccessBox = blockAccessBox;
			recalcMass();
		}

		@Override
		public MutableBlockAccess getLocalBlockAccess(){
			return localBlockAccess;
		}

		public void recalcMass(){
			MutableDouble mass = new MutableDouble();
			BlockPos.getAllInBox(new BlockPos(blockAccessBox.minX, blockAccessBox.minY, blockAccessBox.minZ), new BlockPos(blockAccessBox.maxX, blockAccessBox.maxY, blockAccessBox.maxZ)).forEach(pos -> mass.add(getMass(localBlockAccess.getBlockState(pos))));
			setMass(mass.getValue());
		}

		protected double getMass(IBlockState block){
			//TODO This may very well throw excep
			return Math.log(block.getBlockHardness(null, null) * block.getBlock().getExplosionResistance(null));
		}

	}
}
