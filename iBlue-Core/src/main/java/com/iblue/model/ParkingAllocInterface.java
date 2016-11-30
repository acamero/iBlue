package com.iblue.model;

import java.util.List;


public interface ParkingAllocInterface {

	public int getNearestStreetId(SpotInterface spot);
	public List<? extends StreetAvailabilityInterface> getNearStreetAvailability(SpotInterface spot) ;
	public StreetAvailabilityInterface parkMeClosest(SpotInterface spot) ;
	
}
