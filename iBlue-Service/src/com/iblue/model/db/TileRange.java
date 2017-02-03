package com.iblue.model.db;

import java.math.BigDecimal;
import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import com.iblue.model.db.service.DbSchema;

@Entity
@Table(name = "tile_range", schema = DbSchema.DB_SCHEMA)
public class TileRange {

	public static final int LAT_LON_SCALE = 7;

	@Id
	@Column(name = "pk_id")
	private long id;
	@Column(name = "decimal_latitude_range", precision = 10, scale = LAT_LON_SCALE)
	private BigDecimal latitudeRange;
	@Column(name = "decimal_longitude_range", precision = 10, scale = LAT_LON_SCALE)
	private BigDecimal longitudeRange;
	@Column(name = "ts_update", insertable = false)
	private Timestamp updateTs;
	@Column(name = "ts_create", updatable = false)
	private Timestamp createTs;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public BigDecimal getLatitudeRange() {
		return latitudeRange;
	}

	public BigDecimal getLongitudeRange() {
		return longitudeRange;
	}

	public void setLatitudeRange(BigDecimal latRange) {
		this.latitudeRange = latRange.setScale(LAT_LON_SCALE, BigDecimal.ROUND_HALF_DOWN);
	}

	public void setLongitudeRange(BigDecimal lonRange) {
		this.longitudeRange = lonRange.setScale(LAT_LON_SCALE, BigDecimal.ROUND_HALF_DOWN);
	}

	public Timestamp getUpdateTs() {
		return updateTs;
	}

	public Timestamp getCreateTs() {
		return createTs;
	}

}
