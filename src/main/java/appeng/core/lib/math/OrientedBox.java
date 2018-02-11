package appeng.core.lib.math;

import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.Vec3d;
import org.joml.*;

import java.util.Objects;
import java.util.stream.Stream;

public final class OrientedBox implements Cloneable {

	static Vector3dc toJOML(Vec3d vec){
		return new Vector3d(vec.x, vec.y, vec.z);
	}

	private final Vector3d center;
	private final Vector3d xAxis, yAxis, zAxis;
	private final Vector3d halfSize;

	public OrientedBox(Vector3dc center, Vector3dc xAxis, Vector3dc yAxis, Vector3dc zAxis, Vector3dc halfSize){
		this.center = new Vector3d(center);
		this.xAxis = new Vector3d(xAxis);
		this.yAxis = new Vector3d(yAxis);
		this.zAxis = new Vector3d(zAxis);
		this.halfSize = new Vector3d(halfSize);
	}

	public OrientedBox(Vector3dc center, Vector3dc halfSize){
		this(center, new Vector3d(1, 0, 0), new Vector3d(0, 1, 0), new Vector3d(0, 0, 1), halfSize);
	}

	public OrientedBox(AxisAlignedBB aabb){
		this(toJOML(aabb.getCenter()), new Vector3d((aabb.maxX - aabb.minX) / 2, (aabb.maxY - aabb.minY) / 2, (aabb.maxZ - aabb.minZ) / 2));
	}

	// Clone

	public OrientedBox(OrientedBox box){
		this(box.center, box.xAxis, box.yAxis, box.zAxis, box.halfSize);
	}

	@Override
	public OrientedBox clone(){
		return new OrientedBox(this);
	}

	/*
	 * Transform
	 */

	public OrientedBox translate(Vector3dc d){
		return new OrientedBox(center.add(d, new Vector3d()), xAxis, yAxis, zAxis, halfSize);
	}

	public OrientedBox rotate(Quaterniondc rot){
		return new OrientedBox(center, xAxis.rotate(rot, new Vector3d()), yAxis.rotate(rot, new Vector3d()), zAxis.rotate(rot, new Vector3d()), halfSize);
	}

	public OrientedBox rotateAroundOrigin(Quaterniondc rot){
		return new OrientedBox(center.rotate(rot, new Vector3d()), xAxis.rotate(rot, new Vector3d()), yAxis.rotate(rot, new Vector3d()), zAxis.rotate(rot, new Vector3d()), halfSize);
	}

	public OrientedBox scale(Vector3dc scale){
		return new OrientedBox(center, xAxis, yAxis, zAxis, new Vector3d(halfSize.x() * scale.x(), halfSize.y() * scale.y(), halfSize.z() * scale.z()));
	}

	public OrientedBox transform(Matrix4d matrix4d){
		Vector4d rotCenter = matrix4d.transform(new Vector4d(center.x(), center.y(), center.z(), 1), new Vector4d());
		Vector4d rXAxis4 = matrix4d.transform(new Vector4d(xAxis.x(), xAxis.y(), xAxis.z(), 0));
		Vector4d rYAxis4 = matrix4d.transform(new Vector4d(yAxis.x(), yAxis.y(), yAxis.z(), 0));
		Vector4d rZAxis4 = matrix4d.transform(new Vector4d(zAxis.x(), zAxis.y(), zAxis.z(), 0));
		Vector3d rXAxis = new Vector3d(rXAxis4.x, rXAxis4.y, rXAxis4.z);
		Vector3d rYAxis = new Vector3d(rYAxis4.x, rYAxis4.y, rYAxis4.z);
		Vector3d rZAxis = new Vector3d(rZAxis4.x, rZAxis4.y, rZAxis4.z);
		Vector3d sHalfSize = new Vector3d(halfSize.x() * rXAxis.length(), halfSize.y() * rYAxis.length(), halfSize.z() * rZAxis.length());
		return new OrientedBox(new Vector3d(rotCenter.x, rotCenter.y, rotCenter.z), rXAxis.normalize(), rYAxis.normalize(), rZAxis.normalize(), sHalfSize);
	}

