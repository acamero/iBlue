package com.iblue.service;

import java.math.BigDecimal;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import org.json.JSONArray;
import org.json.JSONException;

import com.iblue.model.ParkingAllocInterface;
import com.iblue.model.StreetAvailabilityInterface;
import com.iblue.model.db.service.ParkingAlloc;
import com.iblue.model.msg.SpotJSON;
import com.iblue.utils.Log;

@Path("/map")
public class MapService {

	@GET
	@Path("/availability/{latitude}/{longitude}")
	@Produces("application/json")
	public Response getActiveStreets(@PathParam("latitude") float latitude, @PathParam("longitude") float longitude)
			throws JSONException {
		Log.info("Map availability lat=" + latitude + " lon=" + longitude);

		JSONArray jsonArray = new JSONArray();
		ParkingAllocInterface alloc = new ParkingAlloc();
		SpotJSON spot = new SpotJSON();
		spot.setLatLong(BigDecimal.valueOf(latitude), BigDecimal.valueOf(longitude));
		List<? extends StreetAvailabilityInterface> map = alloc.getNearStreetAvailability(spot);
		for (StreetAvailabilityInterface st : map) {
			// System.out.println(st.toString());
			jsonArray.put(st.toString());
		}
		return Response.status(200).entity(jsonArray.toString()).build();
	}
}
