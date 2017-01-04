package com.iblue.model.db.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
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
import com.iblue.utils.Log;
import com.iblue.utils.Pair;

public class TileService implements TileServiceInterface {

	private StreetDAOInterface streetDAO;
	private TileDAO tileDAO;
	private TileCacheInterface tileCache = loadTileCache();
	


	public TileService() {
		streetDAO = new GeoStreetDAO();
		tileDAO = new TileDAO();		
	}

	private static TileCacheInterface loadTileCache() {
		ClassLoader classLoader = TileService.class.getClassLoader();
		InputStream propFile = null;
		TileCacheInterface cacheInterface = null;
		try {
			File file = new File(classLoader.getResource("service.properties").getFile());
			propFile = new FileInputStream(file);
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

	public Tile buildTile(Pair<Long, Long> id) {
		List<? extends GeoStreetInterface> streets = ((GeoStreetDAO)streetDAO).getTileBounded(id);
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

	public String updateMap() {		
		Pair<BigDecimal, BigDecimal> latBounds = ((GeoStreetDAO)streetDAO).getLatitudeBoundaries();
		Log.debug("Lat min=" + latBounds.getFirst() + " max=" + latBounds.getSecond());

		Pair<BigDecimal, BigDecimal> lonBounds = ((GeoStreetDAO)streetDAO).getLongitudeBoundaries();
		Log.debug("Lon min=" + lonBounds.getFirst() + " max=" + lonBounds.getSecond());

		int added = 0;
		int updated = 0;
		List<Pair<Long, Long>> tileIds = TileHelper.getInstance().getBoundariesTileId(latBounds, lonBounds);
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

	public String computeMap(BigDecimal latRange, BigDecimal lonRange) {
		// update TileHelper
		if( TileHelper.getInstance().setRange(latRange, lonRange) ) {
			Log.debug("Tile range changed");
			// clear all tiles
			tileDAO.deleteAll();
			// and finally update the map
			return updateMap();
		}
		Log.debug("No changes to tile range");
		return "No changes";
	}
	
	public Pair<BigDecimal,BigDecimal> getRange() {
		return TileHelper.getInstance().getRange();
	}

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

}
