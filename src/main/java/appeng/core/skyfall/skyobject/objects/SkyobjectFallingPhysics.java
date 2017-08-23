package appeng.core.skyfall.skyobject.objects;

import appeng.core.lib.util.NbtUtils;
import appeng.core.skyfall.api.skyobject.Skyobject;
import appeng.core.skyfall.api.skyobject.SkyobjectPhysics;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import org.apache.commons.lang3.mutable.MutableObject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class SkyobjectFallingPhysics implements SkyobjectPhysics {

	protected final Skyobject skyobject;

	protected boolean dirty = false;

	protected Vec3d pos = new Vec3d(0, 0, 0);
	protected Vec3d rot = new Vec3d(0, 0, 0);

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

	@Override
	public Vec3d getRotation(){
		return rot;
	}

	public void setRot(Vec3d rot){
		this.rot = rot;
		setDirty(true);
	}

	public double getMass(){
		return mass;
	}

	public void setMass(double mass){
		this.mass = mass;
	}

	@Override
	public boolean tick(World world){
		List<Vec3d> forces = new ArrayList<>();
		List<Vec3d> torques = new ArrayList<>();
		MinecraftForge.EVENT_BUS.post(new GatherForcesEvent(skyobject, forces, torques));
		if(!forces.isEmpty()) setPos(getPos().add(sumVecs(forces).scale(1/getMass())));
		if(!torques.isEmpty()) setRot(getRotation().add(sumVecs(torques).scale(1/getMass())));
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
		return nbt;
	}

	@Override
	public void deserializeNBT(NBTTagCompound nbt){
		pos = NbtUtils.deserializeVec3d(nbt.getCompoundTag("pos"));
		rot = NbtUtils.deserializeVec3d(nbt.getCompoundTag("rot"));
	}
}
