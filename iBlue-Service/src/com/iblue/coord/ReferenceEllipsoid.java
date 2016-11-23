package com.iblue.coord;

public class ReferenceEllipsoid {
	/**
	 * The World Geodetic System 1984 reference ellipsoid.
	 */
	public static final ReferenceEllipsoid WGS84 = new ReferenceEllipsoid(6378137.0, 298.257223563);
	/**
	 * Geodetic Reference System 1980 ellipsoid.
	 */
	public static final ReferenceEllipsoid GRS80 = new ReferenceEllipsoid(6378137.0, 298.257222101);
	/**
	 * The World Geodetic System 1972 reference ellipsoid.
	 */
	public static final ReferenceEllipsoid WGS72 = new ReferenceEllipsoid(6378135.0, 298.26);
	/**
	 * The International 1924 reference ellipsoid, one of the earliest "global"
	 * ellipsoids.
	 */
	public static final ReferenceEllipsoid INTERNATIONAL1924 = new ReferenceEllipsoid(6378388.0, 297.0);

	private double a;

	//private double b;

	private double f;

	private double ea2;

	private double e;

	private double eb2;

	private double _semimajorAxis;

	private double _semiminorAxis;

	/**
	 * Constructs an instance of a reference ellipsoid.
	 *
	 * @param semimajorAxis
	 *            The semimajor or equatorial radius of this reference
	 *            ellipsoid, in meters.
	 * @param inverseFlattening
	 *            The reciprocal of the ellipticity or flattening of this
	 *            reference ellipsoid (dimensionless).
	 */
	public ReferenceEllipsoid(double semimajorAxis, double inverseFlattening) {
		this.a = semimajorAxis;
		this.f = 1.0 / inverseFlattening;
		// this.b = semimajorAxis * (1.0 - f);
		ea2 = f * (2.0 - f);
		e = Math.sqrt(ea2);
		eb2 = ea2 / (1.0 - ea2);
	}

	private static double sqr(final double x) {
		return x * x;
	}

	/**
	 * Returns the semimajor or equatorial radius of this reference ellipsoid.
	 *
	 * @return The semimajor radius.
	 */
	public double getSemimajorAxis() {
		return _semimajorAxis;
	}

	/**
	 * Returns the semiminor or polar radius of this reference ellipsoid.
	 *
	 * @return The semiminor radius.
	 */
	public double getsSemiminorAxis() {
		return _semiminorAxis;
	}

	/**
	 * Returns the flattening or ellipticity of this reference ellipsoid.
	 *
	 * @return The flattening.
	 */
	public double getFlattening() {
		return f;
	}

	/**
	 * Returns the (first) eccentricity of this reference ellipsoid.
	 *
	 * @return The eccentricity.
	 */
	public double getEccentricity() {
		return e;
	}

	/**
	 * Returns the square of the (first) eccentricity. This number is frequently
	 * used in ellipsoidal calculations.
	 *
	 * @return The square of the eccentricity.
	 */
	public double getEccentricitySquared() {
		return ea2;
	}

	/**
	 * Returns the square of the second eccentricity of this reference
	 * ellipsoid. This number is frequently used in ellipsoidal calculations.
	 *
	 * @return The square of the second eccentricity.
	 */
	public double getSecondEccentricitySquared() {
		return eb2;
	}

	/**
	 * Returns the <i>radius of curvature in the prime vertical</i> for this
	 * reference ellipsoid at the specified latitude.
	 *
	 * @param phi
	 *            The local latitude (radians).
	 * @return The radius of curvature in the prime vertical (meters).
	 */
	public double verticalRadiusOfCurvature(final double phi) {
		return a / Math.sqrt(1.0 - (ea2 * sqr(Math.sin(phi))));
	}

	/**
	 * Returns the <i>radius of curvature in the prime vertical</i> for this
	 * reference ellipsoid at the specified latitude.
	 *
	 * @param latitude
	 *            The local latitude.
	 * @return The radius of curvature in the prime vertical.
	 */
	public double verticalRadiusOfCurvature(final LatLong latLong) {
		return verticalRadiusOfCurvature(latLong.latitudeValue(LatLong.RADIAN_ANGLE));
	}

	/**
	 * Returns the <i>radius of curvature in the meridian<i> for this reference
	 * ellipsoid at the specified latitude.
	 *
	 * @param phi
	 *            The local latitude (in radians).
	 * @return The radius of curvature in the meridian (in meters).
	 */
	public double meridionalRadiusOfCurvature(final double phi) {
		return verticalRadiusOfCurvature(phi) / (1.0 + eb2 * sqr(Math.cos(phi)));
	}

	/**
	 * Returns the <i>radius of curvature in the meridian<i> for this reference
	 * ellipsoid at the specified latitude.
	 *
	 * @param latitude
	 *            The local latitude (in radians).
	 * @return The radius of curvature in the meridian (in meters).
	 */
	public double meridionalRadiusOfCurvature(final LatLong latLong) {
		return meridionalRadiusOfCurvature(latLong.latitudeValue(LatLong.RADIAN_ANGLE));
	}

	/**
	 * Returns the meridional arc, the true meridional distance on the ellipsoid
	 * from the equator to the specified latitude, in meters.
	 *
	 * @param phi
	 *            The local latitude (in radians).
	 * @return The meridional arc (in meters).
	 */
	public double meridionalArc(final double phi) {
		final double sin2Phi = Math.sin(2.0 * phi);
		final double sin4Phi = Math.sin(4.0 * phi);
		final double sin6Phi = Math.sin(6.0 * phi);
		final double sin8Phi = Math.sin(8.0 * phi);
		final double n = f / (2.0 - f);
		final double n2 = n * n;
		final double n3 = n2 * n;
		final double n4 = n3 * n;
		final double n5 = n4 * n;
		final double n1n2 = n - n2;
		final double n2n3 = n2 - n3;
		final double n3n4 = n3 - n4;
		final double n4n5 = n4 - n5;
		final double ap = a * (1.0 - n + (5.0 / 4.0) * (n2n3) + (81.0 / 64.0) * (n4n5));
		final double bp = (3.0 / 2.0) * a * (n1n2 + (7.0 / 8.0) * (n3n4) + (55.0 / 64.0) * n5);
		final double cp = (15.0 / 16.0) * a * (n2n3 + (3.0 / 4.0) * (n4n5));
		final double dp = (35.0 / 48.0) * a * (n3n4 + (11.0 / 16.0) * n5);
		final double ep = (315.0 / 512.0) * a * (n4n5);
		return ap * phi - bp * sin2Phi + cp * sin4Phi - dp * sin6Phi + ep * sin8Phi;
	}

	/**
	 * Returns the meridional arc, the true meridional distance on the ellipsoid
	 * from the equator to the specified latitude.
	 *
	 * @param latitude
	 *            The local latitude.
	 * @return The meridional arc.
	 */
	public double meridionalArc(final LatLong latLong) {
		return meridionalArc(latLong.latitudeValue(LatLong.RADIAN_ANGLE));
	}
}
