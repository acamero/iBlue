package com.iblue.model.db;

import java.math.BigDecimal;

import com.iblue.model.StreetAvailabilityInterface;

public class StreetAvailability implements StreetAvailabilityInterface {

	private BigDecimal latitude1;
	private BigDecimal longitude1;
	private BigDecimal latitude2;
	private BigDecimal longitude2;
	private int status = 1; // by default
	private boolean routable;
	private int parkingCapacity;
	private long streetTypeId;
	private long id;
	private boolean oneway;
	private int numberOfLanes;
	private int lanesForward;
	private int lanesBackward;
	private int usageNumber;

	public StreetAvailability(long id, BigDecimal latitude1, BigDecimal longitude1, BigDecimal latitude2,
			BigDecimal longitude2, int inUse, int capacity, long type) {
		this.id = id;
		this.latitude1 = latitude1;
		this.longitude1 = longitude1;
		this.latitude2 = latitude2;
		this.longitude2 = longitude2;
		this.usageNumber = inUse;
		this.parkingCapacity = capacity;
		this.streetTypeId = type;
	}

	public long getId() {
		return id;
	}

	public BigDecimal getLatitude1() {
		return latitude1;
	}

	public BigDecimal getLongitude1() {
		return longitude1;
	}

	public BigDecimal getLatitude2() {
		return latitude2;
	}

	public BigDecimal getLongitude2() {
		return longitude2;
	}

	public int getUsageNumber() {
		return usageNumber;
	}

	public int getParkingCapacity() {
		return parkingCapacity;
	}

	public long getStreetTypeId() {
		return streetTypeId;
	}

	public boolean isRoutable() {
		return routable;
	}

	public void setRoutable(boolean routable) {
		this.routable = routable;
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

	public void setLatitude1(BigDecimal latitude1) {
		this.latitude1 = latitude1;
	}

	public void setLongitude1(BigDecimal longitude1) {
		this.longitude1 = longitude1;
	}

	public void setLatitude2(BigDecimal latitude2) {
		this.latitude2 = latitude2;
	}

	public void setLongitude2(BigDecimal longitude2) {
		this.longitude2 = longitude2;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public void setParkingCapacity(int parkingCapacity) {
		this.parkingCapacity = parkingCapacity;
	}

	public void setStreetTypeId(long streetTypeId) {
		this.streetTypeId = streetTypeId;
	}

	public void setId(long id) {
		this.id = id;
	}

	public void setUsageNumber(int usageNumber) {
		this.usageNumber = usageNumber;
	}

	public String toString() {
		return "{" + 
				"\"id\":\"" + id + "\"," +
				"\"latitude1\":\"" + latitude1 + "\"," + 
				"\"longitude1\":\"" + longitude1 + "\"," + 
				"\"latitude2\":\"" + latitude2 + "\"," + 
				"\"longitude2\":\"" + longitude2 + "\"," +
				"\"status\":\"" + status + "\"," +
				"\"routable\":\"" + routable + "\"," +
				"\"parkingCapacity\":\"" + parkingCapacity + "\"," +
				"\"streetTypeId\":\"" + streetTypeId + "\"," +
				"\"usageNumber\":\"" + usageNumber + "\"," + 
				"\"oneway\":\"" + oneway + "\"," +
				"\"numerOfLanes\":\"" + numberOfLanes + "\"," +
				"\"lanesForward\":\"" + lanesForward + "\"," +
				"\"lanesBackward\":\"" + lanesBackward + "\"" +
				"}";
	}

	public int getStatus() {
		return status;
	}

}
