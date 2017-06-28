/*
 * The MIT License
 *
 * Copyright 2016 Christopher Collin Hall 
 * <a href="mailto:explosivegnome@yahoo.com">explosivegnome@yahoo.com</a>.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package hall.collin.christopher.math.noise;

import hall.collin.christopher.math.AbstractNumberGenerator;
import hall.collin.christopher.math.random.DefaultRandomNumberGenerator;



/**
 * This class provides <b>distortion-free</b> fractal noise on a sphere. It does 
 * so by using 3D fractal noise (instead of the standard 2D noise) and sampling 
 * the longitude and latitude as a 3D point on the sphere's surface in the noise 
 * space.
 * @author Christopher Collin Hall
 */
public class SphericalSurfaceFractalNoiseGenerator extends DefaultFractalNoiseGenerator3D{
	
	private final double radius;
	/**
	 * Constructs a distortion-free spherical noise generator for generating 
	 * longitude-latitude (aka UV) coordinate noise.
	 * @param radius Radius of the sphere
	 * @param initialNoiseScale The initial grid spacing of the lowest frequency noise octave
	 * @param initialNoiseMagnitude The range of values for the lowest frequency noise octave
	 * @param octaveScaleMultiplier How much to change the grid size for each successive 
	 * layer of noise (must be between 0 and 1). Typically 0.5
	 * @param octaveMagnitudeMultiplier How to change the range of values when 
	 * generating noise octaves. Typically 0.5
	 * @param seeder random number generator
	 */
	public SphericalSurfaceFractalNoiseGenerator(double radius, double initialNoiseScale, double initialNoiseMagnitude, double octaveScaleMultiplier, double octaveMagnitudeMultiplier, AbstractNumberGenerator seeder) {
		super(initialNoiseScale, initialNoiseMagnitude, octaveScaleMultiplier, octaveMagnitudeMultiplier, seeder);
		this.radius = radius;
	}
	/**
	 * Constructs a distortion-free spherical noise generator for generating 
	 * longitude-latitude (aka UV) coordinate noise using typical Perlin Noise 
	 * default settings.
	 * @param seed The seed for the random number generator
	 */
	public SphericalSurfaceFractalNoiseGenerator(long seed){
		this(1.0,1.0,1.0,0.5,0.5,new DefaultRandomNumberGenerator(seed));
	}
	/**
	 * This method will generate a Perlin Noise type of value 
	 * interpolated at the provided coordinate, specified as longitude and 
	 * latitude on the surface of a sphere.<p/>
	 * Thread Safe!
	 * @param precision Spacial resolution (finest noise "octave" will have a 
	 * grid size smaller than this value)
	 * @param longitude The longitude coordinate (in <b><u>radians</u></b>) around the sphere (wraps around 
	 * Y-axis in right-hand coordinate space, making east the positive longitude direction)
	 * @param latitude The longitude coordinate (in <b><u>radians</u></b>) around the sphere as degrees 
	 * north of the equator (negative values for south of the equator)
	 * @return A value interpolated at the given coordinates from a 3D noise 
	 * space
	 */
	public double valueAt(double precision, double longitude, double latitude){
		double x,y,z;
		y = radius*Math.sin(latitude);
		double planarRadius = radius * cos(latitude);
		z = planarRadius * cos(longitude);
		x = planarRadius * sin(longitude);
		return super.valueAt(precision, x, y, z);
	}
	
	
	
	/**
	 * This method can be overridden for optimization purposes
	 * @param a a number
	 * @return the cosine of the number
	 */
	protected double cos(double a) {
		return Math.cos(a);
	}
	
	
	/**
	 * This method can be overridden for optimization purposes
	 * @param a a number
	 * @return the sine of the number
	 */
	protected double sin(double a) {
		return Math.sin(a);
	}
	
	
	@Deprecated // for testing and demonstration only
	public static void main(String[] a) {
		System.out.println("Running...");
		int size = 400;
		final java.awt.image.BufferedImage bimg = new java.awt.image.BufferedImage(size, size, java.awt.image.BufferedImage.TYPE_INT_ARGB);
		SphericalSurfaceFractalNoiseGenerator prng = new SphericalSurfaceFractalNoiseGenerator(System.currentTimeMillis());
		final double precision = 1.0 / size;
		long t0 = System.currentTimeMillis();
		
		for (int x = 0; x < size; x++) {
			final int xf = x;
			final double longitude = x * (2 * Math.PI / size);
			// sine functions are a lot slower than standard floating point math,
			// so you need to multi-thread spherical geometry for performance
			// (lord help you should you ever need to use arc-sine functions)
			java.util.concurrent.ForkJoinPool.commonPool().submit(()->{
				for (int y = 0; y < size; y++) {
					double latitude = ((size / 2) - y) * (Math.PI / size);
					float v = (float) prng.valueAt(precision, longitude, latitude) + 1;
					v *= 0.5f;
					if (v < 0) {
						v = 0f;//0.5f;
					}
					if (v > 1) {
						v = 1f;//0.5f;
					}
					bimg.setRGB(xf, y, java.awt.Color.HSBtoRGB(0, 0f, v));
				}
			});
		}
		java.util.concurrent.ForkJoinPool.commonPool().awaitQuiescence(1, java.util.concurrent.TimeUnit.MINUTES);
		long t1 = System.currentTimeMillis();
		System.out.println("Completed in " + (t1 - t0) + " ms");
		javax.swing.JOptionPane.showMessageDialog(null, new javax.swing.JLabel(new javax.swing.ImageIcon(bimg)));

		System.exit(0);
	}


}
