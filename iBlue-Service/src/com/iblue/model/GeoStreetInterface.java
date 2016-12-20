package com.iblue.model;

import java.util.Set;


public interface GeoStreetInterface extends SimpleStreetInterface {

	public IntersectionInterface getFromIntersection();
	public IntersectionInterface getToIntersection();
	public Set<? extends GeoStreetWeightInterface> getWeights();
}
