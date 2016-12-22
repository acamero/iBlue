package test.iblue.model.db;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Test;

import com.iblue.model.db.GeoStreet;
import com.iblue.model.db.GeoStreetWeight;
import com.iblue.model.db.dao.GeoStreetDAO;
import com.iblue.utils.Pair;


public class GeoStreetTest {

	@Test
	public void streetWeight() {
		GeoStreetDAO dao = new GeoStreetDAO();
		GeoStreet street = dao.getStreet(9);
		System.out.println("Street id: " + street.getId());
		
		if (street != null) {
			for (GeoStreetWeight w : street.getWeights()) {
				System.out.println("Weight: " + w.getWeight());				
			}
		} 		
	}
	
	@Test
	public void test() {
		Pair<Long,Long> tileId = new Pair<Long,Long>(3650l,-475l);
		GeoStreetDAO dao = new GeoStreetDAO();
		List<GeoStreet> street = dao.getTileBounded(tileId);
		assertEquals(16,street.size());				
	}

}
