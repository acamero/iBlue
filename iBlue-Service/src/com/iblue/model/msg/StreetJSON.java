package com.iblue.model.msg;

import com.iblue.model.StreetInterface;

import java.math.BigDecimal;

import com.iblue.auth.AuthMsgInterface;

public class StreetJSON implements StreetInterface, AuthMsgInterface {

	private BigDecimal latitude1;
	private BigDecimal longitude1;
	private BigDecimal latitude2;
	private BigDecimal longitude2;
	private int status;
	private boolean routable;	
	private int parkingCapacity;	
	private long streetTypeId;
	private long id;
	private boolean oneway;
	private int numberOfLanes;
	private int lanesForward;
	private int lanesBackward;
	private String token;
	private String userId;
	
	public StreetJSON() {
	}

	public StreetJSON(BigDecimal latitude1, BigDecimal longitude1, BigDecimal latitude2, BigDecimal longitude2, int status, 
			int capacity, int type) {
		this.latitude1 = latitude1;
		this.longitude1 = longitude1;
		this.latitude2 = latitude2;
		this.longitude2 = longitude2;
		this.status = status;
		this.parkingCapacity = capacity;
		this.streetTypeId = type;
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

	public void setParkingCapacity(int parkingCapacity) {
		this.parkingCapacity = parkingCapacity;
	}

	public BigDecimal getLatitude1() {
		return latitude1;
	}

	public void setLatitude1(BigDecimal latitude1) {
		this.latitude1 = latitude1;
	}

	public BigDecimal getLongitude1() {
		return longitude1;
	}

	public void setLongitude1(BigDecimal longitude1) {
		this.longitude1 = longitude1;
	}

	public BigDecimal getLatitude2() {
		return latitude2;
	}

	public void setLatitude2(BigDecimal latitude2) {
		this.latitude2 = latitude2;
	}

	public BigDecimal getLongitude2() {
		return longitude2;
	}

	public void setLongitude2(BigDecimal longitude2) {
		this.longitude2 = longitude2;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public int getParkingCapacity() {
		return parkingCapacity;
	}

	public void setCapacity(int capacity) {
		this.parkingCapacity = capacity;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public void setLatLong1(BigDecimal latitude1, BigDecimal longitude1) {
		this.latitude1 = latitude1;
		this.longitude1 = longitude1;
	}

	public void setLatLong2(BigDecimal latitude2, BigDecimal longitude2) {
		this.latitude2 = latitude2;
		this.longitude2 = longitude2;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}
	
	public long getStreetTypeId() {
		return streetTypeId;
	}
	
	public void setStreetTypeId(long type) {
		this.streetTypeId = type;
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
				"\"oneway\":\"" + oneway + "\"," +
				"\"numerOfLanes\":\"" + numberOfLanes + "\"," +
				"\"lanesForward\":\"" + lanesForward + "\"," +
				"\"lanesBackward\":\"" + lanesBackward + "\"," +
				"\"token\":\"" + token + "\"," +
				"\"userId\":\"" + userId + "\"" +
				"}";
	}
}
