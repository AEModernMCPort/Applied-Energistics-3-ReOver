package com.owens.oobjloader.builder;

// This code was written by myself, Sean R. Owens, sean at guild dot net,
// and is released to the public domain. Share and enjoy. Since some
// people argue that it is impossible to release software to the public
// domain, you are also free to use this code under any version of the
// GPL, LPGL, Apache, or BSD licenses, or contact me for use of another
// license.  (I generally don't care so I'll almost certainly say yes.)
// In addition this code may also be used under the "unlicense" described
// at http://unlicense.org/ .  See the file UNLICENSE in the repo.

public class FaceVertex {

	int index = -1;
	public VertexGeometric v = null;
	public VertexTexture t = null;
	public VertexNormal n = null;

	public String toString(){
		return v + "|" + n + "|" + t;
	}

	@Override
	public boolean equals(Object o){
		if(this == o) return true;
		if(o == null || getClass() != o.getClass()) return false;

		FaceVertex vertex = (FaceVertex) o;

		if(v != null ? !v.equals(vertex.v) : vertex.v != null) return false;
		if(t != null ? !t.equals(vertex.t) : vertex.t != null) return false;
		return n != null ? n.equals(vertex.n) : vertex.n == null;
	}

	@Override
	public int hashCode(){
		int result = v != null ? v.hashCode() : 0;
		result = 31 * result + (t != null ? t.hashCode() : 0);
		result = 31 * result + (n != null ? n.hashCode() : 0);
		return result;
	}

}