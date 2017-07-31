package com.owens.oobjloader.builder;

// This code was written by myself, Sean R. Owens, sean at guild dot net,
// and is released to the public domain. Share and enjoy. Since some
// people argue that it is impossible to release software to the public
// domain, you are also free to use this code under any version of the
// GPL, LPGL, Apache, or BSD licenses, or contact me for use of another
// license.  (I generally don't care so I'll almost certainly say yes.)
// In addition this code may also be used under the "unlicense" described
// at http://unlicense.org/ .  See the file UNLICENSE in the repo.

public class VertexGeometric {

	public float x = 0;
	public float y = 0;
	public float z = 0;

	public VertexGeometric(float x, float y, float z){
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public String toString(){
		if(null == this){
			return "null";
		} else {
			return x + "," + y + "," + z;
		}
	}

	@Override
	public boolean equals(Object o){
		if(this == o) return true;
		if(o == null || getClass() != o.getClass()) return false;

		VertexGeometric that = (VertexGeometric) o;

		if(Float.compare(that.x, x) != 0) return false;
		if(Float.compare(that.y, y) != 0) return false;
		return Float.compare(that.z, z) == 0;
	}

	@Override
	public int hashCode(){
		int result = (x != +0.0f ? Float.floatToIntBits(x) : 0);
		result = 31 * result + (y != +0.0f ? Float.floatToIntBits(y) : 0);
		result = 31 * result + (z != +0.0f ? Float.floatToIntBits(z) : 0);
		return result;
	}

}