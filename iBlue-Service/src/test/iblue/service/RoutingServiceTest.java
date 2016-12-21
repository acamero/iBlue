package test.iblue.service;

import static org.junit.Assert.*;


import javax.ws.rs.core.Response;

import org.junit.Test;

import com.iblue.service.RoutingService;

public class RoutingServiceTest {

	@Test
	public void test() {
		float latitude1 = 36.719678f;
		float longitude1 = -4.479118f;
		float latitude2 = 36.627789f;
		float longitude2 = -4.494637f;

		RoutingService serv = new RoutingService();

		Response resp = serv.getDijkstra(latitude1, longitude1, latitude2, longitude2);

		String body = (String)resp.getEntity();

		System.out.println(body);
		assertEquals(200, resp.getStatus());
	}
}
