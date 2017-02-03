package test.iblue.service;

import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import javax.ws.rs.core.Response;

import org.junit.BeforeClass;
import org.junit.Test;

import com.iblue.model.GeoStreetInterface;
import com.iblue.model.IntersectionInterface;
import com.iblue.model.ParkingAllocInterface;
import com.iblue.model.StreetDAOInterface;
import com.iblue.model.db.Spot;
import com.iblue.model.db.dao.GeoStreetDAO;
import com.iblue.model.db.service.ParkingAlloc;
import com.iblue.model.db.service.TileService;
import com.iblue.path.AlgorithmInterface;
import com.iblue.path.Dijkstra;
import com.iblue.path.GraphInterface;
import com.iblue.service.RoutingService;
import com.iblue.utils.Log;
import com.iblue.utils.Pair;
import com.iblue.utils.Log.LogLevel;

public class RoutingServiceTest {

	private static List<Pair<GeoStreetInterface, GeoStreetInterface>> pairs;

	@BeforeClass
	public static void init() throws NumberFormatException, IOException {
		Log.setLogLevel(LogLevel.INFO);

		FileReader fr = new FileReader("spots-malaga.txt");
		BufferedReader br = new BufferedReader(fr);
		String line;
		List<Pair<Float, Float>> pairsSpot = new ArrayList<Pair<Float, Float>>();
		while ((line = br.readLine()) != null) {
			String[] nums = line.split(",");
			Pair<Float, Float> p = new Pair<Float, Float>(Float.parseFloat(nums[0]), Float.parseFloat(nums[1]));
			pairsSpot.add(p);
		}
		br.close();

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

		pairs = new ArrayList<Pair<GeoStreetInterface, GeoStreetInterface>>();
		for (int i = 0; i < spots.size(); i++) {
			for (int j = 0; j < spots.size(); j++) {
				if (i != j) {
					pairs.add(new Pair<GeoStreetInterface, GeoStreetInterface>(spots.get(i), spots.get(j)));
				}
			}
		}

		Log.debug(pairs.size() + " routes added");
	}

	@Test
	public void simple() {

		float latitude1 = 36.715064f;
		float longitude1 = -4.477191f;
		float latitude2 = 36.723061f;
		float longitude2 = -4.376468f;

		RoutingService serv = new RoutingService();

		Response resp = serv.getRoute(latitude1, longitude1, latitude2, longitude2);

		String body = (String) resp.getEntity();

		System.out.println(body);
		assertEquals(200, resp.getStatus());

	}

	@Test
	public void full() {
		TileService tileService = new TileService();
		long aggFitnessTime = 0l;
		
		for (int i = 0; i < pairs.size(); i++) {
			Pair<GeoStreetInterface, GeoStreetInterface> p = pairs.get(i);
			Log.debug("Setting graph " + System.currentTimeMillis());
			long begin = System.currentTimeMillis();
			GraphInterface graph = tileService.getTile(p.getFirst().getLatitude1(), p.getFirst().getLongitude1(),
					p.getSecond().getLatitude1(), p.getSecond().getLongitude1());
			AlgorithmInterface alg = new Dijkstra();
			alg.setGraph(graph);

			//long beginSearch = System.currentTimeMillis();
			LinkedList<IntersectionInterface> path = alg.getPath(p.getFirst().getFromIntersection(),
					p.getSecond().getFromIntersection());
			long end = System.currentTimeMillis();
			long time = end - begin;
			// long searchTime = end - beginSearch;
			long fitnessTime = time;

			boolean found = !path.isEmpty();
			if (!found) {
				fitnessTime += 20000;
			}
			aggFitnessTime += fitnessTime;
			Log.debug("Route " + i + " time " + time + " found " + found);
						
		}
		
		//Log.info("Fitness time: " + aggFitnessTime);
		Log.info("Avg Fitness:" + (double) (aggFitnessTime / pairs.size()));
	}

	@Test
	public void full2() {
		for(int i=0;i<31;i++) {
			full();		
		}
	}
}
