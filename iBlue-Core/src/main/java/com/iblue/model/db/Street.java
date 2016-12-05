package com.iblue.model.db;

import java.math.BigDecimal;
import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SqlResultSetMapping;
import javax.persistence.Table;
import javax.persistence.ConstructorResult;
import javax.persistence.ColumnResult;

import com.iblue.coord.LatLong;
import com.iblue.coord.ReferenceEllipsoid;
import com.iblue.coord.UTM;
import com.iblue.model.StreetInterface;

@SqlResultSetMapping(name = "AreaMapResultSet", classes = {
		@ConstructorResult(targetClass = StreetAvailability.class, columns = {
				@ColumnResult(name = "pk_id", type = Integer.class),
				@ColumnResult(name = "decimal_latitude_1", type = BigDecimal.class),
				@ColumnResult(name = "decimal_longitude_1", type = BigDecimal.class),
				@ColumnResult(name = "decimal_latitude_2", type = BigDecimal.class),
				@ColumnResult(name = "decimal_longitude_2", type = BigDecimal.class),
				@ColumnResult(name = "in_use_spots", type = Integer.class),
				@ColumnResult(name = "int_capacity", type = Integer.class),
				@ColumnResult(name = "int_type", type = Integer.class) }) })

@Entity
@Table(name = "streets", schema = DbSchema.DB_SCHEMA)
public class Street implements StreetInterface {
	
	protected static final int LAT_LON_SCALE = 6;

