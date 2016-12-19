package com.iblue.model.db;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.iblue.model.db.service.DbSchema;

@Entity
@Table(name = "named_street_types", schema = DbSchema.DB_SCHEMA)
public class NamedStreetType {
	@Id
	@Column(name = "pk_id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	
	@Column(name = "ts_update", insertable = false)
	private Timestamp updateTs;
	@Column(name = "ts_create", updatable = false)
	private Timestamp createTs;
	
	@Column(name="str_description")
	private String description;
	@Column(name="str_named_street_type")
	private String name;
	
	//@OneToMany(mappedBy = "streettype", cascade = CascadeType.ALL)
	//private Set<GeoStreet> geoStreets;
	
	public String getDescription() {
		return description;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}
	
	public String getName(){
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
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
	
	//public Set<GeoStreet> getGeoStreets() {
	//	return geoStreets;
	//}
}
