package com.iblue.model.db.service;


import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

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
import com.iblue.model.partitioning.TileHelper;
import com.iblue.utils.Log;
import com.iblue.utils.Pair;

public class TileService implements TileServiceInterface {

	private StreetDAOInterface streetDAO;
	private TileDAO tileDAO;
	private TileCacheInterface tileCache = loadTileCache();
	private static final BigDecimal DELTA = new BigDecimal(1.0d);

	public TileService() {
		streetDAO = new GeoStreetDAO();
		tileDAO = new TileDAO();
	}

	/**
	 * Load the class that implements the cache interface
	 * 
	 * @return
	 */
	private static TileCacheInterface loadTileCache() {
		ClassLoader classLoader = TileService.class.getClassLoader();
		InputStream propFile = null;
		TileCacheInterface cacheInterface = null;
		try {
			//File file = new File(classLoader.getResource("/service.properties").getFile());
			//propFile = new FileInputStream(file);
			propFile = classLoader.getResourceAsStream("service.properties");
			Properties prop = new Properties();
			prop.load(propFile);
			String cacheClass = prop.getProperty("service.cache.class");
			Log.debug("Properties cache class=" + cacheClass);
			Class<?> _class = Class.forName(cacheClass);
			// retrieve the static method getInstance from the selected class
			Method method = _class.getMethod("getInstance");
			cacheInterface = (TileCacheInterface) method.invoke(null, new Object[] {});
		} catch (IOException | IllegalAccessException | ClassNotFoundException | NoSuchMethodException
				| SecurityException | IllegalArgumentException | InvocationTargetException e) {
			e.printStackTrace();
			Log.debug("Unable to load cache properties");
			cacheInterface = GuavaCache.getInstance();
		} finally {
			if (propFile != null) {
				try {
					propFile.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return cacheInterface;
	}

	/**
	 * Compute the tile for the given tile id (uses the tile partitioning for
	 * selecting the region to be included)
	 * 
	 * @param id
	 * @return
	 */
	public Tile buildTile(Pair<Long, Long> id) {
		List<? extends GeoStreetInterface> streets = ((GeoStreetDAO) streetDAO).getTileBounded(id);
		Tile tile = computeTile(streets);
		return tile;
	}

	/**
	 * Compute the tile for the given list of streets
	 * 
	 * @param streets
	 * @return
	 */
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

	/**
	 * Recompute the tile map with the actual partitioning values
	 */
	public String updateMap() {
		Pair<BigDecimal, BigDecimal> latBounds = ((GeoStreetDAO) streetDAO).getLatitudeBoundaries();
		Log.debug("Lat min=" + latBounds.getFirst() + " max=" + latBounds.getSecond());

		Pair<BigDecimal, BigDecimal> lonBounds = ((GeoStreetDAO) streetDAO).getLongitudeBoundaries();
		Log.debug("Lon min=" + lonBounds.getFirst() + " max=" + lonBounds.getSecond());

		List<Pair<Long, Long>> tileIds = TileHelper.getInstance().getBoundariesTileId(latBounds, lonBounds);
		return updateMap(tileIds);
	}
	
	
	/**
	 * <lat1,lat2>, <lon1,lon2>
	 * @return
	 */
	public Pair<Pair<BigDecimal,BigDecimal>,Pair<BigDecimal,BigDecimal>> getMapBoundaries() {
		Pair<BigDecimal, BigDecimal> latBounds = ((GeoStreetDAO) streetDAO).getLatitudeBoundaries();
		Log.debug("Lat min=" + latBounds.getFirst() + " max=" + latBounds.getSecond());

		Pair<BigDecimal, BigDecimal> lonBounds = ((GeoStreetDAO) streetDAO).getLongitudeBoundaries();
		Log.debug("Lon min=" + lonBounds.getFirst() + " max=" + lonBounds.getSecond());
		
		return new Pair<Pair<BigDecimal,BigDecimal>,Pair<BigDecimal,BigDecimal>>(latBounds,lonBounds);
	}
	
	/**
	 * Compute the tile map for the specified tile ids
	 * @param tileIds
	 * @return
	 */
	public String updateMap(List<Pair<Long, Long>> tileIds) {
		int added = 0;
		int updated = 0;
		for (Pair<Long, Long> id : tileIds) {
			Log.debug("Build tile latId=" + id.getFirst() + " lonId=" + id.getSecond());
			Tile tile = buildTile(id);
			TileContainer tileCont = tileDAO.getTile(id);
			if (tileCont == null) {
				tileCont = new TileContainer();
				tileCont.setTileId(id);
				tileCont.setTile(tile);
				tileDAO.persist(tileCont);
				Log.debug("New tile added");
				added++;
			} else {
				tileCont.setTile(tile);
				tileDAO.update(tileCont);
				Log.debug("Tile updated");
				updated++;
			}
		}
		return added + " tiles added and " + updated + " tiles updated";
	}

	/**
	 * Set the tile partitioning size and update the tile map
	 * @param latRanges
	 * @param lonRanges
	 * @return
	 */
	public String computeMap(List<BigDecimal> latRanges, List<BigDecimal> lonRanges) {
		// update TileHelper
		if (TileHelper.updateConfiguration(latRanges, lonRanges)) {
			Log.debug("Tile range changed");
			// clear all tiles
			tileDAO.deleteAll();
			// and finally update the map
			return updateMap();
		}
		Log.debug("No changes to tile range");
		return "No changes";
	}

	/**
	 * Set the tile partitioning size and update the tile map
	 * @param latRange
	 * @param lonRange
	 * @return
	 */
	public String computeMap(BigDecimal latRange, BigDecimal lonRange) {
		List<BigDecimal> latRanges = new ArrayList<BigDecimal>();
		List<BigDecimal> lonRanges = new ArrayList<BigDecimal>();
		latRanges.add(latRange);
		lonRanges.add(lonRange);

		return computeMap(latRanges,lonRanges);
	}

	/**
	 * Get the tile (or group of tiles unified into a new tile) needed for
	 * computing P2PSP from "from" to "to"
	 */
	public Tile getTile(BigDecimal latFrom, BigDecimal lonFrom, BigDecimal latTo, BigDecimal lonTo) {

		List<Pair<Long, Long>> tileIds = TileHelper.getInstance().getListTileId(latFrom, lonFrom, latTo, lonTo);
		@SuppressWarnings("unchecked")
		List<TileContainerInterface> tileConts = (List<TileContainerInterface>) tileCache.getTiles(tileIds);

		// System.out.println("Tiles number " + tileConts.size());
		Tile tile = new Tile();
		tile.setAdjacencyMatrix(HashBasedTable.create());
		tile.setIntersections(new HashMap<Long, IntersectionInterface>());
		tile.setWeightsMatrix(HashBasedTable.create());

		for (TileContainerInterface tc : tileConts) {
			Log.debug("Tile id: " + tc.getTileId().getFirst() + " " + tc.getTileId().getSecond());
			Log.debug("Intersections: " + tc.getTile().getIntersections().size());
			Log.debug("Weights: " + tc.getTile().getWeightsMatrix().size());
			Log.debug("Edges: " + tc.getTile().getAdjacencyMatrix().size());
			tile.appendAdjacencyMatrix(tc.getTile().getAdjacencyMatrix());
			tile.appendIntersections(tc.getTile().getIntersections());
			tile.appendWeights(tc.getTile().getWeightsMatrix());
		}

		return tile;
	}
	
	public Tile getEnlargedTile(BigDecimal latFrom, BigDecimal lonFrom, BigDecimal latTo, BigDecimal lonTo) {
		BigDecimal _latFrom, _latTo, _lonFrom, _lonTo;
		if(latFrom.compareTo(latTo)<0) {
			_latFrom = latFrom.subtract(DELTA);
			_latTo = latTo.add(DELTA);
		} else {
			_latFrom = latTo.subtract(DELTA);
			_latTo = latFrom.add(DELTA);
		}
		
		if(lonFrom.compareTo(lonTo)<0) {
			_lonFrom = lonFrom.subtract(DELTA);
			_lonTo = lonTo.add(DELTA);
		} else {
			_lonFrom = lonTo.subtract(DELTA);
			_lonTo = lonFrom.add(DELTA);
		}
		
		List<Pair<Long, Long>> tileIds = TileHelper.getInstance().getListTileId(_latFrom, _lonFrom, _latTo, _lonTo);
		@SuppressWarnings("unchecked")
		List<TileContainerInterface> tileConts = (List<TileContainerInterface>) tileCache.getTiles(tileIds);

		// System.out.println("Tiles number " + tileConts.size());
		Tile tile = new Tile();
		tile.setAdjacencyMatrix(HashBasedTable.create());
		tile.setIntersections(new HashMap<Long, IntersectionInterface>());
		tile.setWeightsMatrix(HashBasedTable.create());

		for (TileContainerInterface tc : tileConts) {
			Log.debug("Tile id: " + tc.getTileId().getFirst() + " " + tc.getTileId().getSecond());
			Log.debug("Intersections: " + tc.getTile().getIntersections().size());
			Log.debug("Weights: " + tc.getTile().getWeightsMatrix().size());
			Log.debug("Edges: " + tc.getTile().getAdjacencyMatrix().size());
			tile.appendAdjacencyMatrix(tc.getTile().getAdjacencyMatrix());
			tile.appendIntersections(tc.getTile().getIntersections());
			tile.appendWeights(tc.getTile().getWeightsMatrix());
		}

		return tile;
	}
	
	public Pair<List<BigDecimal>,List<BigDecimal>> getRanges() {
		return TileHelper.getInstance().getRanges();
	}

}
