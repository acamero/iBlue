package com.iblue.model;

import java.io.Serializable;
import java.util.Map;

import com.google.common.collect.Table;
import com.iblue.path.GraphInterface;

public class Tile implements Serializable, GraphInterface {

	private static final long serialVersionUID = 8977744359886535528L;

	// intersection-from, intersection-to, geoStreet
	private Table<Long, Long, Long> adjacencyMatrix;
	// id, intersection
	private Map<Long, IntersectionInterface> intersections;
	// edgeId, weightTypeId, weight
	private Table<Long, Long, Float> weightsMatrix;

	public Table<Long, Long, Long> getAdjacencyMatrix() {
		return adjacencyMatrix;
	}

	public void setAdjacencyMatrix(Table<Long, Long, Long> adjacencyMatrix) {
		this.adjacencyMatrix = adjacencyMatrix;
	}

	public Map<Long, IntersectionInterface> getIntersections() {
		return intersections;
	}

	public void setIntersections(Map<Long, IntersectionInterface> intersections) {
		this.intersections = intersections;
	}

	public Table<Long, Long, Float> getWeightsMatrix() {
		return weightsMatrix;
	}

	public void setWeightsMatrix(Table<Long, Long, Float> weightsMatrix) {
		this.weightsMatrix = weightsMatrix;
	}

	public void appendAdjacencyMatrix(Table<Long, Long, Long> appendix) {
		if (adjacencyMatrix != null) {
			adjacencyMatrix.putAll(appendix);
		}
	}

	public void appendIntersections(Map<Long, IntersectionInterface> appendix) {
		if (intersections != null) {
			intersections.putAll(appendix);
		}
	}

	public void appendWeights(Table<Long, Long, Float> appendix) {
		if (weightsMatrix != null) {
			weightsMatrix.putAll(appendix);
		}
	}
}
