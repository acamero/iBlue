package com.iblue.model.db;

import java.util.Map;

import com.google.common.collect.Table;

public class Tile {

	// intersection-from, intersection-to, geoStreet
	private Table<Long, Long, Long> adjacencyMatrix;
	// id, intersection
	private Map<Long,Intersection> intersections;
	// edgeId, weightTypeId, weight
	private Table<Long,Long,Float> weightsMatrix;
	
	public Tile() {		
	}

	public Table<Long,Long,Long> getAdjacencyMatrix() {
		return adjacencyMatrix;
	}

	public void setAdjacencyMatrix(Table<Long,Long,Long> adjacencyMatrix) {
		this.adjacencyMatrix = adjacencyMatrix;
	}

	public Map<Long,Intersection> getIntersections() {
		return intersections;
	}

	public void setIntersections(Map<Long,Intersection> intersections) {
		this.intersections = intersections;
	}

	public Table<Long,Long,Float> getWeightsMatrix() {
		return weightsMatrix;
	}

	public void setWeightsMatrix(Table<Long,Long,Float> weightsMatrix) {
		this.weightsMatrix = weightsMatrix;
	}
}
