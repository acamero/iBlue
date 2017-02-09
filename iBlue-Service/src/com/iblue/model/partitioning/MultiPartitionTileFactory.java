package com.iblue.model.partitioning;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import java.util.List;

import com.iblue.model.db.TileRange;
import com.iblue.model.db.dao.GeoStreetDAO;
import com.iblue.model.db.dao.TileRangeDAO;
import com.iblue.utils.Log;
import com.iblue.utils.Pair;

public class MultiPartitionTileFactory implements PartitionFactoryInterface {

	private static final BigDecimal DEFAULT_NULL_BD = new BigDecimal(-1).setScale(7, BigDecimal.ROUND_HALF_DOWN);

	@Override
	public MultiPartitionTile loadFromDb() {
		TileRangeDAO dao = new TileRangeDAO();
		List<TileRange> ranges = dao.getTileRanges();
		if (ranges == null) {
			Log.warning("No Tile Range configuration found");
			List<BigDecimal> latRanges = new ArrayList<BigDecimal>();
			List<BigDecimal> lonRanges = new ArrayList<BigDecimal>();
			latRanges.add(new BigDecimal(0.5d));
			lonRanges.add(new BigDecimal(0.5d));
			return loadFromConfiguration(latRanges, lonRanges);
		}

		Pair<List<BigDecimal>, List<BigDecimal>> pRanges = dbToRanges(ranges);

		GeoStreetDAO streetDAO = new GeoStreetDAO();
		Pair<BigDecimal, BigDecimal> latBounds = streetDAO.getLatitudeBoundaries();
		Log.debug("Lat min=" + latBounds.getFirst() + " max=" + latBounds.getSecond());

		Pair<BigDecimal, BigDecimal> lonBounds = streetDAO.getLongitudeBoundaries();
		Log.debug("Lon min=" + lonBounds.getFirst() + " max=" + lonBounds.getSecond());

		return loadFromConfiguration(pRanges.getFirst(), pRanges.getSecond(), latBounds, lonBounds);
	}

	@Override
	public MultiPartitionTile loadFromConfiguration(List<BigDecimal> latRanges, List<BigDecimal> lonRanges) {
		GeoStreetDAO streetDAO = new GeoStreetDAO();
		Pair<BigDecimal, BigDecimal> latBounds = streetDAO.getLatitudeBoundaries();
		Log.debug("Lat min=" + latBounds.getFirst() + " max=" + latBounds.getSecond());

		Pair<BigDecimal, BigDecimal> lonBounds = streetDAO.getLongitudeBoundaries();
		Log.debug("Lon min=" + lonBounds.getFirst() + " max=" + lonBounds.getSecond());

		rangesToDb(latRanges, lonRanges);

		return loadFromConfiguration(latRanges, lonRanges, latBounds, lonBounds);
	}

	public MultiPartitionTile loadFromConfiguration(List<BigDecimal> latRanges, List<BigDecimal> lonRanges,
			Pair<BigDecimal, BigDecimal> latBounds, Pair<BigDecimal, BigDecimal> lonBounds) {
				
		if(latRanges.isEmpty()) {
			Log.warning("Adding default latitude partition (0)");
			latRanges.add(new BigDecimal(0));
		}
		
		if(lonRanges.isEmpty()) {
			Log.warning("Adding default longitude partition (0)");
			lonRanges.add(new BigDecimal(0));
		}
		
		Pair<List<BigDecimal>, List<BigDecimal>> ranges = new Pair<List<BigDecimal>, List<BigDecimal>>(latRanges,
				lonRanges);
		
		return new MultiPartitionTile(ranges, latBounds, lonBounds);
	}

	private void rangesToDb(List<BigDecimal> latRanges, List<BigDecimal> lonRanges) {
		long id = 1;
		TileRangeDAO dao = new TileRangeDAO();
		dao.deleteAll();

		for (BigDecimal r : latRanges) {
			TileRange temp = new TileRange();
			temp.setId(id);
			temp.setLatitudeRange(r);
			temp.setLongitudeRange(DEFAULT_NULL_BD);
			dao.persist(temp);
			id++;
		}

		for (BigDecimal r : lonRanges) {
			TileRange temp = new TileRange();
			temp.setId(id);
			temp.setLatitudeRange(DEFAULT_NULL_BD);
			temp.setLongitudeRange(r);
			dao.persist(temp);
			id++;
		}
	}

	private Pair<List<BigDecimal>, List<BigDecimal>> dbToRanges(List<TileRange> ranges) {
		List<BigDecimal> latRanges = new ArrayList<BigDecimal>();
		List<BigDecimal> lonRanges = new ArrayList<BigDecimal>();
		// sort by id
		Collections.sort(ranges, new TileRangeComparator());
		for (int i=0;i<ranges.size();i++) {
			if(ranges.get(i).getLatitudeRange().compareTo(DEFAULT_NULL_BD)==0) {
				lonRanges.add(ranges.get(i).getLongitudeRange());
				Log.debug("Lon range added: "+ranges.get(i).getLongitudeRange());
			} else {
				latRanges.add(ranges.get(i).getLatitudeRange());
				Log.debug("Lat range added: "+ranges.get(i).getLatitudeRange());
			}
		}
		Log.debug("Decoded from DB: lat-ranges="+latRanges.size()+"\tlon-ranges="+lonRanges.size());
		return new Pair<List<BigDecimal>, List<BigDecimal>>(latRanges, lonRanges);
	}

	private class TileRangeComparator implements Comparator<TileRange> {

		@Override
		public int compare(TileRange arg0, TileRange arg1) {
			return (int) (arg0.getId() - arg1.getId());
		}

	}

}
