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

/**
 * Abstract class for 3D noise generators (such as 3D perlin noise). Extending 
 * classes will use some method to generate an interpolated random value for the 
 * given coordinate.
 * @author Christopher Collin Hall
 */
public abstract class CoordinateNoiseGenerator3D {
	/**
	 * Implementations of this method will generate a Perlin Noise type of value 
	 * interpolated at the provided coordinate.
	 * @param x X coordinate
	 * @param y Y coordinate
	 * @param z Z coordinate
	 * @return A value interpolated from random control points, such that the 
	 * same coordinate always results in the same output value and a coordinate 
	 * very close to another will have a similar, but not necessarily the same, 
	 * value as the other coordinate.
	 * @throws ArrayIndexOutOfBoundsException May be thrown if the provided 
	 * coordinates exceed the allowable range of the underlying algorithm.
	 */
	public abstract double getValue(double x, double y, double z) throws ArrayIndexOutOfBoundsException;
	
}
