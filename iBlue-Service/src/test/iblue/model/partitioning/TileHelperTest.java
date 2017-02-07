package test.iblue.model.partitioning;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;

import com.iblue.model.partitioning.TileHelper;
import com.iblue.utils.Log;
import com.iblue.utils.Log.LogLevel;

public class TileHelperTest {
	
	@BeforeClass
	public static void before() {
		Log.setLogLevel(LogLevel.DEBUG);
	}

	@Test
	public void load() {		
		TileHelper.getInstance();
	}
	
	@Test
	public void change() {		
		List<BigDecimal> latRanges = new ArrayList<BigDecimal>();
		List<BigDecimal> lonRanges = new ArrayList<BigDecimal>();
		TileHelper.updateConfiguration(latRanges, lonRanges);
	}
}
