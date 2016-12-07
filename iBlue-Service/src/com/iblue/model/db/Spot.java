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
import com.iblue.model.SpotInterface;

@Entity
@Table(name = "spots", schema = DbSchema.DB_SCHEMA)
public class Spot implements SpotInterface {
	
	protected static final int LAT_LON_SCALE = 6;

	@Id
	@Column(name = "pk_id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;

	// Latitude and longitude in degrees
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

	// Street associated to this parking spot
	@Column(name = "fk_street_id")
	private int streetId;

	@Column(name = "str_mac", length = 45)
	private String mac;

	@Column(name = "int_status", columnDefinition = "TINYINT")
	private int status;

	@Column(name = "ts_update", insertable = false)
	private Timestamp updateTs;
	@Column(name = "ts_create", updatable = false)
	private Timestamp createTs;

	public Spot() {
	}

	private void setUTM() {
		UTM utm = UTM.latLongToUtm(LatLong.valueOf(latitude.floatValue(), longitude.floatValue(), LatLong.DEGREE_ANGLE),
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

	public void setLatLong(BigDecimal latitude, BigDecimal longitude) {
		this.latitude = latitude.setScale(LAT_LON_SCALE, BigDecimal.ROUND_HALF_DOWN);
		this.longitude = longitude.setScale(LAT_LON_SCALE, BigDecimal.ROUND_HALF_DOWN);
		setUTM();
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getMac() {
		return mac;
	}

	public void setMac(String mac) {
		this.mac = mac;
	}

	public int getId() {
		return id;
	}

	public Timestamp getUpdateTs() {
		return updateTs;
	}

	public Timestamp getCreateTs() {
		return createTs;
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

	public int getStreetId() {
		return streetId;
	}

	public void setStreetId(int streetId) {
		this.streetId = streetId;
	}

	@Override
	public String toString() {
		return "{\"latitude\":\"" + getLatitude() + "\",\"longitude\":\"" + getLongitude() + "\",\"mac\":\"" + this.mac
				+ "\",\"status\":\"" + this.status + "\",\"id\":\"" + this.id + "\"}";
	}

}
