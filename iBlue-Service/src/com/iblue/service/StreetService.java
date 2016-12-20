package com.iblue.service;

import java.util.List;

import javax.validation.Valid;
import javax.ws.rs.Consumes;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;


import com.iblue.auth.AuthServiceInterface;
import com.iblue.auth.BasicAuthService;
import com.iblue.model.StreetDAOInterface;
import com.iblue.model.msg.StreetJSON;
import com.iblue.model.SimpleStreetInterface;
import com.iblue.model.db.dao.GeoStreetDAO;

@Path("/street")
public class StreetService {

	@POST
	@Path("/bulk")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response addBulkStreet(@Valid List<StreetJSON> streets) {
		System.out.println("Load bulk streets");

		if (streets == null) {
			return Response.status(200).entity("Null list").build();
		}

		StreetDAOInterface dao = new GeoStreetDAO();
		int counter = 0;

		for (SimpleStreetInterface street : streets) {
			street = dao.persist(street);
			if (street != null) {
				counter++;
			}
		}

		return Response.status(200).entity(streets.size() + " streets received, " + counter + " streets uploaded")
				.build();
	}

	
//	@GET
//	@Path("/active")
//	@Produces("application/json")
//	public Response getActiveStreets() throws JSONException {
//		System.out.println("Get all active streets");
//
//		JSONArray jsonArray = new JSONArray();
//		StreetDAOInterface dao = new StreetDAO();
//		List<? extends StreetInterface> streets = dao.findAllActive();
//
//		for (StreetInterface st : streets) {
//			// System.out.println(st.toString());
//			jsonArray.put(st.toString());
//		}
//
//		return Response.status(200).entity(jsonArray.toString()).build();
//	}

	@POST
	@Path("/set")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response setStreet(StreetJSON street) {		
		if (street == null) {
			return Response.status(200).entity("Null street").build();
		}
		System.out.println("Set street "+ street.toString());
		

		AuthServiceInterface auth = new BasicAuthService();
		String response = "";
		if (auth.isValidMsg(street)) {
			
			StreetDAOInterface dao = new GeoStreetDAO();

			if (street.getId() > 0) {
				SimpleStreetInterface tmp = dao.update(street);
				if (tmp != null) {
					response = tmp.toString();
					System.out.println("Street updated (id=" + tmp.getId() + ")");
				} else {
					response = "Could not find the spot id=" + street.getId();
					System.out.println(response);
				}
			} else {
				SimpleStreetInterface tmp = dao.persist(street);
				System.out.println("Street created (id=" + street.getId() + ")");
				response = tmp.toString();
			}
		} else {
			System.out.println("Unauthorized message");
			response = "Unauthorized message";
		}

		return Response.status(200).entity(response).build();
	}

}
