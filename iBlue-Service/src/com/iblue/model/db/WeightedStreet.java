package com.iblue.model.db;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;


import org.hibernate.annotations.Immutable;
import org.hibernate.annotations.Type;

import com.iblue.model.StreetInterface;
import com.iblue.path.EdgeInterface;

@Entity
@Immutable
@Table(name = "vw_geo_streets_weighted", schema = DbSchema.DB_SCHEMA)
public class WeightedStreet implements EdgeInterface, StreetInterface {
	protected static final int LAT_LON_SCALE = 7;

	@Id
	@Column(name = "street_id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	@Column(name = "fk_street_type_id")
	private long streetTypeId;
	@Column(name = "fk_intersection_from_id")
	private long intersectionFromId;
	@Column(name = "fk_intersection_to_id")
	private long intersectionToId;
	@Column(name = "bl_oneway_ind", columnDefinition = "TINYINT")
	@Type(type = "org.hibernate.type.NumericBooleanType")
	private boolean oneway;
	@Column(name = "int_parking_capacity", columnDefinition = "SMALLINT")
	private int parkingCapacity;
	@Column(name = "int_lanes", columnDefinition = "TINYINT")
	private int numberOfLanes;
	@Column(name = "int_lanes_forward", columnDefinition = "TINYINT")
	private int lanesForward;
	@Column(name = "int_lanes_backward", columnDefinition = "TINYINT")
	private int lanesBackward;
	@Column(name = "weight_id")
	private long weightId;
	@Column(name = "fk_weight_type_id")
	private long weightTypeId;
	@Column(name = "float_weight")
	private float weight;
	@Column(name = "from_lat", precision = 10, scale = LAT_LON_SCALE)
	private BigDecimal latitudeFrom;
	@Column(name = "from_lon", precision = 10, scale = LAT_LON_SCALE)
	private BigDecimal longitudeFrom;
	@Column(name = "to_lat", precision = 10, scale = LAT_LON_SCALE)
	private BigDecimal latitudeTo;
	@Column(name = "to_lon", precision = 10, scale = LAT_LON_SCALE)
	private BigDecimal longitudeTo;

	public long getId() {
		return id;
	}

	public long getIntersectionFromId() {
		return intersectionFromId;
	}

	public long getIntersectionToId() {
		return intersectionToId;
	}

	public boolean isOneway() {
		return oneway;
	}

	public int getNumberOfLanes() {
		return numberOfLanes;
	}

	public int getLanesForward() {
		return lanesForward;
	}

	public int getLanesBackward() {
		return lanesBackward;
	}

	public long getWeightId() {
		return weightId;
	}

	public long getWeightTypeId() {
		return weightTypeId;
	}

	@Override
	public float getWeight() {
		return weight;
	}

	public BigDecimal getLatitudeFrom() {
		return latitudeFrom;
	}

	public BigDecimal getLongitudeFrom() {
		return longitudeFrom;
	}

	public BigDecimal getLatitudeTo() {
		return latitudeTo;
	}

	public BigDecimal getLongitudeTo() {
		return longitudeTo;
	}

	@Override
	public long getVertexFromId() {		
		return intersectionFromId;
	}

	@Override
	public long getVertexToId() {
		return intersectionToId;
	}
	
	@Override
	public BigDecimal getLatitude1() {		
		return latitudeFrom;
	}

	@Override
	public BigDecimal getLongitude1() {
		return longitudeFrom;
	}

	@Override
	public BigDecimal getLatitude2() {		
		return latitudeTo;
	}

	@Override
	public BigDecimal getLongitude2() {
		return longitudeTo;
	}

	@Override
	public int getStatus() {		
		return 1;
	}

	@Override
	public boolean isRoutable() {
		return true;
	}

	@Override
	public int getParkingCapacity() {
		return parkingCapacity;
	}

	@Override
	public long getStreetTypeId() {		
		return streetTypeId;
	}

	public String toString() {
		return "{"+
				"\"id\":\"" + id + "\"," +
				"\"latitude1\":\"" + latitudeFrom + "\"," + 
				"\"longitude1\":\"" + longitudeFrom + "\"," + 
				"\"latitude2\":\"" + latitudeTo + "\"," + 
				"\"longitude2\":\"" + longitudeTo + "\"," +
				"\"status\":\"" + getStatus() + "\"," +
				"\"routable\":\"" + isRoutable() + "\"," +				
				"\"streetTypeId\":\"" + getStreetTypeId() + "\"," +				
				"\"oneway\":\"" + oneway + "\"," +
				"\"numerOfLanes\":\"" + numberOfLanes + "\"," +
				"\"lanesForward\":\"" + lanesForward + "\"," +
				"\"lanesBackward\":\"" + lanesBackward + "\"" +
				"}";
	}

	
}
