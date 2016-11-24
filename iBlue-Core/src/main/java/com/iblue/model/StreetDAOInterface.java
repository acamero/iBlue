package com.iblue.model;

import java.util.List;

public interface StreetDAOInterface {

	public StreetInterface persist(StreetInterface street);

	public StreetInterface update(StreetInterface street);

	public void delete(StreetInterface street);

	public StreetInterface getStreet(int id);

	public List<? extends StreetInterface> findAll();

	public List<? extends StreetInterface> findAllActive();
}
