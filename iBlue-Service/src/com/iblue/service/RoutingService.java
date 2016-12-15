package com.iblue.service;

import java.math.BigDecimal;
import java.util.LinkedList;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import org.json.JSONArray;
import org.json.JSONException;

import com.iblue.model.db.GeoStreet;
import com.iblue.model.db.ParkingAlloc;
import com.iblue.model.db.Spot;
import com.iblue.model.db.StreetDAO;
import com.iblue.model.db.WeightedStreet;
import com.iblue.model.db.WeightedStreetDAO;
import com.iblue.path.AStarAlgorithm;
import com.iblue.path.AlgorithmInterface;
import com.iblue.path.DijkstraAlgorithm;

@Path("/route")
public class RoutingService {
	
	private LinkedList<WeightedStreet> getPath(float latitude1, float longitude1, float latitude2,
			float longitude2, AlgorithmInterface alg) {
		Spot origin = new Spot();
		origin.setLatLong(new BigDecimal(latitude1), new BigDecimal(longitude1));
		Spot destination = new Spot();
		destination.setLatLong(new BigDecimal(latitude2), new BigDecimal(longitude2));

		ParkingAlloc park = new ParkingAlloc();
		long originId = park.getNearestStreetId(origin);
		long destId = park.getNearestStreetId(destination);
		StreetDAO stDao = new StreetDAO();
		GeoStreet stOrig = (GeoStreet) stDao.getStreet(originId);
		GeoStreet stDest = (GeoStreet) stDao.getStreet(destId);

		WeightedStreetDAO dao = new WeightedStreetDAO();
		List<WeightedStreet> map = dao.getGreatCircle(origin, destination);
		
		alg.setEdges(map);

		@SuppressWarnings("unchecked")
		LinkedList<WeightedStreet> streets = (LinkedList<WeightedStreet>) alg.getPath(stOrig.getFromIntersection(),
				stDest.getToIntersection());
		
		return streets;
	}
	
	private LinkedList<WeightedStreet> getRouteAStar(float latitude1, float longitude1, float latitude2,
			float longitude2) {	

		AlgorithmInterface alg = new AStarAlgorithm();
		LinkedList<WeightedStreet> streets = getPath(latitude1,longitude1,latitude2,longitude2, alg);

		return streets;
	}

	private LinkedList<WeightedStreet> getRouteDijkstra(float latitude1, float longitude1, float latitude2,
			float longitude2) {	

		AlgorithmInterface alg = new DijkstraAlgorithm();
		LinkedList<WeightedStreet> streets = getPath(latitude1,longitude1,latitude2,longitude2, alg);

		return streets;
	}

	@GET
	@Path("/from/{latitude1}/{longitude1}/to/{latitude2}/{longitude2}")
	@Produces("application/json")
	public Response getDijkstra(@PathParam("latitude1") float latitude1, @PathParam("longitude1") float longitude1,
			@PathParam("latitude2") float latitude2, @PathParam("longitude2") float longitude2) throws JSONException {
		System.out.println(
				"Route from lat=" + latitude1 + " lon=" + longitude1 + " to lat=" + latitude2 + " lon=" + longitude2);

		JSONArray jsonArray = new JSONArray();
		LinkedList<WeightedStreet> route = getRouteDijkstra(latitude1, longitude1, latitude2, longitude2);

		for (WeightedStreet st : route) {
			// System.out.println(st.toString());
			jsonArray.put(st.toString());
		}
		return Response.status(200).entity(jsonArray.toString()).build();
	}

	public static void main(String[] args) {
		float latitude1 = 36.716169f;
		float longitude1 = -4.479118f;
		float latitude2 = 36.726351f;
		float longitude2 = -4.479890f;

		RoutingService serv = new RoutingService();
		LinkedList<WeightedStreet> streetIds = serv.getRouteAStar(latitude1, longitude1, latitude2, longitude2);
		//LinkedList<WeightedStreet> streetIds = serv.getRouteDijkstra(latitude1, longitude1, latitude2, longitude2);

		for (WeightedStreet st : streetIds) {
			System.out.println("Street: " + st.toString());
		}

		System.exit(0);
	}

}
