/*******************************************************************************
 * Copyright 2016 Elix_x
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package code.elix_x.excore.utils.vecs;

import java.util.Random;

import org.apache.commons.lang3.math.NumberUtils;

import code.elix_x.excomms.random.RandomUtils;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public class Vec3Utils {

	public static Vec3d getLookVec(float rotationYaw, float rotationPitch){
		return getLook(rotationYaw, rotationPitch, 1.0f, 0, 0);
	}

	public static Vec3d getLook(float rotationYaw, float rotationPitch, float f, float prevRotationYaw, float prevRotationPitch){
		float f1;
		float f2;
		float f3;
		float f4;

		if(f == 1.0F){
			f1 = MathHelper.cos(-rotationYaw * 0.017453292F - (float) Math.PI);
			f2 = MathHelper.sin(-rotationYaw * 0.017453292F - (float) Math.PI);
			f3 = -MathHelper.cos(-rotationPitch * 0.017453292F);
			f4 = MathHelper.sin(-rotationPitch * 0.017453292F);
			return new Vec3d((double) (f2 * f3), (double) f4, (double) (f1 * f3));
		} else{
			f1 = prevRotationPitch + (rotationPitch - prevRotationPitch) * f;
			f2 = prevRotationYaw + (rotationYaw - prevRotationYaw) * f;
			f3 = MathHelper.cos(-f2 * 0.017453292F - (float) Math.PI);
			f4 = MathHelper.sin(-f2 * 0.017453292F - (float) Math.PI);
			float f5 = -MathHelper.cos(-f1 * 0.017453292F);
			float f6 = MathHelper.sin(-f1 * 0.017453292F);
			return new Vec3d((double) (f4 * f5), (double) f6, (double) (f3 * f5));
		}
	}

	public static Vec3d getRandomVecBetween(Random random, Vec3d... vecs){
		double[] xs = getAllX(vecs);
		double[] ys = getAllY(vecs);
		double[] zs = getAllZ(vecs);
		double minX = NumberUtils.min(xs);
		double maxX = NumberUtils.max(xs);
		double minY = NumberUtils.min(ys);
		double maxY = NumberUtils.max(ys);
		double minZ = NumberUtils.min(zs);
		double maxZ = NumberUtils.max(zs);
		return new Vec3d(RandomUtils.nextDouble(random, minX, maxX), RandomUtils.nextDouble(random, minY, maxY), RandomUtils.nextDouble(random, minZ, maxZ));
	}

	public static double[] getAllX(Vec3d... vecs){
		double[] xs = new double[vecs.length];
		for(int i = 0; i < vecs.length; i++){
			xs[i] = vecs[i].x;
		}
		return xs;
	}

	public static double[] getAllY(Vec3d... vecs){
		double[] ys = new double[vecs.length];
		for(int i = 0; i < vecs.length; i++){
			ys[i] = vecs[i].y;
		}
		return ys;
	}

	public static double[] getAllZ(Vec3d... vecs){
		double[] zs = new double[vecs.length];
		for(int i = 0; i < vecs.length; i++){
			zs[i] = vecs[i].z;
		}
		return zs;
	}

}
