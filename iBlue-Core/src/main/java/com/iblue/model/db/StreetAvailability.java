package com.iblue.model.db;

import com.iblue.model.StreetAvailabilityInterface;

public class StreetAvailability implements StreetAvailabilityInterface {

	private int id;
	private float latitude1;
	private float longitude1;
	private float latitude2;
	private float longitude2;
	private int inUseSpots;
	private int capacity;
	private int type;

	public StreetAvailability(int id, float latitude1, float longitude1, float latitude2, float longitude2, int inUse,
			int capacity, int type) {
		this.id = id;
		this.latitude1 = latitude1;
		this.longitude1 = longitude1;
		this.latitude2 = latitude2;
		this.longitude2 = longitude2;
		this.inUseSpots = inUse;
		this.capacity = capacity;
		this.type = type;
	}

	public int getId() {
		return id;
	}

	public float getLatitude1() {
		return latitude1;
	}

	public float getLongitude1() {
		return longitude1;
	}

	public float getLatitude2() {
		return latitude2;
	}

	public float getLongitude2() {
		return longitude2;
	}

	public int getInUseSpots() {
		return inUseSpots;
	}

	public int getCapacity() {
		return capacity;
	}

	/*
	public void setLatLong1(float latitude1, float longitude1) {
		this.latitude1 = latitude1;
		this.longitude1 = longitude1;
	}

	public void setLatLong2(float latitude2, float longitude2) {
		this.latitude2 = latitude2;
		this.longitude2 = longitude2;
	}
	*/
	
	public int getType() {
		return type;
	}

	public String toString() {
		return "{" + "\"id\":\"" + id + "\"," + "\"latitude1\":\"" + latitude1 + "\"," + "\"longitude1\":\""
				+ longitude1 + "\"," + "\"latitude2\":\"" + latitude2 + "\"," + "\"longitude2\":\"" + longitude2 + "\","
				+ "\"inUseSpots\":\"" + inUseSpots + "\"," + "\"capacity\":\"" + capacity + "\"," 
				+ "\"type\":\"" + type + "\"" + "}";
	}

	public int getStatus() {
		return 1;
	}

}
