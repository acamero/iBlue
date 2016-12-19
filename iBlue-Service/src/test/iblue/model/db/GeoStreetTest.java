package test.iblue.model.db;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Test;

import com.iblue.model.Pair;
import com.iblue.model.db.GeoStreet;
import com.iblue.model.db.GeoStreetWeight;
import com.iblue.model.db.dao.StreetDAO;


public class GeoStreetTest {

	@Test
	public void streetWeight() {
		StreetDAO dao = new StreetDAO();
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
		StreetDAO dao = new StreetDAO();
		List<GeoStreet> street = dao.getTileBounded(tileId);
		assertEquals(16,street.size());				
	}

}
