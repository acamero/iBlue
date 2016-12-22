package com.iblue.service;

import java.math.BigDecimal;
import java.util.LinkedList;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import org.json.JSONArray;
import org.json.JSONException;

import com.iblue.model.GeoStreetInterface;
import com.iblue.model.IntersectionInterface;
import com.iblue.model.ParkingAllocInterface;
import com.iblue.model.StreetDAOInterface;
import com.iblue.model.TileServiceInterface;
import com.iblue.model.db.Spot;
import com.iblue.model.db.dao.GeoStreetDAO;
import com.iblue.model.db.service.ParkingAlloc;
import com.iblue.model.db.service.TileService;
import com.iblue.path.AlgorithmInterface;
import com.iblue.path.Dijkstra;
import com.iblue.path.GraphInterface;

@Path("/route")
public class RoutingService {

	private LinkedList<IntersectionInterface> getPath(float latitude1, float longitude1, float latitude2,
			float longitude2, AlgorithmInterface alg) {
		
		// 
		Spot origin = new Spot();
		origin.setLatLong(new BigDecimal(latitude1), new BigDecimal(longitude1));		
		Spot destination = new Spot();
		destination.setLatLong(new BigDecimal(latitude2), new BigDecimal(longitude2));

		ParkingAllocInterface park = new ParkingAlloc();
		long originId = park.getNearestStreetId(origin);
		long destId = park.getNearestStreetId(destination);
		StreetDAOInterface stDao = new GeoStreetDAO();
		GeoStreetInterface stOrig = (GeoStreetInterface) stDao.getStreet(originId);
		GeoStreetInterface stDest = (GeoStreetInterface) stDao.getStreet(destId);

		System.out.println("Setting graph " + System.currentTimeMillis());
		TileServiceInterface serv = new TileService();
		GraphInterface graph = serv.getTile(origin.getLatitude(), origin.getLongitude(), destination.getLatitude(),
				destination.getLongitude());
		alg.setGraph(graph);

		System.out.println("Start routing " + System.currentTimeMillis());

		LinkedList<IntersectionInterface> path = alg.getPath(stOrig.getFromIntersection(),
				stDest.getFromIntersection());
		System.out.println("Route found " + System.currentTimeMillis());

		return path;
	}

	@GET
	@Path("/from/{latitude1}/{longitude1}/to/{latitude2}/{longitude2}")
	@Produces("application/json")
	public Response getRoute(@PathParam("latitude1") float latitude1, @PathParam("longitude1") float longitude1,
			@PathParam("latitude2") float latitude2, @PathParam("longitude2") float longitude2) throws JSONException {
		System.out.println(
				"Route from lat=" + latitude1 + " lon=" + longitude1 + " to lat=" + latitude2 + " lon=" + longitude2);

		// TODO include algorithm parameters in the RQ
		
		JSONArray jsonArray = new JSONArray();
		
		// TODO change algorithm selection
		AlgorithmInterface alg = new Dijkstra();
		LinkedList<IntersectionInterface> path = getPath(latitude1, longitude1, latitude2, longitude2, alg);

		for (IntersectionInterface st : path) {
			// System.out.println(st.toString());
			jsonArray.put(st.toString());
		}
		return Response.status(200).entity(jsonArray.toString()).build();
	}

	@GET
	@Path("/compute/{key}")
	@Produces("application/json")
	public Response computeMap(@PathParam("key") String key) {
		String resp = "{\"compute\":\"key error\"}";
		if (key.equals("aq1sw2de3")) {
			Thread computer = new Thread(new Runnable() {
				public void run() {
					System.out.println("Start computing in new trhread");;
					TileServiceInterface service = new TileService();
					String compResp = service.computeMap();
					System.out.println("Finish computing "+ compResp);
				}
			});
			computer.start();
			resp = "{\"compute\":\"processing in another thread\"}";
		}
		return Response.status(200).entity(resp).build();
	}
	
	

}
