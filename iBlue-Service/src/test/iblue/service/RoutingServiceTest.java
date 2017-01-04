package test.iblue.service;

import static org.junit.Assert.*;


import javax.ws.rs.core.Response;

import org.junit.BeforeClass;
import org.junit.Test;

import com.iblue.service.RoutingService;
import com.iblue.utils.Log;
import com.iblue.utils.Log.LogLevel;

public class RoutingServiceTest {
	
	@BeforeClass
	public static void init() {
		Log.setLogLevel(LogLevel.DEBUG);
	}

	@Test
	public void simple() {
		
		float latitude1 = 36.719678f;
		float longitude1 = -4.479118f;
		float latitude2 = 36.627789f;
		float longitude2 = -4.494637f;

		RoutingService serv = new RoutingService();

		Response resp = serv.getRoute(latitude1, longitude1, latitude2, longitude2);

		String body = (String)resp.getEntity();

		System.out.println(body);
		assertEquals(200, resp.getStatus());
	}
	
	
}
