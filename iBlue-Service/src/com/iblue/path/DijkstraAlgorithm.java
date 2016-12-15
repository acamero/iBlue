package com.iblue.path;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class DijkstraAlgorithm implements AlgorithmInterface {

	private List<? extends EdgeInterface> edges;
	private Set<Long> settledNodes;
	private Set<Long> unSettledNodes;
	private Map<Long, Long> predecessors;
	private Map<Long, Float> distance;
	private Map<Long, List<EdgeInterface>> neighbors;

	public DijkstraAlgorithm() {
		
	}
	
	public void setEdges(List<? extends EdgeInterface> edges) {
		this.edges = new ArrayList<EdgeInterface>(edges);
		neighbors = new HashMap<Long, List<EdgeInterface>>();
		for (EdgeInterface edge : this.edges) {
			List<EdgeInterface> tmp = neighbors.get(edge.getVertexFromId());
			if (tmp == null) {
				tmp = new ArrayList<EdgeInterface>();
			}
			tmp.add(edge);
			neighbors.put(edge.getVertexFromId(), tmp);
		}
	}
	
	public List<? extends EdgeInterface> getPath(VertexInterface origin, VertexInterface destination) {
		executeSource(origin);
		return computePath(destination);
	}

	private void executeSource(VertexInterface source) {
		System.out.println("Get route from: " + source.getId());

		settledNodes = new HashSet<Long>();
		unSettledNodes = new HashSet<Long>();
		distance = new HashMap<Long, Float>();
		predecessors = new HashMap<Long, Long>();
		distance.put(source.getId(), 0f);
		unSettledNodes.add(source.getId());
		while (unSettledNodes.size() > 0) {
			Long node = getMinimum(unSettledNodes);
			settledNodes.add(node);
			unSettledNodes.remove(node);
			findMinimalDistances(node);
		}
	}

	private void findMinimalDistances(Long node) {
		List<EdgeInterface> adjacentNodes = getNeighbors(node);
		for (EdgeInterface target : adjacentNodes) {
			if (getShortestDistance(target.getVertexToId()) > getShortestDistance(node) + getDistance(node, target)) {
				distance.put(target.getVertexToId(), getShortestDistance(node) + getDistance(node, target));
				predecessors.put(target.getVertexToId(), node);
				unSettledNodes.add(target.getVertexToId());
			}
		}

	}

	private float getDistance(Long node, EdgeInterface target) {
		return target.getWeight();
		// for (EdgeInterface edge : edges) {
		// if (edge.getVertexFromId() == node && edge.getVertexToId() == target)
		// {
		// return edge.getWeight();
		// }
		// }
		// throw new RuntimeException("Should not happen");
	}

	private List<EdgeInterface> getNeighbors(Long node) {
		List<EdgeInterface> tmp = new ArrayList<EdgeInterface>();
		if (neighbors.get(node) != null) {
			for (EdgeInterface edge : neighbors.get(node)) {
				if (!isSettled(edge.getVertexToId())) {
					tmp.add(edge);
				}
			}
		}
		return tmp;
	}

	private Long getMinimum(Set<Long> vertexes) {
		Long minimum = null;
		for (Long vertex : vertexes) {
			if (minimum == null) {
				minimum = vertex;
			} else {
				if (getShortestDistance(vertex) < getShortestDistance(minimum)) {
					minimum = vertex;
				}
			}
		}
		return minimum;
	}

	private boolean isSettled(long vertexId) {
		return settledNodes.contains(vertexId);
	}

	private float getShortestDistance(Long destination) {
		Float d = distance.get(destination);
		if (d == null) {
			return Float.MAX_VALUE;
		} else {
			return d;
		}
	}

	private LinkedList<? extends EdgeInterface> computePath(VertexInterface target) {
		System.out.println("To: " + target.getId());
		LinkedList<Long> pathIds = new LinkedList<Long>();
		Long step = target.getId();
		// check if a path exists
		if (predecessors.get(step) == null) {
			return null;
		}
		pathIds.add(step);
		while (predecessors.get(step) != null) {
			step = predecessors.get(step);
			pathIds.add(step);
		}
		// Put it into the correct order
		Collections.reverse(pathIds);

		LinkedList<EdgeInterface> path = new LinkedList<EdgeInterface>();
		for (int i = 0; i < pathIds.size() - 1; i++) {
			for (EdgeInterface edge : edges) {
				if (edge.getVertexFromId() == pathIds.get(i) && edge.getVertexToId() == pathIds.get(i + 1)) {
					path.add(edge);
					// edges.remove(edge);
				}
			}
		}

		return path;
	}

}
