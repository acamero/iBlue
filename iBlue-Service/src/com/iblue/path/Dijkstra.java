package com.iblue.path;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;

import com.iblue.model.IntersectionInterface;

public class Dijkstra implements AlgorithmInterface {

	private GraphInterface graph;
	private Map<Long, Float> distances;
	private Map<Long, Long> predecessors;
	private HashSet<Long> unvisited;

	public Dijkstra() {
		distances = new HashMap<Long, Float>();
		predecessors = new HashMap<Long, Long>();
		unvisited = new HashSet<Long>();
	}

	@Override
	public void setGraph(GraphInterface graph) {
		this.graph = graph;
		System.out.println("Intersections: " + graph.getIntersections().size());
		System.out.println("Weights: " + graph.getWeightsMatrix().size());
		System.out.println("Edges: " + graph.getAdjacencyMatrix().size());
	}

	@Override
	public LinkedList<IntersectionInterface> getPath(IntersectionInterface from, IntersectionInterface to) {
		System.out.println("From:" + from.getId() + " To:" + to.getId());

		setShortestDistance(from.getId(), 0f);
		unvisited.add(from.getId());

		while (!unvisited.isEmpty()) {
			Long node = minDistance(unvisited);
			unvisited.remove(node);
			visitNeighbors(node);
		}

		return computePath(to.getId());
	}

	private void visitNeighbors(Long source) {
		// System.out.println("Visiting source " + source);
		Map<Long, Long> neighbors = getNeighbors(source);
		for (Entry<Long, Long> neighbor : neighbors.entrySet()) {
			// System.out.println("Neighbor: " + neighbor.getKey());
			if (getShortestDistance(neighbor.getKey()) > getShortestDistance(source)
					+ getEdgeDistance(source, neighbor)) {
				setShortestDistance(neighbor.getKey(), getShortestDistance(source) + getEdgeDistance(source, neighbor));
				predecessors.put(neighbor.getKey(), source);
				unvisited.add(neighbor.getKey());
			}
		}
	}

	private float getEdgeDistance(Long source, Entry<Long, Long> target) {
		return graph.getWeightsMatrix().get(target.getValue(), 8l);
	}

	private float getShortestDistance(long destId) {
		if (!distances.containsKey(destId)) {
			distances.put(destId, Float.MAX_VALUE);
		}
		return distances.get(destId);
	}

	private void setShortestDistance(long destId, float distance) {
		distances.put(destId, distance);
	}

	private Map<Long, Long> getNeighbors(Long sourceId) {
		// System.out.println("Id" + sourceId + " cols:" +
		// graph.getAdjacencyMatrix().containsColumn(sourceId) + " rows:"
		// + graph.getAdjacencyMatrix().containsColumn(sourceId));
		return graph.getAdjacencyMatrix().row(sourceId);
	}

	private Long minDistance(HashSet<Long> nodes) {
		Long minimum = null;
		for (Long vertex : nodes) {
			if (minimum == null) {
				minimum = vertex;
			} else if (getShortestDistance(vertex) < getShortestDistance(minimum)) {
				minimum = vertex;
			}
		}
		return minimum;
	}

	private LinkedList<IntersectionInterface> computePath(Long target) {
		LinkedList<IntersectionInterface> path = new LinkedList<IntersectionInterface>();
		Long step = target;
		// check if a path exists
		if (predecessors.get(step) == null) {
			System.out.println("No path found");
			return path;
		}
		path.add(graph.getIntersections().get(step));
		while (predecessors.get(step) != null) {
			step = predecessors.get(step);
			path.add(graph.getIntersections().get(step));
		}
		// Put it into the correct order
		Collections.reverse(path);
		return path;
	}

}
