package com.iblue.model;

import java.io.Serializable;
import java.math.BigDecimal;

public interface IntersectionInterface extends Serializable {
	
	public BigDecimal getLatitude();
	public BigDecimal getLongitude();
	public long getId() ;
	
	public float getNorthing();
	public float getEasting();
	public int getLongitudeZone();
	public char getLatitudeZone();

}
