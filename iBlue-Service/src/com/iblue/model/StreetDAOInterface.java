package com.iblue.model;

import java.util.List;

import com.iblue.utils.Pair;


public interface StreetDAOInterface {

	public SimpleStreetInterface persist(SimpleStreetInterface street);

	public SimpleStreetInterface update(SimpleStreetInterface street);

	public void delete(SimpleStreetInterface street);

	public SimpleStreetInterface getStreet(long id);

	public List<? extends SimpleStreetInterface> findAll();

	public List<? extends SimpleStreetInterface> findAllActive();
	
	public List<? extends GeoStreetInterface> getTileBounded(Pair<Long, Long> tileId);
}
