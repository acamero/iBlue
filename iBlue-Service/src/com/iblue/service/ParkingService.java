package com.iblue.service;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import org.json.JSONException;

import com.iblue.model.ParkingAllocInterface;
import com.iblue.model.StreetAvailabilityInterface;
import com.iblue.model.db.ParkingAlloc;
import com.iblue.model.msg.SpotJSON;

@Path("/parkme")
public class ParkingService {

	@GET
	@Path("/closest/{latitude}/{longitude}")
	@Produces("application/json")
	public Response getActiveStreets(@PathParam("latitude") float latitude, @PathParam("longitude") float longitude)
			throws JSONException {
		System.out.println("Parking closest to lat="+latitude+" lon="+longitude);
		
		ParkingAllocInterface alloc = new ParkingAlloc();
		SpotJSON spot = new SpotJSON();
		spot.setLatLong(latitude, longitude);		
		StreetAvailabilityInterface street = alloc.parkMeClosest(spot);
		if( street!=null ) {
			return Response.status(200).entity(street.toString()).build();
		} else {
			return Response.status(200).entity("No street available in the neighborhood").build();
		}
	}
}
