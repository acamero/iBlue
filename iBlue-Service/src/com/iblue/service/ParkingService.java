package com.iblue.service;

import java.math.BigDecimal;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import org.json.JSONException;

import com.iblue.model.ParkingAllocInterface;
import com.iblue.model.StreetAvailabilityInterface;
import com.iblue.model.db.service.ParkingAlloc;
import com.iblue.model.msg.SpotJSON;
import com.iblue.utils.Log;

@Path("/parkme")
public class ParkingService {

	@GET
	@Path("/closest/{latitude}/{longitude}")
	@Produces("application/json")
	public Response closestParking(@PathParam("latitude") float latitude, @PathParam("longitude") float longitude)
			throws JSONException {
		Log.info("Parking closest to lat=" + latitude + " lon=" + longitude);

		ParkingAllocInterface alloc = new ParkingAlloc();
		SpotJSON spot = new SpotJSON();
		spot.setLatLong(BigDecimal.valueOf(latitude), BigDecimal.valueOf(longitude));
		StreetAvailabilityInterface street = alloc.parkMeClosest(spot);
		if (street != null) {
			Log.debug(street.toString());
			return Response.status(200).entity(street.toString()).build();
		} else {
			Log.debug("No street available in the neighborhood");
			return Response.status(200).entity("No street available in the neighborhood").build();
		}
	}
	
	
}
