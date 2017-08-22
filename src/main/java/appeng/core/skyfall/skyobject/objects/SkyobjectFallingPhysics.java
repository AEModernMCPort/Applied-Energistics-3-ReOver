package appeng.core.skyfall.skyobject.objects;

import appeng.core.lib.util.NbtUtils;
import appeng.core.skyfall.api.skyobject.Skyobject;
import appeng.core.skyfall.api.skyobject.SkyobjectPhysics;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class SkyobjectFallingPhysics implements SkyobjectPhysics {

	protected final Skyobject skyobject;

	protected Vec3d pos = new Vec3d(0, 0, 0);
	protected Vec3d rot = new Vec3d(0, 0, 0);

	public SkyobjectFallingPhysics(Skyobject skyobject){
		this.skyobject = skyobject;
	}

	@Override
	public Vec3d getPos(){
		return pos;
	}

	public void setPos(Vec3d pos){
		this.pos = pos;
	}

	@Override
	public Vec3d getRotation(){
		return rot;
	}

	public void setRot(Vec3d rot){
		this.rot = rot;
	}

	@Override
	public boolean tick(World world){
		return false;
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
