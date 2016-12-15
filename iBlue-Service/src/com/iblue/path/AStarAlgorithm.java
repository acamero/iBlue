package com.iblue.path;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class AStarAlgorithm implements AlgorithmInterface {

	//private Map<VertexInterface, Float> distanceFromOrigin = new HashMap<VertexInterface, Float>();
	private Map<Long, Float> distanceFromOrigin = new HashMap<Long, Float>();
	//private Map<VertexInterface, Float> distanceToGoal = new HashMap<VertexInterface, Float>();
	private Map<Long, Float> distanceToGoal = new HashMap<Long, Float>();
	//private List<VertexInterface> closedList = new ArrayList<VertexInterface>();
	private List<Long> closedList = new ArrayList<Long>();
	//private List<VertexInterface> openList = new ArrayList<VertexInterface>();
	private List<Long> openList = new ArrayList<Long>();
	//private Map<VertexInterface, VertexInterface> cameFrom = new HashMap<VertexInterface, VertexInterface>();
	private Map<Long, Long> cameFrom = new HashMap<Long, Long>();
	//private Map<VertexInterface, Map<VertexInterface, Float>> weights = new HashMap<VertexInterface, Map<VertexInterface, Float>>();
	private Map<Long, Map<Long, Float>> weights = new HashMap<Long, Map<Long, Float>>();
	//private Map<VertexInterface, Map<VertexInterface, Long>> edgeIds = new HashMap<VertexInterface, Map<VertexInterface, Long>>();
	private Map<Long, Map<Long, Long>> edgeIds = new HashMap<Long, Map<Long, Long>>();
	private Map<Long, EdgeInterface> mapEdges = new HashMap<Long, EdgeInterface>();
	private List<VertexInterface> vertex = new ArrayList<VertexInterface>();

	private class Vertex implements VertexInterface {
		private long id;

		private Vertex(long id) {
			this.id = id;
		}

		@Override
		public long getId() {
			return id;
		}

		@Override
		public BigDecimal getLatitude() {
			return null;
		}

		@Override
		public BigDecimal getLongitude() {
			return null;
		}

	}

	public AStarAlgorithm() {

	}

	public void setEdges(List<? extends EdgeInterface> edges) {

		for (EdgeInterface edge : edges) {
			Vertex v1 = new Vertex(edge.getVertexFromId());
			if (!vertex.contains(v1)) {
				vertex.add(v1);
			}

			Vertex v2 = new Vertex(edge.getVertexToId());
			if (!vertex.contains(v2)) {
				vertex.add(v2);
			}

			setWeight(v1, v2, edge.getWeight());
			setEdgeId(v1, v2, edge.getId());

			mapEdges.put(edge.getId(), edge);
		}
		
		System.out.println("Edges loaded");
	}

	private void setWeight(VertexInterface from, VertexInterface to, float weight) {
		Map<Long, Float> temp = weights.get(from.getId());
		if (temp == null) {
			temp = new HashMap<Long, Float>();
		}
		temp.put(to.getId(), weight);
		weights.put(from.getId(), temp);
	}

	private void setEdgeId(VertexInterface from, VertexInterface to, long id) {
		Map<Long, Long> temp = edgeIds.get(from.getId());
		if (temp == null) {
			temp = new HashMap<Long, Long>();
		}
		temp.put(to.getId(), id);
		edgeIds.put(from.getId(), temp);
	}

	public List<? extends EdgeInterface> getPath(VertexInterface origin, VertexInterface destination) {

		openList.add(origin.getId());
		setDistanceToOrigin(origin.getId(), 0f);
		distanceToGoal.put(origin.getId(), getHeuristicDistance(origin.getId(), destination.getId()));

		int counter = 0;
		while (!openList.isEmpty()) {
			Long current = getCurrent();
			if (current == destination.getId()) {
				System.out.println("Solution found!");
				return generatePath(origin.getId(), destination.getId());
			}

			openList.remove(current);
			closedList.add(current);
			for (Long neighbor : getAvailableNeighbors(current)) {
				float tempDist = getDistanceFromOrigin(current) + getWeight(current, neighbor);
				if (!openList.contains(neighbor)) {
					openList.add(neighbor);
				} else if (tempDist < getDistanceFromOrigin(neighbor)) {
					cameFrom.put(neighbor, current);
					setDistanceToOrigin(neighbor, tempDist);
					distanceToGoal.put(neighbor, tempDist + getHeuristicDistance(neighbor, destination.getId()));
				}
			}
			
			if(counter>10000) {
				break;
			}
		}

		// In theory we never reach that part of the code
		throw new RuntimeException("Should not happen");
	}

	private List<? extends EdgeInterface> generatePath(Long origin, Long destination) {
		Long current = destination;
		LinkedList<Long> pathVs = new LinkedList<Long>();
		pathVs.add(current);
		while (cameFrom.containsKey(current)) {
			current = cameFrom.get(current);
			pathVs.add(current);
		}

		Collections.reverse(pathVs);

		LinkedList<EdgeInterface> path = new LinkedList<EdgeInterface>();
		for (int i = 0; i < pathVs.size() - 1; i++) {
			EdgeInterface edge = mapEdges.get(edgeIds.get(pathVs.get(i)).get(pathVs.get(i + 1)));
			path.add(edge);
		}

		return path;
	}

	private void setDistanceToOrigin(Long to, float dist) {
		distanceFromOrigin.put(to, dist);
	}

	private float getDistanceFromOrigin(Long node) {
		if (distanceFromOrigin.get(node) == null) {
			distanceFromOrigin.put(node, Float.MAX_VALUE);
		}
		return distanceFromOrigin.get(node);
	}

	private float getWeight(Long current, Long neighbor) {
		return weights.get(current).get(neighbor);
	}

	private float getHeuristicDistance(Long from, Long to) {
		return 1f;
	}

	private Long getCurrent() {
		// return the vertex with minimum heuristic distance to the goal
		Long minVertex = null;
		float min = Float.MAX_VALUE;
		for (Entry<Long, Float> entry : distanceToGoal.entrySet()) {
			if (entry.getValue() < min) {
				min = entry.getValue();
				minVertex = entry.getKey();
			}
		}
		return minVertex;
	}

	private List<Long> getAvailableNeighbors(Long current) {
		// return a list with the neighbors that are not in the closed list
		Map<Long, Float> temp = weights.get(current);
		List<Long> neighbors = new ArrayList<Long>();
		if (temp != null) {
			for (Long v : temp.keySet()) {
				if (!closedList.contains(v)) {
					neighbors.add(v);
				}
			}
		}
		return neighbors;
	}

}
