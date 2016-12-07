package com.iblue.model.msg;

import com.iblue.model.SpotInterface;

import java.math.BigDecimal;

import com.iblue.auth.AuthMsgInterface;

public class SpotJSON implements SpotInterface, AuthMsgInterface {

	private BigDecimal latitude;
	private BigDecimal longitude;
	private String mac;
	private int id;
	private int status;
	private String token;

	public SpotJSON() {

	}

	public SpotJSON(BigDecimal latitude, BigDecimal longitude, String mac, int status) {
		this.latitude = latitude;
		this.longitude = longitude;
		this.mac = mac;
		this.status = status;
	}

	@Override
	public BigDecimal getLatitude() {
		return latitude;
	}

	public void setLatitude(BigDecimal latitude) {
		this.latitude = latitude;
	}

	@Override
	public BigDecimal getLongitude() {
		return longitude;
	}

	public void setLongitude(BigDecimal longitude) {
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

	public void setLatLong(BigDecimal latitude, BigDecimal longitude) {
		this.latitude = latitude;
		this.longitude = longitude;
	}

	public String toString() {
		return "{\"latitude\":\"" + getLatitude() + "\",\"longitude\":\"" + getLongitude() + "\",\"mac\":\"" + this.mac
				+ "\",\"status\":\"" + this.status + "\",\"id\":\"" + this.id + "\"}";

	}

	@Override
	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public String getUserId() {
		return mac;
	}
}
