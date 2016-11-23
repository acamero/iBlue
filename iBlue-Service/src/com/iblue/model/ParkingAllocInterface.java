package com.iblue.model;

import java.util.List;

import com.iblue.model.db.Spot;

public interface ParkingAllocInterface {

	public int getNearestStreetId(Spot spot);
	public List<? extends StreetAvailabilityInterface> getNearStreetAvailability(SpotInterface spot) ;
}
