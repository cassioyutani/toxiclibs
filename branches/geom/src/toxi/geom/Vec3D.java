/* 
 * Copyright (c) 2006, 2007 Karsten Schmidt
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * http://creativecommons.org/licenses/LGPL/2.1/
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 */

package toxi.geom;

import java.util.Random;

import toxi.math.MathUtils;
import toxi.math.InterpolateStrategy;

/**
 * Comprehensive 3D vector class with additional basic intersection and
 * collision detection features.
 * 
 * @author Karsten Schmidt
 * 
 */
public class Vec3D implements Comparable {

	/**
	 * Defines positive X axis
	 */
	public static final Vec3D X_AXIS = new Vec3D(1, 0, 0);

	/**
	 * Defines positive Y axis
	 */
	public static final Vec3D Y_AXIS = new Vec3D(0, 1, 0);

	/**
	 * Defines positive Z axis
	 */
	public static final Vec3D Z_AXIS = new Vec3D(0, 0, 1);

	/**
	 * X coordinate
	 */
	public float x;

	/**
	 * Y coordinate
	 */
	public float y;

	/**
	 * Z coordinate
	 */
	public float z;

	/**
	 * Creates a new zero vect `or
	 */
	public Vec3D() {
		x = y = z = 0;
	}

	/**
	 * Creates a new vector with the given coordinates
	 * 
	 * @param x
	 * @param y
	 * @param z
	 */
	public Vec3D(float x, float y, float z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	/**
	 * Creates a new vector with the coordinates of the given vector
	 * 
	 * @param v
	 *            vector to be copied
	 */
	public Vec3D(Vec3D v) {
		set(v);
	}

	/**
	 * @return a new independent instance/copy of a given vector
	 */
	public final Vec3D copy() {
		return new Vec3D(this);
	}

	/**
	 * Sets all vector components to 0.
	 * 
	 * @return itself
	 */
	public final Vec3D clear() {
		x = y = z = 0;
		return this;
	}

	/**
	 * Overrides coordinates with the ones of the given vector
	 * 
	 * @param v
	 *            vector to be copied
	 * @return itself
	 */
	public final Vec3D set(Vec3D v) {
		x = v.x;
		y = v.y;
		z = v.z;
		return this;
	}

	/**
	 * Overrides coordinates with the given values
	 * 
	 * @param x
	 * @param y
	 * @param z
	 * @return itself
	 */
	public final Vec3D set(float x, float y, float z) {
		this.x = x;
		this.y = y;
		this.z = z;
		return this;
	}

	/**
	 * Checks if vector has a magnitude of 0
	 * 
	 * @return true, if vector = {0,0,0}
	 */
	public final boolean isZeroVector() {
		return x == 0 && y == 0 && z == 0;
		// return magnitude()<FastMath.EPS;
	}

	/**
	 * Produces the normalized version as a new vector
	 * 
	 * @return new vector
	 */
	public final Vec3D getNormalized() {
		return new Vec3D(this).normalize();
	}

	/**
	 * Normalizes the vector so that its magnitude = 1
	 * 
	 * @return itself
	 */
	public final Vec3D normalize() {
		float mag = MathUtils.sqrt(x * x + y * y + z * z);
		if (mag > 0) {
			mag = 1f / mag;
			x *= mag;
			y *= mag;
			z *= mag;
		}
		return this;
	}

	/**
	 * Creates a copy of the vector with its magnitude limited to the length
	 * given
	 * 
	 * @param lim
	 *            new maximum magnitude
	 * @return result as new vector
	 */
	public final Vec3D getLimited(float lim) {
		if (magSquared() > lim * lim) {
			return getNormalized().scaleSelf(lim);
		}
		return new Vec3D(this);
	}

	/**
	 * Limits the vector's magnitude to the length given
	 * 
	 * @param lim
	 *            new maximum magnitude
	 * @return itself
	 */
	public final Vec3D limit(float lim) {
		if (magSquared() > lim * lim) {
			return normalize().scaleSelf(lim);
		}
		return this;
	}

	/**
	 * Forcefully fits the vector in the given AABB.
	 * 
	 * @param box
	 * @return itself
	 */
	public final Vec3D constrain(AABB box) {
		x = MathUtils.max(MathUtils.min(x, box.maxX()), box.minX());
		y = MathUtils.max(MathUtils.min(y, box.maxY()), box.minY());
		z = MathUtils.max(MathUtils.min(z, box.maxZ()), box.minZ());
		return this;
	}

	/**
	 * Creates a copy of the vector which forcefully fits in the given AABB.
	 * 
	 * @param box
	 * @return fitted vector
	 */
	public final Vec3D getConstrained(AABB box) {
		return new Vec3D(MathUtils
				.max(MathUtils.min(x, box.maxX()), box.minX()), MathUtils.max(
				MathUtils.min(y, box.maxY()), box.minY()), MathUtils.max(
				MathUtils.min(z, box.maxZ()), box.minZ()));
	}

	/**
	 * Calculates the magnitude/eucledian length of the vector
	 * 
	 * @return vector length
	 */
	public final float magnitude() {
		return MathUtils.sqrt(x * x + y * y + z * z);
	}

	/**
	 * Calculates only the squared magnitude/length of the vector. Useful for
	 * inverse square law applications and/or for speed reasons or if the real
	 * eucledian distance is not required (e.g. sorting).
	 * 
	 * @return squared magnitude (x^2 + y^2 + z^2)
	 */
	public final float magSquared() {
		return x * x + y * y + z * z;
	}

	/**
	 * Calculates distance to another vector
	 * 
	 * @param v
	 *            non-null vector
	 * @return distance or Float.NaN if v=null
	 */
	public final float distanceTo(Vec3D v) {
		if (v != null) {
			float dx = x - v.x;
			float dy = y - v.y;
			float dz = z - v.z;
			return MathUtils.sqrt(dx * dx + dy * dy + dz * dz);
		} else {
			return Float.NaN;
		}
	}

	/**
	 * Calculates the squared distance to another vector
	 * 
	 * @see #magSquared()
	 * @param v
	 *            non-null vector
	 * @return distance or NaN if v=null
	 */
	public final float distanceToSquared(Vec3D v) {
		if (v != null) {
			float dx = x - v.x;
			float dy = y - v.y;
			float dz = z - v.z;
			return dx * dx + dy * dy + dz * dz;
		} else {
			return Float.NaN;
		}
	}

	/**
	 * Subtracts vector v and returns result as new vector.
	 * 
	 * @param v
	 *            vector to be subtracted
	 * @return result as new vector
	 */
	public final Vec3D sub(Vec3D v) {
		return new Vec3D(x - v.x, y - v.y, z - v.z);
	}

	/**
	 * Subtracts vector {a,b,c} and returns result as new vector.
	 * 
	 * @param a
	 *            X coordinate
	 * @param b
	 *            Y coordinate
	 * @param c
	 *            Z coordinate
	 * @return result as new vector
	 */
	public final Vec3D sub(float a, float b, float c) {
		return new Vec3D(x - a, y - b, z - c);
	}

	/**
	 * Subtracts vector v and overrides coordinates with result.
	 * 
	 * @param v
	 *            vector to be subtracted
	 * @return itself
	 */
	public final Vec3D subSelf(Vec3D v) {
		x -= v.x;
		y -= v.y;
		z -= v.z;
		return this;
	}

	/**
	 * Subtracts vector {a,b,c} and overrides coordinates with result.
	 * 
	 * @param a
	 *            X coordinate
	 * @param b
	 *            Y coordinate
	 * @param c
	 *            Z coordinate
	 * @return itself
	 */
	public final Vec3D subSelf(float a, float b, float c) {
		x -= a;
		y -= b;
		z -= c;
		return this;
	}

	/**
	 * Add vector v and returns result as new vector.
	 * 
	 * @param v
	 *            vector to add
	 * @return result as new vector
	 */
	public final Vec3D add(Vec3D v) {
		return new Vec3D(x + v.x, y + v.y, z + v.z);
	}

	/**
	 * Adds vector {a,b,c} and returns result as new vector.
	 * 
	 * @param a
	 *            X coordinate
	 * @param b
	 *            Y coordinate
	 * @param c
	 *            Z coordinate
	 * @return result as new vector
	 */
	public final Vec3D add(float a, float b, float c) {
		return new Vec3D(x + a, y + b, z + c);
	}

	/**
	 * Adds vector v and overrides coordinates with result.
	 * 
	 * @param v
	 *            vector to add
	 * @return itself
	 */
	public final Vec3D addSelf(Vec3D v) {
		x += v.x;
		y += v.y;
		z += v.z;
		return this;
	}

	/**
	 * Adds vector {a,b,c} and overrides coordinates with result.
	 * 
	 * @param a
	 *            X coordinate
	 * @param b
	 *            Y coordinate
	 * @param c
	 *            Z coordinate
	 * @return itself
	 */
	public final Vec3D addSelf(float a, float b, float c) {
		x += a;
		y += b;
		z += c;
		return this;
	}

	/**
	 * Scales vector uniformly and returns result as new vector.
	 * 
	 * @param s
	 *            scale factor
	 * @return new vector
	 */
	public final Vec3D scale(float s) {
		return new Vec3D(x * s, y * s, z * s);
	}

	/**
	 * Scales vector non-uniformly and returns result as new vector.
	 * 
	 * @param a
	 *            scale factor for X coordinate
	 * @param b
	 *            scale factor for Y coordinate
	 * @param c
	 *            scale factor for Z coordinate
	 * @return new vector
	 */
	public final Vec3D scale(float a, float b, float c) {
		return new Vec3D(x * a, y * b, z * c);
	}

	/**
	 * Scales vector non-uniformly by vector v and returns result as new vector
	 * 
	 * @param s
	 *            scale vector
	 * @return new vector
	 */
	public final Vec3D scale(Vec3D s) {
		return new Vec3D(x * s.x, y * s.y, z * s.z);
	}

	/**
	 * Scales vector non-uniformly by vector v and overrides coordinates with
	 * result
	 * 
	 * @param s
	 *            scale vector
	 * @return itself
	 */

	public final Vec3D scaleSelf(Vec3D s) {
		x *= s.x;
		y *= s.y;
		z *= s.z;
		return this;
	}

	/**
	 * Scales vector uniformly and overrides coordinates with result
	 * 
	 * @param s
	 *            scale factor
	 * @return itself
	 */
	public final Vec3D scaleSelf(float s) {
		x *= s;
		y *= s;
		z *= s;
		return this;
	}

	/**
	 * Scales vector non-uniformly by vector {a,b,c} and overrides coordinates
	 * with result
	 * 
	 * @param a
	 *            scale factor for X coordinate
	 * @param b
	 *            scale factor for Y coordinate
	 * @param c
	 *            scale factor for Z coordinate
	 * @return itself
	 */
	public final Vec3D scaleSelf(float a, float b, float c) {
		x *= a;
		y *= b;
		z *= c;
		return this;
	}

	/**
	 * Scales vector uniformly by factor -1 ( v = -v ), overrides coordinates
	 * with result
	 * 
	 * @return itself
	 */
	public final Vec3D invert() {
		x *= -1;
		y *= -1;
		z *= -1;
		return this;
	}

	/**
	 * Scales vector uniformly by factor -1 ( v = -v )
	 * 
	 * @return result as new vector
	 */
	public final Vec3D getInverted() {
		return new Vec3D(-x, -y, -z);
	}

	/**
	 * Creates a new vector whose components are the integer value of their
	 * current values
	 * 
	 * @return result as new vector
	 */
	public final Vec3D getFloored() {
		return new Vec3D(MathUtils.fastFloor(x), MathUtils.fastFloor(y),
				MathUtils.fastFloor(z));
	}

	/**
	 * Replaces the vector components with integer values of their current
	 * values
	 * 
	 * @return itself
	 */
	public final Vec3D floor() {
		x = MathUtils.fastFloor(x);
		y = MathUtils.fastFloor(y);
		z = MathUtils.fastFloor(z);
		return this;
	}

	/**
	 * Creates a new vector whose components are the fractional part of their
	 * current values
	 * 
	 * @return result as new vector
	 */
	public final Vec3D getFrac() {
		return new Vec3D((float) (x - Math.floor(x)), (float) (y - Math
				.floor(y)), (float) (z - Math.floor(z)));
	}

	/**
	 * Replaces the vector components with the fractional part of their current
	 * values
	 * 
	 * @return itself
	 */
	public final Vec3D frac() {
		x -= MathUtils.fastFloor(x);
		y -= MathUtils.fastFloor(y);
		z -= MathUtils.fastFloor(z);
		return this;
	}

	/**
	 * Applies a uniform modulo operation to the vector, using the same base for
	 * all components.
	 * 
	 * @param base
	 * @return itself
	 */
	public final Vec3D modSelf(float base) {
		x %= base;
		y %= base;
		z %= base;
		return this;
	}

	/**
	 * Calculates modulo operation for each vector component separately.
	 * 
	 * @param bx
	 * @param by
	 * @param bz
	 * @return itself
	 */

	public final Vec3D modSelf(float bx, float by, float bz) {
		x %= bx;
		y %= by;
		z %= bz;
		return this;
	}

	/**
	 * Constructs a new vector consisting of the smallest components of both
	 * vectors.
	 * 
	 * @param b
	 *            comparing vector
	 * @return result as new vector
	 */
	public static final Vec3D min(Vec3D a, Vec3D b) {
		return new Vec3D(MathUtils.min(a.x, b.x), MathUtils.min(a.y, b.y),
				MathUtils.min(a.z, b.z));
	}

	/**
	 * Constructs a new vector consisting of the largest components of both
	 * vectors.
	 * 
	 * @param b
	 * @return result as new vector
	 */
	public static final Vec3D max(Vec3D a, Vec3D b) {
		return new Vec3D(MathUtils.max(a.x, b.x), MathUtils.max(a.y, b.y),
				MathUtils.max(a.z, b.z));
	}

	/**
	 * Calculates cross-product with vector v. The resulting vector is
	 * perpendicular to both the current and supplied vector.
	 * 
	 * @param v
	 *            vector to cross
	 * @return cross-product as new vector
	 */
	public final Vec3D cross(Vec3D v) {
		return new Vec3D(y * v.z - v.y * z, z * v.x - v.z * x, x * v.y - v.x
				* y);
	}

	/**
	 * Calculates cross-product with vector v. The resulting vector is
	 * perpendicular to both the current and supplied vector and overrides the
	 * current.
	 * 
	 * @param v
	 * @return itself
	 */
	public final Vec3D crossSelf(Vec3D v) {
		float cx = y * v.z - v.y * z;
		float cy = z * v.x - v.z * x;
		z = x * v.y - v.x * y;
		y = cy;
		x = cx;
		return this;
	}

	/**
	 * Calculates cross-product with vector v. The resulting vector is
	 * perpendicular to both the current and supplied vector and stored in the
	 * supplied result vector.
	 * 
	 * @param v
	 *            vector to cross
	 * @param result
	 *            result vector
	 * @return result vector
	 */
	public final Vec3D crossInto(Vec3D v, Vec3D result) {
		float rx = y * v.z - v.y * z;
		float ry = z * v.x - v.z * x;
		float rz = x * v.y - v.x * y;
		result.set(rx, ry, rz);
		return result;
	}

	/**
	 * Computes the scalar product (dot product) with the given vector.
	 * 
	 * @see <a href="http://en.wikipedia.org/wiki/Dot_product">Wikipedia entry</a>
	 * 
	 * @param v
	 * @return dot product
	 */
	public final float dot(Vec3D v) {
		return x * v.x + y * v.y + z * v.z;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		StringBuffer sb = new StringBuffer(32);
		sb.append("{x:").append(x).append(", y:").append(y).append(", z:")
				.append(z).append("}");
		return sb.toString();
	}

	/**
	 * Interpolates the vector towards the given target vector, using linear
	 * interpolation
	 * 
	 * @param v
	 *            target vector
	 * @param f
	 *            interpolation factor (should be in the range 0..1)
	 * @return result as new vector
	 */
	public final Vec3D interpolateTo(Vec3D v, float f) {
		return new Vec3D(x + (v.x - x) * f, y + (v.y - y) * f, z + (v.z - z)
				* f);
	}

	/**
	 * Interpolates the vector towards the given target vector, using the given
	 * {@link InterpolateStrategy}
	 * 
	 * @param v
	 *            target vector
	 * @param f
	 *            interpolation factor (should be in the range 0..1)
	 * @param s
	 *            InterpolateStrategy instance
	 * @return result as new vector
	 */
	public Vec3D interpolateTo(Vec3D v, float f, InterpolateStrategy s) {
		return new Vec3D(s.interpolate(x, v.x, f), s.interpolate(y, v.y, f), s
				.interpolate(z, v.z, f));
	}

	/**
	 * Interpolates the vector towards the given target vector, using linear
	 * interpolation
	 * 
	 * @param v
	 *            target vector
	 * @param f
	 *            interpolation factor (should be in the range 0..1)
	 * @return itself, result overrides current vector
	 */
	public final Vec3D interpolateToSelf(Vec3D v, float f) {
		x += (v.x - x) * f;
		y += (v.y - y) * f;
		z += (v.z - z) * f;
		return this;
	}

	/**
	 * Interpolates the vector towards the given target vector, using the given
	 * {@link InterpolateStrategy}
	 * 
	 * @param v
	 *            target vector
	 * @param f
	 *            interpolation factor (should be in the range 0..1)
	 * @param s
	 *            InterpolateStrategy instance
	 * @return itself, result overrides current vector
	 */
	public Vec3D interpolateToSelf(Vec3D v, float f, InterpolateStrategy s) {
		x = s.interpolate(x, v.x, f);
		y = s.interpolate(y, v.y, f);
		z = s.interpolate(z, v.z, f);
		return this;
	}

	/**
	 * Computes the angle between this vector and vector V. This function
	 * assumes both vectors are normalized, if this can't be guaranteed, use the
	 * alternative implementation {@link #angleBetween(Vec3D, boolean)}
	 * 
	 * @param v
	 *            vector
	 * @return angle in radians, or NaN if vectors are parallel
	 */
	public final float angleBetween(Vec3D v) {
		return (float) Math.acos(dot(v));
	}

	/**
	 * Computes the angle between this vector and vector V
	 * 
	 * @param v
	 *            vector
	 * @param forceNormalize
	 *            true, if normalized versions of the vectors are to be used
	 *            (Note: only copies will be used, original vectors will not be
	 *            altered by this method)
	 * @return angle in radians, or NaN if vectors are parallel
	 */
	public final float angleBetween(Vec3D v, boolean forceNormalize) {
		float theta;
		if (forceNormalize) {
			theta = getNormalized().dot(v.getNormalized());
		} else {
			theta = dot(v);
		}
		return (float) Math.acos(theta);
	}

	/**
	 * Computes the vector's direction in the XY plane (for example for 2D
	 * points). The positive X axis equals 0 degrees.
	 * 
	 * @return rotation angle
	 */
	public final float headingXY() {
		return (float) Math.atan2(y, x);
	}

	/**
	 * Computes the vector's direction in the XZ plane. The positive X axis
	 * equals 0 degrees.
	 * 
	 * @return rotation angle
	 */
	public final float headingXZ() {
		return (float) Math.atan2(z, x);
	}

	/**
	 * Computes the vector's direction in the YZ plane. The positive Z axis
	 * equals 0 degrees.
	 * 
	 * @return rotation angle
	 */
	public final float headingYZ() {
		return (float) Math.atan2(y, z);
	}

	/**
	 * Rotates the vector by the given angle around the X axis.
	 * 
	 * @param theta
	 * @return itself
	 */
	public final Vec3D rotateX(float theta) {
		float co = (float) Math.cos(theta);
		float si = (float) Math.sin(theta);
		float yy = co * z - si * y;
		z = si * z + co * y;
		y = yy;
		return this;
	}

	/**
	 * Creates a new vector rotated by the given angle around the X axis.
	 * 
	 * @param theta
	 * @return rotated vector
	 */
	public final Vec3D getRotatedX(float theta) {
		return new Vec3D(this).rotateX(theta);
	}

	/**
	 * Rotates the vector by the given angle around the Y axis.
	 * 
	 * @param theta
	 * @return itself
	 */
	public final Vec3D rotateY(float theta) {
		float co = (float) Math.cos(theta);
		float si = (float) Math.sin(theta);
		float xx = co * x - si * z;
		z = si * x + co * z;
		x = xx;
		return this;
	}

	/**
	 * Creates a new vector rotated by the given angle around the Y axis.
	 * 
	 * @param theta
	 * @return rotated vector
	 */
	public final Vec3D getRotatedY(float theta) {
		return new Vec3D(this).rotateY(theta);
	}

	/**
	 * Rotates the vector by the given angle around the Z axis.
	 * 
	 * @param theta
	 * @return itself
	 */
	public final Vec3D rotateZ(float theta) {
		float co = (float) Math.cos(theta);
		float si = (float) Math.sin(theta);
		float xx = co * x - si * y;
		y = si * x + co * y;
		x = xx;
		return this;
	}

	/**
	 * Creates a new vector rotated by the given angle around the Z axis.
	 * 
	 * @param theta
	 * @return rotated vector
	 */
	public final Vec3D getRotatedZ(float theta) {
		return new Vec3D(this).rotateZ(theta);
	}

	/**
	 * Rotates the vector around the giving axis
	 * 
	 * @param axis
	 *            rotation axis vector
	 * @param theta
	 *            rotation angle (in radians)
	 * @return
	 */
	public final Vec3D rotateAroundAxis(Vec3D axis, float theta) {
		float ux = axis.x * x;
		float uy = axis.x * y;
		float uz = axis.x * z;
		float vx = axis.y * x;
		float vy = axis.y * y;
		float vz = axis.y * z;
		float wx = axis.z * x;
		float wy = axis.z * y;
		float wz = axis.z * z;
		double si = Math.sin(theta);
		double co = Math.cos(theta);
		float xx = (float) (axis.x
				* (ux + vy + wz)
				+ (x * (axis.y * axis.y + axis.z * axis.z) - axis.x * (vy + wz))
				* co + (-wy + vz) * si);
		float yy = (float) (axis.y
				* (ux + vy + wz)
				+ (y * (axis.x * axis.x + axis.z * axis.z) - axis.y * (ux + wz))
				* co + (wx - uz) * si);
		float zz = (float) (axis.z
				* (ux + vy + wz)
				+ (z * (axis.x * axis.x + axis.y * axis.y) - axis.z * (ux + vy))
				* co + (-vx + uy) * si);
		return new Vec3D(xx, yy, zz);
	}

	// intersection code below is adapted from C version at
	// http://www.peroxide.dk/

	/**
	 * Calculates the distance of the vector to the given plane in the specified
	 * direction. A plane is specified by a 3D point and a normal vector
	 * perpendicular to the plane. Normalized directional vectors expected (for
	 * rayDir and planeNormal).
	 * 
	 * @param rayDir
	 *            intersection direction
	 * @param planeOrigin
	 * @param planeNormal
	 * @return distance to plane in world units, -1 if no intersection.
	 */
	// FIXME this is kind of obsolete since the arrival of the Plane class, but
	// needs amends to reflector code
	public float intersectRayPlane(Vec3D rayDir, Vec3D planeOrigin,
			Vec3D planeNormal) {
		float d = -planeNormal.dot(planeOrigin);
		float numer = planeNormal.dot(this) + d;
		float denom = planeNormal.dot(rayDir);

		// normal is orthogonal to vector, cant intersect
		if (MathUtils.abs(denom) < MathUtils.EPS)
			return -1;

		return -(numer / denom);
	}

	/**
	 * Calculates the distance of the vector to the given sphere in the
	 * specified direction. A sphere is defined by a 3D point and a radius.
	 * Normalized directional vectors expected.
	 * 
	 * @param rayDir
	 *            intersection direction
	 * @param sphereOrigin
	 * @param sphereRadius
	 * @return distance to sphere in world units, -1 if no intersection.
	 */

	// FIXME this really should be part of either Sphere or
	// SphereIntersectorReflector
	public float intersectRaySphere(Vec3D rayDir, Vec3D sphereOrigin,
			float sphereRadius) {
		Vec3D q = sphereOrigin.sub(this);
		float c = q.magnitude();
		float v = q.dot(rayDir);
		float d = sphereRadius * sphereRadius - (c * c - v * v);

		// If there was no intersection, return -1
		if (d < 0.0)
			return -1;

		// Return the distance to the [first] intersecting point
		return v - (float) Math.sqrt(d);
	}

	/**
	 * 
	 * Helper function for {@link #closestPointOnTriangle(Vec3D, Vec3D, Vec3D)}
	 * 
	 * @param a
	 *            start point of line segment
	 * @param b
	 *            end point of line segment
	 * @return closest point on the line segment a -> b
	 */

	public Vec3D closestPointOnLine(Vec3D a, Vec3D b) {
		// Determine t (the length of the vector from �a� to 'this')
		Vec3D c = sub(a);
		Vec3D v = b.sub(a);

		float d = v.magnitude();
		v.normalize();

		float t = v.dot(c);

		// Check to see if t is beyond the extents of the line segment
		if (t < 0.0f)
			return a;
		if (t > d)
			return b;

		// Return the point between 'a' and 'b'
		// set length of V to t. V is normalized so this is easy
		v.scale(t);

		return a.add(v);
	}

	/**
	 * Checks if the point is inside the given sphere.
	 * 
	 * @param sO
	 *            sphere origin/centre
	 * @param sR
	 *            sphere radius
	 * @return true, if point is in sphere
	 */
	// FIXME move to Sphere
	public boolean isInSphere(Vec3D sO, float sR) {
		float d = this.sub(sO).magSquared();
		return (d <= sR * sR);
	}

	/**
	 * Checks if the point is inside the given sphere.
	 * 
	 * @param s
	 *            bounding sphere to check
	 * @return true, if point is inside
	 */
	// FIXME move to Sphere
	public boolean isInSphere(Sphere s) {
		float d = this.sub(s).magSquared();
		return (d <= s.radius * s.radius);
	}

	/**
	 * Checks if the point is inside the given axis-aligned bounding box.
	 * 
	 * @param bO
	 *            bounding box origin/center
	 * @param bDim
	 *            bounding box extends (half measure)
	 * @return true, if point is inside the box
	 */

	public boolean isInAABB(Vec3D bO, Vec3D bDim) {
		float w = bDim.x;
		if (x < bO.x - w || x > bO.x + w)
			return false;
		w = bDim.y;
		if (y < bO.y - w || y > bO.y + w)
			return false;
		w = bDim.z;
		if (z < bO.z - w || z > bO.z + w)
			return false;
		return true;
	}

	/**
	 * Checks if the point is inside the given AABB.
	 * 
	 * @param box
	 *            bounding box to check
	 * @return true, if point is inside
	 */
	public boolean isInAABB(AABB box) {
		Vec3D min = box.getMin();
		Vec3D max = box.getMax();
		if (x < min.x || x > max.x)
			return false;
		if (y < min.y || y > max.y)
			return false;
		if (z < min.z || z > max.z)
			return false;
		return true;
	}

	/**
	 * Calculates the normal vector on the given ellipsoid in the direction of
	 * the current point.
	 * 
	 * @param eO
	 *            ellipsoid origin/centre
	 * @param eR
	 *            ellipsoid radius
	 * @return a unit normal vector to the tangent plane of the ellipsoid in the
	 *         point.
	 */

	public Vec3D tangentPlaneNormalOfEllipsoid(Vec3D eO, Vec3D eR) {
		Vec3D p = this.sub(eO);

		float xr2 = eR.x * eR.x;
		float yr2 = eR.y * eR.y;
		float zr2 = eR.z * eR.z;

		return new Vec3D(p.x / xr2, p.y / yr2, p.z / zr2).normalize();
	}

	/**
	 * Considers the current vector as centre of a collision sphere with radius
	 * r and checks if the triangle abc intersects with this sphere. The Vec3D p
	 * The point on abc closest to the sphere center is returned via the
	 * supplied result vector argument.
	 * 
	 * @param r
	 *            collision sphere radius
	 * @param a
	 *            triangle vertex
	 * @param b
	 *            triangle vertex
	 * @param c
	 *            triangle vertex
	 * @param result
	 *            a non-null vector for storing the result
	 * @return true, if sphere intersects triangle ABC
	 */
	// FIXME this needs to be moved out from Vec3D in either
	public boolean intersectSphereTriangle(float r, Vec3D a, Vec3D b, Vec3D c,
			Vec3D result) {
		// Find Vec3D P on triangle ABC closest to sphere center
		result.set(new Triangle(a, b, c).closestPoint(this));

		// Sphere and triangle intersect if the (squared) distance from sphere
		// center to Vec3D p is less than the (squared) sphere radius
		Vec3D v = result.sub(this);
		return v.magSquared() <= r * r;
	}

	/**
	 * Static factory method. Creates a new random unit vector using the default
	 * Math.random() Random instance.
	 * 
	 * @return a new random normalized unit vector.
	 */
	public static final Vec3D randomVector() {
		Vec3D rnd = new Vec3D((float) Math.random() * 2 - 1, (float) Math
				.random() * 2 - 1, (float) Math.random() * 2 - 1);
		return rnd.normalize();
	}

	/**
	 * Static factory method. Creates a new random unit vector using the given
	 * Random generator instance. I recommend to have a look at the
	 * https://uncommons-maths.dev.java.net library for a good choice of
	 * reliable and high quality random number generators.
	 * 
	 * @return a new random normalized unit vector.
	 */
	public static final Vec3D randomVector(Random rnd) {
		Vec3D v = new Vec3D(rnd.nextFloat() * 2 - 1, rnd.nextFloat() * 2 - 1,
				rnd.nextFloat() * 2 - 1);
		return v.normalize();
	}

	/**
	 * Creates a new vector from the given angle in the XY plane. The Z
	 * component of the vector will be zero.
	 * 
	 * The resulting vector for theta=0 is equal to the positive X axis.
	 * 
	 * @param theta
	 * @return
	 */
	public static final Vec3D fromXYTheta(float theta) {
		return new Vec3D((float) Math.cos(theta), (float) Math.sin(theta), 0);
	}

	/**
	 * Creates a new vector from the given angle in the XZ plane. The Y
	 * component of the vector will be zero.
	 * 
	 * The resulting vector for theta=0 is equal to the positive X axis.
	 * 
	 * @param theta
	 * @return
	 */
	public static final Vec3D fromXZTheta(float theta) {
		return new Vec3D((float) Math.cos(theta), 0, (float) Math.sin(theta));
	}

	/**
	 * Creates a new vector from the given angle in the YZ plane. The X
	 * component of the vector will be zero.
	 * 
	 * The resulting vector for theta=0 is equal to the positive Y axis.
	 * 
	 * @param theta
	 * @return
	 */
	public static final Vec3D fromYZTheta(float theta) {
		return new Vec3D(0, (float) Math.cos(theta), (float) Math.sin(theta));
	}

	/**
	 * Replaces all vector components with the signum of their original values.
	 * In other words if a components value was negative its new value will be
	 * -1, if zero => 0, if positive => +1
	 * 
	 * @return itself
	 */
	public Vec3D signum() {
		x = (x < 0 ? -1 : x == 0 ? 0 : 1);
		y = (y < 0 ? -1 : y == 0 ? 0 : 1);
		z = (z < 0 ? -1 : z == 0 ? 0 : 1);
		return this;
	}

	/**
	 * Creates a new vector in which all components are replaced with the signum
	 * of their original values. In other words if a components value was
	 * negative its new value will be -1, if zero => 0, if positive => +1
	 * 
	 * @return result vector
	 */
	public Vec3D getSignum() {
		return new Vec3D(this).signum();
	}

	/* (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(T)
	 */
	public int compareTo(Object obj) {
		Vec3D v = (Vec3D) obj;
		if (x == v.x && y == v.y && z == v.z)
			return 0;
		if (magSquared() < v.magSquared())
			return -1;
		return 1;
	}
}