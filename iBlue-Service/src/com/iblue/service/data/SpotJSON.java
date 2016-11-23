package com.iblue.service.data;

import com.iblue.model.SpotInterface;

public class SpotJSON implements SpotInterface {

	private float latitude;
	private float longitude;
	private String mac;
	private int id;
	private int status;
	
	public SpotJSON() {
		
	}
	
	public SpotJSON(float latitude, float longitude, String mac, int status) {
		this.latitude = latitude;
		this.longitude = longitude;
		this.mac = mac;
		this.status = status;
	}

	@Override
	public float getLatitude() {
		return latitude;
	}

	public void setLatitude(float latitude) {
		this.latitude = latitude;
	}
	
	@Override
	public float getLongitude() {
		return longitude;
	}
	
	public void setLongitude(float longitude) {
		this.longitude = longitude;
	}

	@Override
	public String getMac() {
		return mac;
	}
	
	public void setMac(String mac) {
		this.mac = mac;
	}

	@Override
	public int getId() {
		return id;
	}
	
	public void setId(int id) {
		this.id = id;
	}

	@Override
	public int getStatus() {
		return status;
	}
	
	public void setStatus(int status) {
		this.status = status;
	}
	
	public void setLatLong(float latitude, float longitude) {
		this.latitude = latitude;
		this.longitude = longitude;
	}

	public String toString() {
		return "{\"latitude\":\"" + this.latitude + "\",\"longitude\":\"" + this.longitude + "\",\"mac\":\"" + this.mac
				+ "\",\"status\":\"" + this.status + "\",\"id\":\"" + this.id + "\"}";

	}
}
