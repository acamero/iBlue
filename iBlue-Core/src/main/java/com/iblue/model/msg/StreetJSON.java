package com.iblue.model.msg;

import com.iblue.model.StreetInterface;
import com.iblue.auth.AuthMsgInterface;

public class StreetJSON implements StreetInterface, AuthMsgInterface {

	private float latitude1;
	private float longitude1;
	private float latitude2;
	private float longitude2;
	private int status;
	private int capacity;
	private int type;
	private int id;
	private String token;
	private String userId;

	public StreetJSON() {
	}

	public StreetJSON(float latitude1, float longitude1, float latitude2, float longitude2, int status, 
			int capacity, int type) {
		this.latitude1 = latitude1;
		this.longitude1 = longitude1;
		this.latitude2 = latitude2;
		this.longitude2 = longitude2;
		this.status = status;
		this.capacity = capacity;
		this.type = type;
	}

	public float getLatitude1() {
		return latitude1;
	}

	public void setLatitude1(float latitude1) {
		this.latitude1 = latitude1;
	}

	public float getLongitude1() {
		return longitude1;
	}

	public void setLongitude1(float longitude1) {
		this.longitude1 = longitude1;
	}

	public float getLatitude2() {
		return latitude2;
	}

	public void setLatitude2(float latitude2) {
		this.latitude2 = latitude2;
	}

	public float getLongitude2() {
		return longitude2;
	}

	public void setLongitude2(float longitude2) {
		this.longitude2 = longitude2;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public int getCapacity() {
		return capacity;
	}

	public void setCapacity(int capacity) {
		this.capacity = capacity;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setLatLong1(float latitude1, float longitude1) {
		this.latitude1 = latitude1;
		this.longitude1 = longitude1;
	}

	public void setLatLong2(float latitude2, float longitude2) {
		this.latitude2 = latitude2;
		this.longitude2 = longitude2;
	}

	public String toString() {
		return "{" + "\"latitude1\":\"" + latitude1 + "\"," + "\"longitude1\":\"" + longitude1 + "\","
				+ "\"latitude2\":\"" + latitude2 + "\"," + "\"longitude2\":\"" + longitude2 + "\"," + "\"status\":\""
				+ status + "\"," + "\"capacity\":\"" + capacity + "\"," 
				+ "\"type\":\"" + type +"\"," +"\"id\":\"" + id + "\"" + "}";
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
	
	public int getType() {
		return type;
	}
	
	public void setType(int type) {
		this.type = type;
	}
}
