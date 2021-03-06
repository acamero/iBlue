package com.iblue.service;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.json.JSONArray;
import org.json.JSONException;

import com.iblue.model.SpotDAOInterface;
import com.iblue.model.SpotInterface;
import com.iblue.model.db.dao.SpotDAO;
import com.iblue.queue.SpotSendQueueInterface;
import com.iblue.queue.mq.send.SpotSendQueueService;
import com.iblue.utils.Log;
import com.iblue.auth.AuthServiceInterface;
import com.iblue.auth.BasicAuthService;
import com.iblue.model.msg.SpotJSON;

@Path("/spot")
public class SpotService {

	@GET
	@Path("/active")
	@Produces("application/json")
	public Response getActiveSpots() throws JSONException {
		Log.info("Get all active spots");

		JSONArray jsonArray = new JSONArray();
		SpotDAOInterface spotDAO = new SpotDAO();
		List<? extends SpotInterface> spots = spotDAO.findAllActive();
		for (SpotInterface spot : spots) {
			// System.out.println(spot.toString());
			jsonArray.put(spot.toString());
		}

		return Response.status(200).entity(jsonArray.toString()).build();
	}

	@GET
	@Path("/active/{fromts}")
	@Produces("application/json")
	public Response getActiveSpotsFrom(@PathParam("fromts") String fromTsString) throws JSONException {
		Log.info("Active spots from " + fromTsString);

		JSONArray jsonArray = new JSONArray();
		SpotDAOInterface spotDAO = new SpotDAO();
		List<? extends SpotInterface> spots = spotDAO.findAllActive(Long.parseLong(fromTsString));
		for (SpotInterface spot : spots) {
			// System.out.println(spot.toString());
			jsonArray.put(spot.toString());
		}

		return Response.status(200).entity(jsonArray.toString()).build();
	}

	@GET
	@Path("/release/{fromts}")
	@Produces("application/json")
	public Response getReleasedSpotsFrom(@PathParam("fromts") String fromTsString) throws JSONException {
		Log.info("Released spots from " + fromTsString);

		JSONArray jsonArray = new JSONArray();
		SpotDAOInterface spotDAO = new SpotDAO();
		List<? extends SpotInterface> spots = spotDAO.findAllRelease(Long.parseLong(fromTsString));
		for (SpotInterface spot : spots) {
			// System.out.println(spot.toString());
			jsonArray.put(spot.toString());
		}

		return Response.status(200).entity(jsonArray.toString()).build();
	}

	@POST
	@Path("/set")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response setSpot(SpotJSON spot) {
		if (spot == null) {
			Log.warning("Set null spot");
			return Response.status(200).entity("NULL").build();
		}

		Log.info("Set spot" + spot.toString() + " (id=" + spot.getId() + ")");

		String response = "NO";

		AuthServiceInterface auth = new BasicAuthService();
		if (auth.isValidMsg(spot)) {
			SpotSendQueueInterface queue = SpotSendQueueService.getInstance();
			if (queue.send(spot)) {
				response = "OK";
			} else {
				Log.error("Unable to put message in queue");
			}
		} else {
			Log.warning("Unauthorized message");
		}

		return Response.status(200).entity(response).build();
	}
}
