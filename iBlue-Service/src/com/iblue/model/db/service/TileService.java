package com.iblue.model.db.service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import com.iblue.model.IntersectionInterface;
import com.iblue.model.StreetDAOInterface;
import com.iblue.model.Tile;
import com.iblue.model.TileCacheInterface;
import com.iblue.model.TileContainerInterface;
import com.iblue.model.TileServiceInterface;
import com.iblue.model.cache.GuavaCache;
import com.iblue.model.GeoStreetInterface;
import com.iblue.model.GeoStreetWeightInterface;
import com.iblue.model.db.TileContainer;
import com.iblue.model.db.dao.GeoStreetDAO;
import com.iblue.model.db.dao.TileDAO;
import com.iblue.utils.Pair;

public class TileService implements TileServiceInterface {

	private StreetDAOInterface streetDAO;
	private TileDAO tileDAO;

	public TileService() {
		streetDAO = new GeoStreetDAO();
		tileDAO = new TileDAO();
	}

	public Tile buildTile(Pair<Long, Long> id) {
		List<? extends GeoStreetInterface> streets = streetDAO.getTileBounded(id);
		Tile tile = computeTile(streets);
		return tile;
	}

	private Tile computeTile(List<? extends GeoStreetInterface> streets) {
		Table<Long, Long, Long> adjacencyMatrix = HashBasedTable.create();
		Map<Long, IntersectionInterface> intersections = new HashMap<Long, IntersectionInterface>();
		Table<Long, Long, Float> weightsMatrix = HashBasedTable.create();

		for (GeoStreetInterface street : streets) {
			intersections.put(street.getFromIntersection().getId(), street.getFromIntersection());
			intersections.put(street.getToIntersection().getId(), street.getToIntersection());
			adjacencyMatrix.put(street.getFromIntersection().getId(), street.getToIntersection().getId(),
					street.getId());
			if (!street.isOneway()) {
				adjacencyMatrix.put(street.getToIntersection().getId(), street.getFromIntersection().getId(),
						street.getId());
			}
			for (GeoStreetWeightInterface w : street.getWeights()) {
				weightsMatrix.put(street.getId(), w.getWeightTypeId(), w.getWeight());
			}
		}

		Tile tile = new Tile();
		tile.setAdjacencyMatrix(adjacencyMatrix);
		tile.setIntersections(intersections);
		tile.setWeightsMatrix(weightsMatrix);
		return tile;
	}

	public String computeMap() {
		GeoStreetDAO dao = new GeoStreetDAO();
		Pair<BigDecimal, BigDecimal> latBounds = dao.getLatitudeBoundaries();
		System.out.println("Lat min=" + latBounds.getFirst() + " max=" + latBounds.getSecond());

		Pair<BigDecimal, BigDecimal> lonBounds = dao.getLongitudeBoundaries();
		System.out.println("Lon min=" + lonBounds.getFirst() + " max=" + lonBounds.getSecond());

		int added = 0;
		int updated = 0;
		List<Pair<Long, Long>> tileIds = TileHelper.getBoundariesTileId(latBounds, lonBounds);
		for (Pair<Long, Long> id : tileIds) {
			System.out.println("Build tile latId=" + id.getFirst() + " lonId=" + id.getSecond());
			Tile tile = buildTile(id);
			TileContainer tileCont = tileDAO.getTile(id);
			if (tileCont == null) {
				tileCont = new TileContainer();
				tileCont.setTileId(id);
				tileCont.setTile(tile);
				tileDAO.persist(tileCont);
				System.out.println("New tile added");
				added++;
			} else {
				tileCont.setTile(tile);
				tileDAO.update(tileCont);
				System.out.println("Tile updated");
				updated++;
			}
		}
		
		return added + " tiles added and " + updated + " tiles updated";
	}

	public Tile getTile(BigDecimal latFrom, BigDecimal lonFrom, BigDecimal latTo, BigDecimal lonTo) {
		
		List<Pair<Long, Long>> tileIds = TileHelper.getListTileId(latFrom, lonFrom, latTo, lonTo);
		TileCacheInterface tileCache = GuavaCache.getInstance();
		@SuppressWarnings("unchecked")
		List<TileContainerInterface> tileConts = (List<TileContainerInterface>)tileCache.getTiles(tileIds);
		
		// System.out.println("Tiles number " + tileConts.size());
		Tile tile = new Tile();
		tile.setAdjacencyMatrix(HashBasedTable.create());
		tile.setIntersections(new HashMap<Long, IntersectionInterface>());
		tile.setWeightsMatrix(HashBasedTable.create());

		for (TileContainerInterface tc : tileConts) {
			System.out.println("Tile id: " + tc.getTileId().getFirst() + " " + tc.getTileId().getSecond());
			System.out.println("Intersections: " + tc.getTile().getIntersections().size());
			System.out.println("Weights: " + tc.getTile().getWeightsMatrix().size());
			System.out.println("Edges: " + tc.getTile().getAdjacencyMatrix().size());
			tile.appendAdjacencyMatrix(tc.getTile().getAdjacencyMatrix());
			tile.appendIntersections(tc.getTile().getIntersections());
			tile.appendWeights(tc.getTile().getWeightsMatrix());
		}

		return tile;
	}

}
