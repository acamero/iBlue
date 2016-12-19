package com.iblue.model.db;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.iblue.model.db.service.DbSchema;

@Entity
@Table(name = "geo_street_weights", schema = DbSchema.DB_SCHEMA)
public class GeoStreetWeight {

	@Id
	@Column(name = "pk_id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	
	@ManyToOne
	@JoinColumn(name = "fk_weight_type_id")
	private WeightType streetType;
	
	@ManyToOne
	@JoinColumn(name = "fk_geo_street_id")
	private GeoStreet geoStreet;
	
	@Column(name = "float_weight")
	private float weight;
	
	@Column(name = "ts_update", insertable = false)
	private Timestamp updateTs;
	@Column(name = "ts_create", updatable = false)
	private Timestamp createTs;

	public WeightType getStreetType() {
		return streetType;
	}

	public void setStreetType(WeightType streetType) {
		this.streetType = streetType;
	}

	public GeoStreet getGeoStreet() {
		return geoStreet;
	}

	public void setGeoStreet(GeoStreet geoStreet) {
		this.geoStreet = geoStreet;
	}

	public float getWeight() {
		return weight;
	}

	public void setWeight(float weight) {
		this.weight = weight;
	}

	public long getId() {
		return id;
	}

	public Timestamp getUpdateTs() {
		return updateTs;
	}

	public Timestamp getCreateTs() {
		return createTs;
	}
}
