package com.iblue.model.db;

import java.sql.Timestamp;


import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "named_streets", schema = DbSchema.DB_SCHEMA)
public class NamedStreet {
	@Id
	@Column(name = "pk_id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;

	@Column(name = "ts_update", insertable = false)
	private Timestamp updateTs;
	@Column(name = "ts_create", updatable = false)
	private Timestamp createTs;
	
	@Column(name="str_name")
	private String name;
	@Column(name="str_reference")
	private String reference;
	
	//@OneToMany(mappedBy = "namedstreet", cascade = CascadeType.ALL)
	//private Set<GeoStreet> geoStreets;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getReference() {
		return reference;
	}

	public void setReference(String reference) {
		this.reference = reference;
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