	@Id
	@Column(name = "pk_id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;

	// Start of the street
	// Latitude and longitude in degrees
	@Column(name = "decimal_latitude_1", precision=10, scale=LAT_LON_SCALE)
	private BigDecimal latitude1;
	@Column(name = "decimal_longitude_1", precision=10, scale=LAT_LON_SCALE)
	private BigDecimal longitude1;
	// UTM coordinates
	@Column(name = "float_northing_1")
	private float northing1;
	@Column(name = "float_easting_1")
	private float easting1;
	@Column(name = "int_longitude_zone_1", columnDefinition = "TINYINT")
	private int longitudeZone1;
	@Column(name = "char_latitude_zone_1")
	private char latitudeZone1;

	// End of the street
	// Latitude and longitude in degrees
	@Column(name = "decimal_latitude_2", precision=10, scale=LAT_LON_SCALE)
	private BigDecimal latitude2;
	@Column(name = "decimal_longitude_2", precision=10, scale=LAT_LON_SCALE)
	private BigDecimal longitude2;
	// UTM coordinates
	@Column(name = "float_northing_2")
	private float northing2;
	@Column(name = "float_easting_2")
	private float easting2;
	@Column(name = "int_longitude_zone_2", columnDefinition = "TINYINT")
	private int longitudeZone2;
	@Column(name = "char_latitude_zone_2")
	private char latitudeZone2;

	// Line parameters
	@Column(name = "float_line_coeff_a")
	private float lineCoeffA;
	@Column(name = "float_line_coeff_b")
	private float lineCoeffB;
	@Column(name = "float_line_coeff_c")
	private float lineCoeffC;
	@Column(name = "float_line_sqrt_a2_b2")
	private float sqrtA2B2;

	@Column(name = "int_capacity", columnDefinition = "SMALLINT")
	private int capacity;

	@Column(name = "int_type", columnDefinition = "SMALLINT")
	private int type;

	// Status=1 -> street is available
	@Column(name = "int_status", columnDefinition = "TINYINT")
	private int status;
	@Column(name = "ts_update", insertable = false)
	private Timestamp updateTs;
	@Column(name = "ts_create", updatable = false)
	private Timestamp createTs;

	private void setLineCoeff() {
		if (easting1 == easting2) {
			// x-axis
			lineCoeffA = 1.0f;
			lineCoeffB = 0.0f;
			lineCoeffC = -easting1;
		} else if (northing1 == northing2) {
			// y-axis
			lineCoeffA = 0.0f;
			lineCoeffB = 1.0f;
			lineCoeffC = -northing1;
		} else {
			lineCoeffA = (northing2 - northing1) / (easting2 - easting1);
			lineCoeffB = -1.0f;
			lineCoeffC = northing1 - lineCoeffA * easting1;
		}
		sqrtA2B2 = (float) Math.sqrt(Math.pow(lineCoeffA, 2.0d) + Math.pow(lineCoeffB, 2.0d));
	}

	public double distance(UTM utm) {
		return calcDistance(utm.eastingValue(), utm.northingValue());
	}

	private double calcDistance(double x, double y) {
		return Math.abs(lineCoeffA * x + lineCoeffB * y + lineCoeffC) / sqrtA2B2;
	}

	public double distance(LatLong latLong) {
		return distance(UTM.latLongToUtm(latLong, ReferenceEllipsoid.WGS84));
	}

	public double distance(double latitude, double longitude) {
		return distance(LatLong.valueOf(latitude, longitude, LatLong.DEGREE_ANGLE));
	}

	public int getId() {
		return id;
	}

	public void setLatLong1(BigDecimal latitude1, BigDecimal longitude1) {
		this.latitude1 = latitude1.setScale(LAT_LON_SCALE);
		this.longitude1 = longitude1.setScale(LAT_LON_SCALE);
		setUTM1();
		setLineCoeff();
	}

	private void setUTM1() {
		UTM utm = UTM.latLongToUtm(LatLong.valueOf(latitude1.doubleValue(), longitude1.doubleValue(), LatLong.DEGREE_ANGLE),
				ReferenceEllipsoid.WGS84);
		northing1 = (float) utm.northingValue();
		easting1 = (float) utm.eastingValue();
		longitudeZone1 = utm.longitudeZone();
		latitudeZone1 = utm.latitudeZone();
	}

	public BigDecimal getLatitude1() {
		return latitude1;
	}

	public BigDecimal getLongitude1() {
		return longitude1;
	}

	public float getNorthing1() {
		return northing1;
	}

	public float getEasting1() {
		return easting1;
	}

	public int getLongitudeZone1() {
		return longitudeZone1;
	}

	public char getLatitudeZone1() {
		return latitudeZone1;
	}

	public void setLatLong2(BigDecimal latitude2, BigDecimal longitude2) {
		this.latitude2 = latitude2.setScale(LAT_LON_SCALE);
		this.longitude2 = longitude2.setScale(LAT_LON_SCALE);
		setUTM2();
		setLineCoeff();
	}

	private void setUTM2() {
		UTM utm = UTM.latLongToUtm(LatLong.valueOf(latitude2.doubleValue(), longitude2.doubleValue(), LatLong.DEGREE_ANGLE),
				ReferenceEllipsoid.WGS84);
		northing2 = (float) utm.northingValue();
		easting2 = (float) utm.eastingValue();
		longitudeZone2 = utm.longitudeZone();
		latitudeZone2 = utm.latitudeZone();
	}

	public BigDecimal getLatitude2() {
		return latitude2;
	}

	public BigDecimal getLongitude2() {
		return longitude2;
	}

	public float getNorthing2() {
		return northing2;
	}

	public float getEasting2() {
		return easting2;
	}

	public int getLongitudeZone2() {
		return longitudeZone2;
	}

	public char getLatitudeZone() {
		return latitudeZone2;
	}

	public float getSqrtA2B2() {
		return sqrtA2B2;
	}

	public int getCapacity() {
		return capacity;
	}

	public void setCapacity(int capacity) {
		this.capacity = capacity;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public Timestamp getUpdateTs() {
		return updateTs;
	}

	public Timestamp getCreateTs() {
		return createTs;
	}

	public float getLineCoeffA() {
		return lineCoeffA;
	}

	public float getLineCoeffB() {
		return lineCoeffB;
	}

	public float getLineCoeffC() {
		return lineCoeffC;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public String toString() {
		return "{" + "\"latitude1\":\"" + latitude1 + "\"," + "\"longitude1\":\"" + longitude1 + "\","
				+ "\"northing1\":\"" + northing1 + "\"," + "\"easting1\":\"" + easting1 + "\"," + "\"latitudeZone1\":\""
				+ latitudeZone1 + "\"," + "\"longitudeZone1\":\"" + longitudeZone1 + "\"," +

				"\"latitude2\":\"" + latitude2 + "\"," + "\"longitude2\":\"" + longitude2 + "\"," + "\"northing2\":\""
				+ northing2 + "\"," + "\"easting2\":\"" + easting2 + "\"," + "\"latitudeZone2\":\"" + latitudeZone2
				+ "\"," + "\"longitudeZone2\":\"" + longitudeZone2 + "\"," +

				"\"lineCoeffA\":\"" + lineCoeffA + "\"," + "\"lineCoeffB\":\"" + lineCoeffB + "\","
				+ "\"lineCoeffC\":\"" + lineCoeffC + "\"," + "\"sqrtA2B2\":\"" + sqrtA2B2 + "\"," +

				"\"status\":\"" + status + "\"," + "\"capacity\":\"" + capacity + "\"," + "\"type\":\"" + type + "\"," +

				"\"id\":\"" + id + "\"" + "\"createTs\":\"" + createTs + "\"" + "\"updateTs\":\"" + updateTs + "\""
				+ "}";
	}
}
