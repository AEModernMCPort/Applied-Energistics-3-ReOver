package com.owens.oobjloader.builder;

// This code was written by myself, Sean R. Owens, sean at guild dot net,
// and is released to the public domain. Share and enjoy. Since some
// people argue that it is impossible to release software to the public
// domain, you are also free to use this code under any version of the
// GPL, LPGL, Apache, or BSD licenses, or contact me for use of another
// license.  (I generally don't care so I'll almost certainly say yes.)
// In addition this code may also be used under the "unlicense" described
// at http://unlicense.org/ .  See the file UNLICENSE in the repo.

import com.owens.oobjloader.parser.IMesh;
import net.minecraft.util.ResourceLocation;

public class Material {

	public String name;
	public ReflectivityTransmiss ka = new ReflectivityTransmiss();
	public ReflectivityTransmiss kd = new ReflectivityTransmiss();
	public ReflectivityTransmiss ks = new ReflectivityTransmiss();
	public ReflectivityTransmiss tf = new ReflectivityTransmiss();
	public int illumModel = 0;
	public boolean dHalo = false;
	public double dFactor = 0.0;
	public double nsExponent = 0.0;
	public double sharpnessValue = 0.0;
	public double niOpticalDensity = 0.0;
	public ResourceLocation mapKaLocation = null;
	public ResourceLocation mapKdLocation = null;
	public ResourceLocation mapKsLocation = null;
	public ResourceLocation mapNsLocation = null;
	public ResourceLocation mapDLocation = null;
	public ResourceLocation decalLocation = null;
	public ResourceLocation dispLocation = null;
	public ResourceLocation bumpLocation = null;
	public int reflType = IMesh.MTL_REFL_TYPE_UNKNOWN;
	public ResourceLocation reflLocation = null;

	public Material(String name){
		this.name = name;
	}
}