package com.iblue.model;

import java.util.List;

import com.iblue.utils.Pair;

public interface TileCacheInterface {
	
	public List<? extends TileContainerInterface> getTiles(List<Pair<Long, Long>> tileIds);

}
