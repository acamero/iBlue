package com.iblue.model;

import java.math.BigDecimal;

public interface StreetInterface {

	public long getId();
	// location
	public BigDecimal getLatitude1();		
	public BigDecimal getLongitude1();	
	public BigDecimal getLatitude2();	
	public BigDecimal getLongitude2();	

	public int getStatus();
	public boolean isRoutable(); 	// may be used for routing?
	public int getParkingCapacity();	
	public long getStreetTypeId();
	public boolean isOneway();
	public int getNumberOfLanes();
	public int getLanesForward();
	public int getLanesBackward();
	
}
