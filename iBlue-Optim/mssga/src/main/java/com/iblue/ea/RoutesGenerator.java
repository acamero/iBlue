package com.iblue.ea;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.iblue.model.GeoStreetInterface;
import com.iblue.model.ParkingAllocInterface;
import com.iblue.model.StreetDAOInterface;
import com.iblue.model.db.Spot;
import com.iblue.model.db.dao.GeoStreetDAO;
import com.iblue.model.db.service.ParkingAlloc;
import com.iblue.utils.Log;
import com.iblue.utils.Pair;

public class RoutesGenerator {
	
	
	public List<Pair<GeoStreetInterface, GeoStreetInterface>> getRoutes(String filePath) throws IOException {
		return generateOD(loadSpots(filePath));
	}
	
	private List<Pair<Float, Float>> loadSpots(String filePath) throws IOException {
		FileReader fr = new FileReader(filePath);
		BufferedReader br = new BufferedReader(fr);
		String line;
		List<Pair<Float, Float>> pairs = new ArrayList<Pair<Float, Float>>();
		while ((line = br.readLine()) != null) {
			String[] nums = line.split(",");
			Pair<Float, Float> p = new Pair<Float, Float>(Float.parseFloat(nums[0]), Float.parseFloat(nums[1]));
			pairs.add(p);
		}
		br.close();
		return pairs;
	}
	
	private List<Pair<GeoStreetInterface, GeoStreetInterface>> generateOD(List<Pair<Float, Float>> pairsSpot) {
		List<GeoStreetInterface> spots = new ArrayList<GeoStreetInterface>();
		ParkingAllocInterface park = new ParkingAlloc();
		StreetDAOInterface stDao = new GeoStreetDAO();

		for (Pair<Float, Float> p : pairsSpot) {
			Spot spot = new Spot();
			spot.setLatLong(new BigDecimal(p.getFirst()), new BigDecimal(p.getSecond()));
			// System.out.println("Spot added " + spot.toString());
			long stId = park.getNearestStreetId(spot);
			GeoStreetInterface st = (GeoStreetInterface) stDao.getStreet(stId);
			// System.out.println("Street added " + st.getId());
			spots.add(st);
		}

		List<Pair<GeoStreetInterface, GeoStreetInterface>> pairs = new ArrayList<Pair<GeoStreetInterface, GeoStreetInterface>>();
		for (int i = 0; i < spots.size(); i++) {
			for (int j = 0; j < spots.size(); j++) {
				if (i != j) {
					pairs.add(new Pair<GeoStreetInterface, GeoStreetInterface>(spots.get(i), spots.get(j)));
				}
			}
		}

		Log.debug(pairs.size() + " routes added");

		return pairs;
	}
}
