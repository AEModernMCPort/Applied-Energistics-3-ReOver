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
package hall.collin.christopher.math;

/**
 * This is a convenience class that provides multidimensional cubic 
 * interpolation methods. This class is abstract and not meant to be extended
 * @author Christopher Collin Hall
 */
public abstract class CubicInterpolator {
	/**
	 * Interpolate with cubic approximation for a point X on a grid. X 
	 * must lie between the X values of the yn1 and yp1 control points.
	 * @param x x coordinate to interpolate
	 * @param yn2 Y value at f(floor(x)-1)
	 * @param yn1 Y value at f(floor(x)-0)
	 * @param yp1 Y value at f(floor(x)+1)
	 * @param yp2 Y value at f(floor(x)+2)
	 * @return A cubic-interpolated value from the given control points.
	 */
	public static double interpolate(double x, double yn2, double yn1, double yp1, double yp2){
		return interpolate1d( x,  yn2,  yn1,  yp1,  yp2);
	}
	/**
	 * Interpolate with cubic approximation for a point X on a grid. X 
	 * must lie between the X values of the yn1 and yp1 control points.
	 * @param x x coordinate to interpolate
	 * @param yn2 Y value at f(floor(x)-1)
	 * @param yn1 Y value at f(floor(x)-0)
	 * @param yp1 Y value at f(floor(x)+1)
	 * @param yp2 Y value at f(floor(x)+2)
	 * @return A cubic-interpolated value from the given control points.
	 */
	public static double interpolate1d(double x, double yn2, double yn1, double yp1, double yp2){
		double w = x - subfloor(x);
		// adapted from http://www.paulinternet.nl/?page=bicubic
		
//		original
//		double A = -0.5 * yn2 + 1.5 * yn1 - 1.5 * yp1 + 0.5 * yp2;
//		double B = yn2 - 2.5 * yn1 + 2 * yp1 - 0.5 * yp2;
//		double C = -0.5 * yn2 + 0.5 * yp1;
//		double D = yn1;
//		return A * w * w * w + B * w * w + C * w + D;
		
		// optimized
		double O1 = -0.5 * yn2;
		double O2 = 0.5 * yp2;
		double O3 = w * w;
		double A = O1 + 1.5 * yn1 - 1.5 * yp1 + O2;
		double B = yn2 - 2.5 * yn1 + 2 * yp1 - O2;
		double C = O1 + 0.5 * yp1;
		double D = yn1;
		return A * O3 * w + B * O3 + C * w + D;
	}
	/**
	 * Returns the bi-cubic interpolation of the (x,y) coordinate inide 
	 * the provided grid of control points. (x,y) is assumed to be in the 
	 * center square of the unit grid.
	 * @param x x coordinate between local16[1][y] and local16[2][y]
	 * @param y y coordinate between local16[x][1] and local16[x][2]
	 * @param local16 Array [x][y] of the 4x4 grid around the coordinate
	 * @return Returns the bi-cubic interpolation of the (x,y) coordinate.
	 */
	public static double interpolate2d(double x, double y, double[][] local16){
		double[] section = new double[4];
		for(int i = 0; i < 4; i++){
			section[i] = interpolate1d(y,local16[i][0],local16[i][1],local16[i][2],local16[i][3]);
		}
		return interpolate1d(x,section[0],section[1],section[2],section[3]);
	}
	/**
	 * Performs a tri-cubic interpolation of the (x,y,z) coordinate near 
	 * the center of the provided unit grid of surrounding control points.
	 * @param x x coordinate in the middle of the array space
	 * @param y y coordinate in the middle of the array space
	 * @param z z coordinate in the middle of the array space
	 * @param local64 Array [x][y][z] of the 4x4x4 grid around the coordinate
	 * @return Returns the tri-cubic interpolation of the given coordinate.
	 */
	public static double interpolate3d(double x, double y, double z, double[][][] local64){
		double[] section = new double[4];
		for(int i = 0; i < 4; i++){
			section[i] = interpolate2d(y,z,local64[i]);
		}
		return interpolate1d(x,section[0],section[1],section[2],section[3]);
	}
	/**
	 * Performs a quad-cubic interpolation of the (x,y,z,a) coordinate near 
	 * the center of the provided unit grid of surrounding control points.
	 * @param x coordinate in the middle of the array space
	 * @param y coordinate in the middle of the array space
	 * @param z coordinate in the middle of the array space
	 * @param a coordinate in the middle of the array space
	 * @param local256 Array [x][y][z][a] of the 4x4x4X4 grid around the coordinate
	 * @return Returns the quad-cubic interpolation of the given coordinate.
	 */
	public static double interpolate4d(double x, double y, double z, double a, double[][][][] local256){
		double[] section = new double[4];
		for(int i = 0; i < 4; i++){
			section[i] = interpolate3d(y,z,a,local256[i]);
		}
		return interpolate1d(x,section[0],section[1],section[2],section[3]);
	}
	/**
	 * Interpolate with cubic approximation for a point X on a grid. X 
	 * must lie between the X values of the yn1 and yp1 control points.
	 * @param x x coordinate to interpolate
	 * @param yn2 Y value at f(floor(x)-2)
	 * @param yn1 Y value at f(floor(x)-1)
	 * @param yp1 Y value at f(floor(x)+1)
	 * @param yp2 Y value at f(floor(x)+2)
	 * @return A cubic-interpolated value from the given control points.
	 */
	public static float interpolate1d(float x, float yn2, float yn1, float yp1, float yp2){
		float w = x - subfloor(x);
		// adapted from http://www.paulinternet.nl/?page=bicubic
		
//		original
//		double A = -0.5 * yn2 + 1.5 * yn1 - 1.5 * yp1 + 0.5 * yp2;
//		double B = yn2 - 2.5 * yn1 + 2 * yp1 - 0.5 * yp2;
//		double C = -0.5 * yn2 + 0.5 * yp1;
//		double D = yn1;
//		return A * w * w * w + B * w * w + C * w + D;
		
		// optimized
		float O1 = -0.5f * yn2;
		float O2 = 0.5f * yp2;
		float O3 = w * w;
		float A = O1 + 1.5f * yn1 - 1.5f * yp1 + O2;
		float B = yn2 - 2.5f * yn1 + 2f * yp1 - O2;
		float C = O1 + 0.5f * yp1;
		float D = yn1;
		return A * O3 * w + B * O3 + C * w + D;
	}
	/**
	 * Returns the bi-cubic interpolation of the (x,y) coordinate inide 
	 * the provided grid of control points. (x,y) is assumed to be in the 
	 * center square of the unit grid.
	 * @param x x coordinate between local16[1][y] and local16[2][y]
	 * @param y y coordinate between local16[x][1] and local16[x][2]
	 * @param local16 Array [x][y] of the 4x4 grid around the coordinate
	 * @return Returns the bi-cubic interpolation of the (x,y) coordinate.
	 */
	public static float interpolate2d(float x, float y, float[][] local16){
		float[] section = new float[4];
		for(int i = 0; i < 4; i++){
			section[i] = interpolate1d(y,local16[i][0],local16[i][1],local16[i][2],local16[i][3]);
		}
		return interpolate1d(x,section[0],section[1],section[2],section[3]);
	}
	/**
	 * Performs a tri-cubic interpolation of the (x,y,z) coordinate near 
	 * the center of the provided unit grid of surrounding control points.
	 * @param x x coordinate in the middle of the array space
	 * @param y y coordinate in the middle of the array space
	 * @param z z coordinate in the middle of the array space
	 * @param local64 Array [x][y][z] of the 4x4x4 grid around the coordinate
	 * @return Returns the tri-cubic interpolation of the given coordinate.
	 */
	public static float interpolate3d(float x, float y, float z, float[][][] local64){
		float[] section = new float[4];
		for(int i = 0; i < 4; i++){
			section[i] = interpolate2d(y,z,local64[i]);
		}
		return interpolate1d(x,section[0],section[1],section[2],section[3]);
	}
	/**
	 * Performs a quad-cubic interpolation of the (x,y,z,a) coordinate near 
	 * the center of the provided unit grid of surrounding control points.
	 * @param x coordinate in the middle of the array space
	 * @param y coordinate in the middle of the array space
	 * @param z coordinate in the middle of the array space
	 * @param a coordinate in the middle of the array space
	 * @param local256 Array [x][y][z][a] of the 4x4x4X4 grid around the coordinate
	 * @return Returns the quad-cubic interpolation of the given coordinate.
	 */
	public static float interpolate4d(float x, float y, float z, float a, float[][][][] local256){
		float[] section = new float[4];
		for(int i = 0; i < 4; i++){
			section[i] = interpolate3d(y,z,a,local256[i]);
		}
		return interpolate1d(x,section[0],section[1],section[2],section[3]);
	}

