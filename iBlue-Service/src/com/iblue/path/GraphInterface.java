package com.iblue.path;

import java.util.Map;

import com.google.common.collect.Table;
import com.iblue.model.IntersectionInterface;

public interface GraphInterface {

	// intersectionId, intersection
	public Map<Long, IntersectionInterface> getIntersections();
	// edgeId, weightTypeId, weight
	public Table<Long, Long, Float> getWeightsMatrix();
	// fromIntersectionId, toIntersectionId, edgeId
	public Table<Long, Long, Long> getAdjacencyMatrix();	
	
}
