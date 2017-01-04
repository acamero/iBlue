package com.iblue.model.cache;

import java.util.List;

import com.iblue.model.TileCacheInterface;
import com.iblue.model.TileContainerInterface;
import com.iblue.model.db.dao.TileDAO;
import com.iblue.utils.Pair;

public class NoCache implements TileCacheInterface {
	
	private static NoCache INSTANCE = loadInstance();
	private TileDAO tileDAO;

	private static NoCache loadInstance() {
		return new NoCache();
	}
	
	public static NoCache getInstance() {
		return INSTANCE;
	}
	
	private NoCache() {
		tileDAO = new TileDAO();
	}
	
	@Override
	public List<? extends TileContainerInterface> getTiles(List<Pair<Long, Long>> tileIds) {
		
		return tileDAO.getTiles(tileIds);		
	}

}
