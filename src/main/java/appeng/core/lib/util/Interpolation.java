package appeng.core.lib.util;

import net.minecraft.util.math.Vec3d;

public enum  Interpolation {

	LINEAR {
		@Override
		float interpolateF(float f1, float f2, float fract){
			return f1 * (1-fract) + f2 * fract;
		}

		@Override
		double interpolateD(double d1, double d2, double fract){
			return d1 * (1-fract) + d2 * fract;	
		}
	};

	abstract float interpolateF(float f1, float f2, float fract);

	abstract double interpolateD(double d1, double d2, double fract);

	public Vec3d interpolateVec3d(Vec3d vec1, Vec3d vec2, double fract){
		return new Vec3d(interpolateD(vec1.x, vec2.x, fract), interpolateD(vec1.y, vec2.y, fract), interpolateD(vec1.z, vec2.z, fract));
	}

}
