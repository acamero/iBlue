package com.iblue.coord;

public class LatLong {

	public static final int DEGREE_ANGLE = 1;
	public static final int RADIAN_ANGLE = 2;
	
	private static final double PI = 3.14159265359d;
			

	/**
	 * Holds the latitude in degrees.
	 */
	private double _latitude;

	/**
	 * Holds the longitude in degrees.
	 */
	private double _longitude;

	/**
	 * Returns the surface position corresponding to the specified coordinates.
	 * 
	 * @param latitude
	 *            the latitude value stated in the specified unit.
	 * @param longitude
	 *            the longitude value stated in the specified unit.
	 * @param unit
	 *            the angle unit in which the coordinates are stated
	 *            ({@link javax.measure.unit.NonSI#DEGREE_ANGLE Degree}
	 *            typically).
	 * @return the corresponding surface position.
	 */
	public static LatLong valueOf(double latitude, double longitude, int unit) {
		LatLong latLong = new LatLong();
		if (unit == DEGREE_ANGLE) {
			latLong._latitude = latitude;
			latLong._longitude = longitude;
		} else if (unit == RADIAN_ANGLE) {
			latLong._latitude = RadianToDegree.convert(latitude);
			latLong._longitude = RadianToDegree.convert(longitude);
		} 
		return latLong;
	}

	private LatLong() {
	}

	/**
	 * Returns the latitude value as <code>double</code>
	 * 
	 * @param unit
	 *            the angle unit of the latitude to return.
	 * @return the latitude stated in the specified unit.
	 */
	public final double latitudeValue(int unit) {
		if(unit == RADIAN_ANGLE) {
			return DegreeToRadian.convert(_latitude);
		} 
		return _latitude;		
	}

	/**
	 * Returns the longitude value as <code>double</code>
	 * 
	 * @param unit
	 *            the angle unit of the longitude to return.
	 * @return the longitude stated in the specified unit.
	 */
	public final double longitudeValue(int unit) {
		if(unit == RADIAN_ANGLE) {
			return DegreeToRadian.convert(_longitude);
		} 
		return _longitude;		
	}

	
	public LatLong copy() {
		return LatLong.valueOf(_latitude, _longitude, DEGREE_ANGLE);
	}

	public static class DegreeToRadian {
		public static double convert(double angleDegrees) {
			return ( angleDegrees / 180.0d ) * PI; 
		}
	}
	
	public static class RadianToDegree {
		public static double convert(double angleRadian) {
			return ( angleRadian / PI ) * 180.0d; 
		}
	}

}
