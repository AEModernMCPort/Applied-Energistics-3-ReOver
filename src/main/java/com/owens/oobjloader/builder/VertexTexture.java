package com.owens.oobjloader.builder;

// This code was written by myself, Sean R. Owens, sean at guild dot net,
// and is released to the public domain. Share and enjoy. Since some
// people argue that it is impossible to release software to the public
// domain, you are also free to use this code under any version of the
// GPL, LPGL, Apache, or BSD licenses, or contact me for use of another
// license.  (I generally don't care so I'll almost certainly say yes.)
// In addition this code may also be used under the "unlicense" described
// at http://unlicense.org/ .  See the file UNLICENSE in the repo.

public class VertexTexture {

	public float u = 0;
	public float v = 0;

	VertexTexture(float u, float v){
		this.u = u;
		this.v = v;
	}

	public String toString(){
		if(null == this){
			return "null";
		} else {
			return u + "," + v;
		}
	}

	@Override
	public boolean equals(Object o){
		if(this == o) return true;
		if(o == null || getClass() != o.getClass()) return false;

		VertexTexture that = (VertexTexture) o;

		if(Float.compare(that.u, u) != 0) return false;
		return Float.compare(that.v, v) == 0;
	}

	@Override
	public int hashCode(){
		int result = (u != +0.0f ? Float.floatToIntBits(u) : 0);
		result = 31 * result + (v != +0.0f ? Float.floatToIntBits(v) : 0);
		return result;
	}

}