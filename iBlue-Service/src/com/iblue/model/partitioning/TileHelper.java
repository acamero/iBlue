package com.iblue.model.partitioning;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.List;
import java.util.Properties;

import com.iblue.model.db.service.TileService;
import com.iblue.utils.Log;
import com.iblue.utils.Pair;

public class TileHelper {

	private static PartitionTileInterface INSTANCE;
	private static PartitionFactoryInterface FACTORY = getFactory();

	private static PartitionFactoryInterface getFactory() {
		ClassLoader classLoader = TileService.class.getClassLoader();
		InputStream propFile = null;
		PartitionFactoryInterface factory = null;
		try {
			File file = new File(classLoader.getResource("service.properties").getFile());
			propFile = new FileInputStream(file);
			Properties prop = new Properties();
			prop.load(propFile);
			String partitioningClass = prop.getProperty("service.partitioning.factory.class");
			Log.debug("Properties cache class=" + partitioningClass);
			Class<?> _class = Class.forName(partitioningClass);			
			factory = (PartitionFactoryInterface) _class.newInstance();			
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
		}

		if (ranges.getSecond().size() != lonRanges.size()) {
			hasChanged = true;
			Log.debug("Different longitude range size");
		}

		for (int i = 0; i < ranges.getFirst().size(); i++) {
			if (ranges.getFirst().get(i).compareTo(
					latRanges.get(i).setScale(ranges.getFirst().get(i).scale(), BigDecimal.ROUND_HALF_DOWN)) != 0) {
				hasChanged = true;
				Log.debug("Different latitude range " + latRanges.get(i) + " " + ranges.getFirst().get(i));
			}
		}

		for (int i = 0; i < ranges.getSecond().size(); i++) {
			if (ranges.getSecond().get(i).compareTo(
					lonRanges.get(i).setScale(ranges.getSecond().get(i).scale(), BigDecimal.ROUND_HALF_DOWN)) != 0) {
				hasChanged = true;
				Log.debug("Different latitude range " + lonRanges.get(i) + " " + ranges.getSecond().get(i));
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
