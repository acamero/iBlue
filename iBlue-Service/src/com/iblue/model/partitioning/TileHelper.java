package com.iblue.model.partitioning;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.List;
import java.util.Properties;

import com.iblue.model.db.dao.GeoStreetDAO;
import com.iblue.model.db.service.TileService;
import com.iblue.utils.Log;
import com.iblue.utils.Pair;

public class TileHelper {

	private static PartitionTileInterface INSTANCE;
	private static PartitionFactoryInterface FACTORY = getFactory();

	private static PartitionFactoryInterface getFactory() {
		ClassLoader classLoader = TileHelper.class.getClassLoader();
		InputStream propFile = null;
		PartitionFactoryInterface factory = null;
		boolean reload = false;
		try {
			// File file = new File(classLoader.getResource("service.properties").getFile());
			propFile = classLoader.getResourceAsStream("service.properties");
			Properties prop = new Properties();
			prop.load(propFile);
			// select the type of partition
			String partitioningClass = prop.getProperty("service.partitioning.factory.class");
			Log.debug("Properties cache class=" + partitioningClass);
			Class<?> _class = Class.forName(partitioningClass);
			factory = (PartitionFactoryInterface) _class.newInstance();
			// select user option to enforce partition processing on startup
			String reloadStr = prop.getProperty("service.partitioning.reload");
			Log.debug("Properties partitioning reprocessing=" + reloadStr);
			if (reloadStr.equals("true")) {
				reload = true;
			}
		} catch (IOException | IllegalAccessException | ClassNotFoundException | SecurityException
				| IllegalArgumentException | InstantiationException e) {
			e.printStackTrace();
			Log.error("Unable to load partitioning factory class from properties");
			factory = new UniformPartitionTileFactory();
			Log.warning("Using default partitioing factory UniformPartitionTileFactory");
		} finally {
			if (propFile != null) {
				try {
					propFile.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		INSTANCE = factory.loadFromDb();

		// re-compute the tile map
		if (reload) {
			Log.info("Re-computing the tile map on startup");
			GeoStreetDAO streetDAO = new GeoStreetDAO();
			Pair<BigDecimal, BigDecimal> latBounds = streetDAO.getLatitudeBoundaries();
			Log.debug("Lat min=" + latBounds.getFirst() + " max=" + latBounds.getSecond());
			Pair<BigDecimal, BigDecimal> lonBounds = streetDAO.getLongitudeBoundaries();
			Log.debug("Lon min=" + lonBounds.getFirst() + " max=" + lonBounds.getSecond());
			List<Pair<Long, Long>> tileIds = INSTANCE.getBoundariesTileId(latBounds, lonBounds);
			TileService serv = new TileService();
			serv.updateMap(tileIds);
		}

		return factory;
	}

	/**
	 * Returns the instanced interface to be used for mapping partitions (tiles)
	 * to coordinates
	 * 
	 * @return
	 */
	public static PartitionTileInterface getInstance() {
		return INSTANCE;
	}

	public static boolean updateConfiguration(List<BigDecimal> latRanges, List<BigDecimal> lonRanges) {
		Pair<List<BigDecimal>, List<BigDecimal>> ranges = INSTANCE.getRanges();
		boolean hasChanged = false;

		if (ranges.getFirst().size() != latRanges.size()) {
			hasChanged = true;
			Log.debug("Different latitude range size");
		} else {
			for (int i = 0; i < ranges.getFirst().size(); i++) {
				if (ranges.getFirst().get(i).compareTo(
						latRanges.get(i).setScale(ranges.getFirst().get(i).scale(), BigDecimal.ROUND_HALF_DOWN)) != 0) {
					hasChanged = true;
					Log.debug("Different latitude range " + latRanges.get(i) + " " + ranges.getFirst().get(i));
				}
			}
		}

		if (ranges.getSecond().size() != lonRanges.size()) {
			hasChanged = true;
			Log.debug("Different longitude range size");
		} else {
			for (int i = 0; i < ranges.getSecond().size(); i++) {
				if (ranges.getSecond().get(i).compareTo(lonRanges.get(i).setScale(ranges.getSecond().get(i).scale(),
						BigDecimal.ROUND_HALF_DOWN)) != 0) {
					hasChanged = true;
					Log.debug("Different longitude range " + lonRanges.get(i) + " " + ranges.getSecond().get(i));
				}
			}
		}

		if (hasChanged) {
			INSTANCE = FACTORY.loadFromConfiguration(latRanges, lonRanges);
			Log.info("Parititoning configuration updated");
		} else {
			Log.info("Partitioning configuration preserved");
		}

		return hasChanged;
	}
}
