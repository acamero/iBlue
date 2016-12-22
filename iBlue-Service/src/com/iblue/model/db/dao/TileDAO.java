package com.iblue.model.db.dao;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.query.Query;

import com.iblue.model.db.TileContainer;
import com.iblue.utils.Pair;

public class TileDAO extends MasterDAO {

	public TileContainer persist(TileContainer tile) {
		saveTx(tile);
		return tile;
	}

	public TileContainer update(TileContainer tile) {
		updateTx(tile);
		return tile;
	}

	public void delete(TileContainer tile) {
		deleteTx(tile);
	}

	public TileContainer getTile(Pair<Long, Long> tileId) {
		openTx();
		Query<TileContainer> query = session.createQuery(
				"from TileContainer where idLatitude = :idLat and idLongitude = :idLon", TileContainer.class);
		query.setParameter("idLat", tileId.getFirst());
		query.setParameter("idLon", tileId.getSecond());
		List<TileContainer> tiles = query.getResultList();
		closeTx();
		if (tiles.isEmpty()) {
			return null;
		}
		return tiles.get(0);
	}

	public List<TileContainer> getTiles(List<Pair<Long, Long>> tileIds) {
		List<TileContainer> tileConts = new ArrayList<TileContainer>();

		openTx();
		for (Pair<Long, Long> tileId : tileIds) {
			Query<TileContainer> query = session.createQuery(
					"from TileContainer where idLatitude = :idLat and idLongitude = :idLon", TileContainer.class);
			query.setParameter("idLat", tileId.getFirst());
			query.setParameter("idLon", tileId.getSecond());
			List<TileContainer> tiles = query.getResultList();
			if(!tiles.isEmpty()) {
				tileConts.add(tiles.get(0));
			}
		}
		closeTx();
		
		return tileConts;
	}
}