	/*
	 * Intersect
	 */

	public boolean intersects(OrientedBox other){
		return Intersectiond.testObOb(center, xAxis, yAxis, zAxis, halfSize, other.center, other.xAxis, other.yAxis, other.zAxis, other.halfSize);
	}

	/*
	 * Bounding box
	 */

	public AABBd getBoundingBox(){
		//@formatter:off
		AABBd[] bbs = new AABBd[]{
				new AABBd(center, center).union(new Vector3d(center).add(new Vector3d(xAxis).mul(halfSize.x)).add(new Vector3d(yAxis).mul(halfSize.y)).add(new Vector3d(zAxis).mul(halfSize.z))),
				new AABBd(center, center).union(new Vector3d(center).add(new Vector3d(xAxis).mul(halfSize.x)).add(new Vector3d(yAxis).mul(halfSize.y)).sub(new Vector3d(zAxis).mul(halfSize.z))),
				new AABBd(center, center).union(new Vector3d(center).add(new Vector3d(xAxis).mul(halfSize.x)).sub(new Vector3d(yAxis).mul(halfSize.y)).add(new Vector3d(zAxis).mul(halfSize.z))),
				new AABBd(center, center).union(new Vector3d(center).add(new Vector3d(xAxis).mul(halfSize.x)).sub(new Vector3d(yAxis).mul(halfSize.y)).sub(new Vector3d(zAxis).mul(halfSize.z))),
				new AABBd(center, center).union(new Vector3d(center).sub(new Vector3d(xAxis).mul(halfSize.x)).add(new Vector3d(yAxis).mul(halfSize.y)).add(new Vector3d(zAxis).mul(halfSize.z))),
				new AABBd(center, center).union(new Vector3d(center).sub(new Vector3d(xAxis).mul(halfSize.x)).add(new Vector3d(yAxis).mul(halfSize.y)).sub(new Vector3d(zAxis).mul(halfSize.z))),
				new AABBd(center, center).union(new Vector3d(center).sub(new Vector3d(xAxis).mul(halfSize.x)).sub(new Vector3d(yAxis).mul(halfSize.y)).add(new Vector3d(zAxis).mul(halfSize.z))),
				new AABBd(center, center).union(new Vector3d(center).sub(new Vector3d(xAxis).mul(halfSize.x)).sub(new Vector3d(yAxis).mul(halfSize.y)).sub(new Vector3d(zAxis).mul(halfSize.z)))
		};
		//@formatter:on
		return Stream.of(bbs).reduce(bbs[0], AABBd::union);
	}

	public AxisAlignedBB getBoundingBoxMC(){
		AABBd aabb = getBoundingBox();
		return new AxisAlignedBB(aabb.minX, aabb.minY, aabb.minZ, aabb.maxX, aabb.maxY, aabb.maxZ);
	}

	/*
	 * HET
	 */

	@Override
	public boolean equals(Object o){
		if(this == o) return true;
		if(!(o instanceof OrientedBox)) return false;
		OrientedBox box = (OrientedBox) o;
		return Objects.equals(center, box.center) && Objects.equals(xAxis, box.xAxis) && Objects.equals(yAxis, box.yAxis) && Objects.equals(zAxis, box.zAxis) && Objects.equals(halfSize, box.halfSize);
	}

	@Override
	public int hashCode(){
		return Objects.hash(center, xAxis, yAxis, zAxis, halfSize);
	}

	@Override
	public String toString(){
		return "OrientedBox{" + "center=" + center + ", xAxis=" + xAxis + ", yAxis=" + yAxis + ", zAxis=" + zAxis + ", halfSize=" + halfSize + '}';
	}

}
