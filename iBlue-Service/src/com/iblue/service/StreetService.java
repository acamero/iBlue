package com.iblue.service;

import java.util.List;

import javax.validation.Valid;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.json.JSONArray;
import org.json.JSONException;

import com.iblue.model.StreetDAOInterface;
import com.iblue.service.data.StreetJSON;
import com.iblue.model.StreetInterface;
import com.iblue.model.db.StreetDAO;

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
		
		StreetDAOInterface dao = new StreetDAO();
		int counter = 0;

		for (StreetInterface street : streets) {
			street = dao.persist(street);
			if(street!=null) {
				counter++;
			}
		}
		
		return Response.status(200).entity(streets.size() +" streets received, " + counter + " streets uploaded").build();
	}
	
	@GET
	@Path("/active")
	@Produces("application/json")
	public Response getActiveStreets() throws JSONException {
		System.out.println("Get all active streets");
		
		JSONArray jsonArray = new JSONArray();
		StreetDAOInterface dao = new StreetDAO();
		List<? extends StreetInterface> streets = dao.findAllActive();
		
		for (StreetInterface st : streets) {
			// System.out.println(st.toString());
			jsonArray.put(st.toString());
		}
		
		return Response.status(200).entity(jsonArray.toString()).build();
	}
	
	@POST
	@Path("/set")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response setStreet(StreetJSON street) {
		System.out.println("Set street");
		if(street==null) {
			return Response.status(200).entity("Null street").build();
		}
		
		String response = "";
		StreetDAOInterface dao = new StreetDAO();

		if (street.getId() > 0) {
			StreetInterface tmp = dao.update(street);
			if(tmp!=null) {
				response = tmp.toString();
				System.out.println("Street updated (id=" + tmp.getId() + ")");
			} else {
				response = "Could not find the spot id=" + street.getId();
				System.out.println(response);				
			}
//			StreetInterface tmp = dao.getStreet(street.getId());
//			if (tmp != null) {
//				tmp.setStatus(street.getStatus());
//				dao.persist(tmp);
//				response = tmp.toString();
//				System.out.println("Street updated (id=" + tmp.getId() + ")");
//			} else {
//				System.out.println("Could not find the spot id=" + street.getId());
//				response = "ERROR";
//			}
			
		} else {			
			StreetInterface tmp = dao.persist(street);
			System.out.println("Street created (id=" + street.getId() + ")");
			response = tmp.toString();
		}

		return Response.status(200).entity(response).build();
	}

}
