package com.iblue.model.db.dao;

import java.util.List;

import org.hibernate.query.Query;

import com.iblue.model.db.TileRange;
import com.iblue.utils.Log;

public class TileRangeDAO extends MasterDAO {

	public TileRange getTileRange() {
		openTx();
		Query<TileRange> query = session.createQuery("from TileRange where id = 1", TileRange.class);
		List<TileRange> ranges = query.getResultList();
		closeTx();
		if(ranges.isEmpty()) {
			Log.error("Tile range record not found");
			return null;
		}
		return ranges.get(0);
	}
	
	public void update(TileRange range) {
		updateTx(range);		
	}
}
