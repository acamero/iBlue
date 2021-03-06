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
	
	@ManyToOne
	@JoinColumn(name = "fk_named_street_type_id")
	private NamedStreetType namedStreetType;
	
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
	
	public NamedStreetType getNamedStreetType() {
		return namedStreetType;
	}
	
	public void setNamedStreetType(NamedStreetType namedStreetType) {
		this.namedStreetType = namedStreetType;
	}
}
