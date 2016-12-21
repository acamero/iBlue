package test.iblue.coord;

import static org.junit.Assert.*;

import org.junit.Test;

import com.iblue.coord.LatLong;
import com.iblue.coord.ReferenceEllipsoid;
import com.iblue.coord.UTM;

public class UTMTest {

	private int longitudeZone = 17;
	private char latitudeZone = 'T';
	private double easting = 630084d, northing = 4833438d;
	private double latitude = 43.642567d, longitude = -79.387139d;
	
	
	@Test
	public void toUtm() {		
		UTM utm = UTM.latLongToUtm(LatLong.valueOf(latitude, longitude, LatLong.DEGREE_ANGLE),
				ReferenceEllipsoid.WGS84);
		assertEquals(latitudeZone, utm.latitudeZone());
		assertEquals(longitudeZone,utm.longitudeZone());
		assertEquals(easting, utm.eastingValue(), 1d);
		assertEquals(northing, utm.northingValue(), 1d);
	}

}
