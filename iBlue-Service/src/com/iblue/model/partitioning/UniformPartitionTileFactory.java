package com.iblue.model.partitioning;

import java.math.BigDecimal;
import java.util.List;

import com.iblue.model.db.TileRange;
import com.iblue.model.db.dao.TileRangeDAO;
import com.iblue.utils.Log;

public class UniformPartitionTileFactory implements PartitionFactoryInterface {

	@Override
	public UniformPartitionTile loadFromDb() {
		TileRangeDAO dao = new TileRangeDAO();
		TileRange range = dao.getTileRange();
		if(range==null) {
			Log.warning("No Tile Range found");
			return loadFromConfiguration(new BigDecimal(0.1d), new BigDecimal(0.1));
		}
		return new UniformPartitionTile(range.getLatitudeRange(), range.getLongitudeRange());
	}

	private UniformPartitionTile loadFromConfiguration(BigDecimal latRange, BigDecimal lonRange) {
		BigDecimal tempLat = latRange.setScale(UniformPartitionTile.LAT_LON_SCALE, BigDecimal.ROUND_HALF_DOWN);
		BigDecimal tempLon = lonRange.setScale(UniformPartitionTile.LAT_LON_SCALE, BigDecimal.ROUND_HALF_DOWN);

		TileRangeDAO dao = new TileRangeDAO();
		TileRange range = dao.getTileRange();
		if (range != null) {
			range.setLatitudeRange(tempLat);
			range.setLongitudeRange(tempLon);
			dao.update(range);
			Log.debug("Configuration updated for UniformPartitionTile " + tempLat + " " + tempLon);
		} else {
			range = new TileRange();
			range.setId(1l);
			range.setLatitudeRange(tempLat);
			range.setLongitudeRange(tempLon);
			dao.persist(range);
			Log.debug("New configuration stored for UniformPartitionTile " + tempLat + " " + tempLon);
		}
		return new UniformPartitionTile(range.getLatitudeRange(), range.getLongitudeRange());
	}

	@Override
	public PartitionTileInterface loadFromConfiguration(List<BigDecimal> latRanges, List<BigDecimal> lonRanges) {
		return loadFromConfiguration(latRanges.get(0), lonRanges.get(0));
	}
}