	/**
	 * Math.floor operation (as method for optimization purposes)
	 * @param x a number
	 * @return The largest integer value that is less than x
	 */
	private static int subfloor(double x) {
		int y = (int)x;
		if(x < 0){
			return y - 1;
		} else {
			return y;
		}
	}
	/**
	 * Math.floor operation (as method for optimization purposes)
	 * @param x a number
	 * @return The largest integer value that is less than x
	 */
	private static int subfloor(float x) {
		int y = (int)x;
		if(x < 0){
			return y - 1;
		} else {
			return y;
		}
	}
	/**
	 * Math.floor operation (as method for optimization purposes)
	 * @param x a number
	 * @return The largest integer value that is less than or equal to x
	 */
	private static int floor(double x) {
		int y = (int)x;
		if((double)y == x) return y;
		if(x < 0){
			return y - 1;
		} else {
			return y;
		}
	}
	
	/**
	 * Math.ceil operation (as method for optimization purposes)
	 * @param x a number
	 * @return The largest integer value that is greater than or equal to x
	 */
	private static int ceiling(double x) {
		int y = (int)x;
		if((double)y == x) return y;
		if(x < 0){
			return y;
		} else {
			return y + 1;
		}
	}
	
	/*
	public static void main(String[] args){
		java.util.Random r = new java.util.Random();
		double x;
		x = 0;System.out.println(floor(x) + " <- "+x);
		x = -1;System.out.println(floor(x) + " <- "+x);
		x = -2;System.out.println(floor(x) + " <- "+x);
		x = 1;System.out.println(floor(x) + " <- "+x);
		x = 2;System.out.println(floor(x) + " <- "+x);
		for(int i = 0; i < 15; i++){
			x = r.nextDouble() * 200 - 100;
			System.out.println(floor(x) + " <- "+x);
		}
	}
	*/
}
