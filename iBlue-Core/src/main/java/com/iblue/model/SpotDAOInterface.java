package com.iblue.model;

import java.util.List;


public interface SpotDAOInterface {

	public SpotInterface persist(SpotInterface spot);
	
	public SpotInterface update(SpotInterface spot);
	
	public void delete(SpotInterface spot);
	
	public SpotInterface getSpot(int id);
	
	public SpotInterface getSpot(SpotInterface spot);
	
	public List<? extends SpotInterface> findAll();
	
	public List<? extends SpotInterface> findAllActive();
	
	public List<? extends SpotInterface> findAllActive(long from);
	
	public List<? extends SpotInterface> findAllRelease(long from);
	
}
