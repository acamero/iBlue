package com.iblue.model.db;

import java.math.BigDecimal;
import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.iblue.coord.LatLong;
import com.iblue.coord.ReferenceEllipsoid;
import com.iblue.coord.UTM;

@Entity
@Table(name = "intersections", schema = DbSchema.DB_SCHEMA)
public class Intersection {

	protected static final int LAT_LON_SCALE = 7;
	
	@Id
	@Column(name = "pk_id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	@Column(name = "decimal_latitude", precision=10, scale=LAT_LON_SCALE)
	private BigDecimal latitude;
	@Column(name = "decimal_longitude", precision=10, scale=LAT_LON_SCALE)
	private BigDecimal longitude;
	// UTM coordinates
	@Column(name = "float_northing")
	private float northing;
	@Column(name = "float_easting")
	private float easting;
	@Column(name = "int_longitude_zone", columnDefinition = "TINYINT")
	private int longitudeZone;
	@Column(name = "char_latitude_zone")
	private char latitudeZone;
	@Column(name = "ts_update", insertable = false)
	private Timestamp updateTs;
	@Column(name = "ts_create", updatable = false)
	private Timestamp createTs;
	
	//@OneToMany(mappedBy = "intersections", cascade = CascadeType.ALL)
	//private Set<GeoStreet> geoStreets;
	
	public Intersection() {
		
	}
	
	public Intersection(BigDecimal latitude, BigDecimal longitude) {
		setLatLong(latitude,longitude);
	}
	
	public long getId() {
		return id;
	}

	public void setLatLong(BigDecimal latitude, BigDecimal longitude) {
		this.latitude = latitude.setScale(LAT_LON_SCALE, BigDecimal.ROUND_HALF_DOWN);
		this.longitude = longitude.setScale(LAT_LON_SCALE, BigDecimal.ROUND_HALF_DOWN);
		setUTM();
	}

	private void setUTM() {
		UTM utm = UTM.latLongToUtm(LatLong.valueOf(latitude.doubleValue(), longitude.doubleValue(), LatLong.DEGREE_ANGLE),
				ReferenceEllipsoid.WGS84);
		northing = (float) utm.northingValue();
		easting = (float) utm.eastingValue();
		longitudeZone = utm.longitudeZone();
		latitudeZone = utm.latitudeZone();
	}
	
	public BigDecimal getLatitude() {
		return latitude;
	}

	public BigDecimal getLongitude() {
		return longitude;
	}

	public float getNorthing() {
		return northing;
	}

	public float getEasting() {
		return easting;
	}

	public int getLongitudeZone() {
		return longitudeZone;
	}

	public char getLatitudeZone() {
		return latitudeZone;
	}
	
	public Timestamp getUpdateTs() {
		return updateTs;
	}

	public Timestamp getCreateTs() {
		return createTs;
	}	
	
	//public Set<GeoStreet> getGeoStreets() {
	//	return geoStreets;
	//}
	
}
