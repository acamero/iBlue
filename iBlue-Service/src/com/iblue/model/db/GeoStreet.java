package com.iblue.model.db;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.ColumnResult;
import javax.persistence.ConstructorResult;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.SqlResultSetMapping;
import javax.persistence.Table;

import org.hibernate.annotations.Type;

import com.iblue.model.GeoStreetInterface;
import com.iblue.model.db.service.DbSchema;

@SqlResultSetMapping(name = "AreaMapResultSet", classes = {
		@ConstructorResult(targetClass = StreetAvailability.class, columns = {
				@ColumnResult(name = "pk_id", type = Long.class),
				@ColumnResult(name = "decimal_latitude_1", type = BigDecimal.class),
				@ColumnResult(name = "decimal_longitude_1", type = BigDecimal.class),
				@ColumnResult(name = "decimal_latitude_2", type = BigDecimal.class),
				@ColumnResult(name = "decimal_longitude_2", type = BigDecimal.class),
				@ColumnResult(name = "usage_number", type = Integer.class),
				@ColumnResult(name = "int_parking_capacity", type = Integer.class),
				@ColumnResult(name = "fk_street_type_id", type = Long.class) }) })

@Entity
@Table(name = "geo_streets", schema = DbSchema.DB_SCHEMA)
public class GeoStreet implements GeoStreetInterface {

	@Id
	@Column(name = "pk_id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;

	@Column(name = "ts_update", insertable = false)
	private Timestamp updateTs;
	@Column(name = "ts_create", updatable = false)
	private Timestamp createTs;
	@Column(name = "int_status", columnDefinition = "TINYINT")
	private int status;

	@ManyToOne
	@JoinColumn(name = "fk_intersection_from_id")
	private Intersection fromIntersection;
	@ManyToOne
	@JoinColumn(name = "fk_intersection_to_id")
	private Intersection toIntersection;

	@ManyToOne
	@JoinColumn(name = "fk_named_street_id")
	private NamedStreet namedStreet;

	@ManyToOne
	@JoinColumn(name = "fk_street_type_id")
	private StreetType streetType;

	@Column(name = "bl_oneway_ind", columnDefinition = "TINYINT")
	@Type(type = "org.hibernate.type.NumericBooleanType")
	private boolean oneway;
	@Column(name = "int_lanes", columnDefinition = "TINYINT")
	private int numberOfLanes;
	@Column(name = "int_lanes_forward", columnDefinition = "TINYINT")
	private int lanesForward;
	@Column(name = "int_lanes_backward", columnDefinition = "TINYINT")
	private int lanesBackward;

	@Type(type = "org.hibernate.type.NumericBooleanType")
	@Column(name = "bl_routable_ind", columnDefinition = "TINYINT")
	private boolean routable;
	@Column(name = "int_parking_capacity", columnDefinition = "SMALLINT")
	private int parkingCapacity;

	// Line parameters
	@Column(name = "float_line_coeff_a")
	private float lineCoeffA;
	@Column(name = "float_line_coeff_b")
	private float lineCoeffB;
	@Column(name = "float_line_coeff_c")
	private float lineCoeffC;
	@Column(name = "float_line_sqrt_a2_b2")
	private float sqrtA2B2;

	@OneToMany(mappedBy = "geoStreet", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	private Set<GeoStreetWeight> weights;

	public Set<GeoStreetWeight> getWeights() {
		return weights;
	}

	public void setWeights(Set<GeoStreetWeight> weights) {
		this.weights = weights;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public boolean isOneway() {
		return oneway;
	}

	public void setOneway(boolean oneway) {
		this.oneway = oneway;
	}

	public int getNumberOfLanes() {
		return numberOfLanes;
	}

	public void setNumberOfLanes(int numberOfLanes) {
		this.numberOfLanes = numberOfLanes;
	}

	public int getLanesForward() {
		return lanesForward;
	}

	public void setLanesForward(int lanesForward) {
		this.lanesForward = lanesForward;
	}

	public int getLanesBackward() {
		return lanesBackward;
	}

	public void setLanesBackward(int lanesBackward) {
		this.lanesBackward = lanesBackward;
	}

	public boolean isRoutable() {
		return routable;
	}

	public void setRoutable(boolean routable) {
		this.routable = routable;
	}

	public int getParkingCapacity() {
		return parkingCapacity;
	}

	public void setParkingCapacity(int parkingCapacity) {
		this.parkingCapacity = parkingCapacity;
	}

	public void setFromIntersection(Intersection fromIntersection) {
		this.fromIntersection = fromIntersection;
	}

	public void setToIntersection(Intersection toIntersection) {
		this.toIntersection = toIntersection;
	}

	public void setNamedStreet(NamedStreet namedStreet) {
		this.namedStreet = namedStreet;
	}

	public void setStreetType(StreetType streetType) {
		this.streetType = streetType;
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

	public Intersection getFromIntersection() {
		return fromIntersection;

	}

	public Intersection getToIntersection() {
		return toIntersection;
	}

	public NamedStreet getNamedStreet() {
		return namedStreet;
	}

	public StreetType getStreetType() {
		return streetType;
	}

	public double calcDistance(double easting, double northing) {
		return Math.abs(lineCoeffA * easting + lineCoeffB * northing + lineCoeffC) / sqrtA2B2;
	}

	public double distance(Spot spot) {
		return calcDistance(spot.getEasting(), spot.getNorthing());
	}

	@Override
	public BigDecimal getLatitude1() {
		return this.fromIntersection.getLatitude();
	}

	@Override
	public BigDecimal getLongitude1() {
		return this.fromIntersection.getLongitude();
	}

	@Override
	public BigDecimal getLatitude2() {
		return this.toIntersection.getLatitude();
	}

	@Override
	public BigDecimal getLongitude2() {
		return this.toIntersection.getLongitude();
	}

	@Override
	public long getStreetTypeId() {
		return streetType.getId();
	}

	public String toString() {
		return "{" + "\"id\":\"" + id + "\"," + "\"latitude1\":\"" + getLatitude1() + "\"," + "\"longitude1\":\""
				+ getLongitude1() + "\"," + "\"latitude2\":\"" + getLatitude2() + "\"," + "\"longitude2\":\""
				+ getLongitude2() + "\"," + "\"status\":\"" + status + "\"," + "\"routable\":\"" + routable + "\","
				+ "\"parkingCapacity\":\"" + parkingCapacity + "\"," + "\"streetTypeId\":\"" + getStreetTypeId() + "\","
				+ "\"oneway\":\"" + oneway + "\"," + "\"numerOfLanes\":\"" + numberOfLanes + "\","
				+ "\"lanesForward\":\"" + lanesForward + "\"," + "\"lanesBackward\":\"" + lanesBackward + "\"" + "}";
	}

}
