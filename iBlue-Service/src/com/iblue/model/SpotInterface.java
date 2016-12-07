package com.iblue.model;

import java.math.BigDecimal;

public interface SpotInterface {

	public BigDecimal getLatitude(); // latitude in degrees
	public BigDecimal getLongitude(); // longitude in degrees
	public String getMac();
	public int getId();
	public int getStatus();
		
}
