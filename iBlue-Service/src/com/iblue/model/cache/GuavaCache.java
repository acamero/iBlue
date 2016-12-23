package com.iblue.model.cache;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.iblue.model.TileCacheInterface;
import com.iblue.model.TileContainerInterface;
import com.iblue.model.db.dao.TileDAO;
import com.iblue.utils.Log;
import com.iblue.utils.Pair;

public class GuavaCache implements TileCacheInterface {

	private static GuavaCache INSTANCE = new GuavaCache();

	public static GuavaCache getInstance() {
		return INSTANCE;
	}

	private LoadingCache<String, TileContainerInterface> cache;
	private int cacheSize;

	private GuavaCache() {
		loadProperties();
		cache = CacheBuilder.newBuilder().maximumSize(cacheSize).expireAfterAccess(30, TimeUnit.MINUTES)
				.build(new CacheLoader<String, TileContainerInterface>() {

					@Override
					public TileContainerInterface load(String cacheId) throws Exception {
						TileDAO tileDAO = new TileDAO();
						Log.debug("Load from db " + cacheId);
						return tileDAO.getTile(decodeCacheId(cacheId));
					}
				});
	}

	@Override
	public List<? extends TileContainerInterface> getTiles(List<Pair<Long, Long>> tileIds) {
		List<TileContainerInterface> tiles = new ArrayList<TileContainerInterface>();
		for (Pair<Long, Long> id : tileIds) {
			try {
				tiles.add(cache.get(encodeCacheId(id)));
			} catch (ExecutionException e) {
				e.printStackTrace();
			}
		}
		return tiles;
	}

	protected Pair<Long, Long> decodeCacheId(String cacheId) {
		String[] ids = cacheId.split(";");
		Log.debug("Id decoded: " + ids[0] + " " + ids[1]);
		return new Pair<Long, Long>(Long.valueOf(ids[0]), Long.valueOf(ids[1]));
	}

	protected String encodeCacheId(Pair<Long, Long> tileId) {
		return tileId.getFirst() + ";" + tileId.getSecond();
	}

	private void loadProperties() {
		ClassLoader classLoader = getClass().getClassLoader();
		InputStream propFile = null;

		try {
			File file = new File(classLoader.getResource("service.properties").getFile());
			propFile = new FileInputStream(file);
			Properties prop = new Properties();
			prop.load(propFile);
			cacheSize = Integer.valueOf(prop.getProperty("service.cache.size"));
			Log.debug("Properties cache size=" + cacheSize);
		} catch (IOException e) {
			e.printStackTrace();
			Log.debug("Unable to load cache properties");
			cacheSize = 10;
		} finally {
			if (propFile != null) {
				try {
					propFile.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

	}

}
