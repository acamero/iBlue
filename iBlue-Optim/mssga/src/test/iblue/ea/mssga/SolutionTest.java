package test.iblue.ea.mssga;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.iblue.ea.RoutesGenerator;
import com.iblue.ea.mssga.Individual;
import com.iblue.ea.mssga.Problem;
import com.iblue.ea.mssga.Chromosome.ChromosomeException;
import com.iblue.model.GeoStreetInterface;
import com.iblue.model.db.service.TileService;
import com.iblue.utils.Log;
import com.iblue.utils.Pair;
import com.iblue.utils.Log.LogLevel;

public class SolutionTest {

	private static List<Pair<GeoStreetInterface, GeoStreetInterface>> originDestinations;
	private static BufferedWriter routeBw;
	private static BufferedWriter tileComputingBw;
	private static BufferedWriter fitnessBw;
	private static BufferedWriter statsBw;
	private static final String OUTPUT_FILE_RANGE = "tile-comp-results.csv";
	private static final String OUTPUT_FILE_ROUTE = "routing-results.csv";
	private static final String OUTPUT_FILE_FITNESS = "fitness-results.csv";
	private static final String STATS_FILE = "population-stats.csv";
	private static final String CACHE_FILE = "cache.txt";
	// private static ScaleInterface mapScale;
	private static Map<String, Double> cache;

	@BeforeClass
	public static void before() throws IOException {
		Log.setLogLevel(LogLevel.INFO);
		RoutesGenerator rg = new RoutesGenerator();
		originDestinations = rg.getRoutes("spots-malaga.txt");

		try {
			File file = new File(OUTPUT_FILE_RANGE);
			if (!file.exists()) {
				file.createNewFile();
			}
			FileWriter fw = new FileWriter(file, false);
			tileComputingBw = new BufferedWriter(fw);
			tileComputingBw.write("latN;lonN;latG;lonG;buildtime\n");
			tileComputingBw.flush();

			file = new File(OUTPUT_FILE_ROUTE);
			if (!file.exists()) {
				file.createNewFile();
			}
			fw = new FileWriter(file, false);
			routeBw = new BufferedWriter(fw);
			routeBw.write("latN;lonN;latG;lonG;route;totaltime;searchtime;fitnesstime;found\n");
			routeBw.flush();

			file = new File(OUTPUT_FILE_FITNESS);
			if (!file.exists()) {
				file.createNewFile();
			}
			fw = new FileWriter(file, false);
			fitnessBw = new BufferedWriter(fw);
			fitnessBw.write("latN;lonN;latG;lonG;fitness\n");
			fitnessBw.flush();

			file = new File(STATS_FILE);
			if (!file.exists()) {
				file.createNewFile();
			}
			fw = new FileWriter(file, false);
			statsBw = new BufferedWriter(fw);
			statsBw.write("bestFitness;bestFitnessEver;medianFitness;meanFitness;stDev\n");
			statsBw.flush();

		} catch (IOException e) {
			e.printStackTrace();
		}

		TileService serv = new TileService();
		Pair<Pair<BigDecimal, BigDecimal>, Pair<BigDecimal, BigDecimal>> bounds = serv.getMapBoundaries();
		BigDecimal latR = bounds.getFirst().getFirst().subtract(bounds.getFirst().getSecond()).abs();
		BigDecimal lonR = bounds.getSecond().getFirst().subtract(bounds.getSecond().getSecond()).abs();
		Log.debug("Map Size latitude=" + latR + " longitude=" + lonR);
		// mapScale = new MapScale(latR, lonR);

		cache = new HashMap<String, Double>();
		try {
			File file = new File(CACHE_FILE);
			if (file.exists()) {
				FileReader fr = new FileReader(file);
				BufferedReader br = new BufferedReader(fr);
				String line;
				// skip first line
				br.readLine();
				while ((line = br.readLine()) != null) {
					Log.debug(line);
					String[] prts = line.split("\\|");
					Double fitness = new Double(prts[1]);
					cache.put(prts[0], fitness);
					Log.debug("Added to cache key=" + prts[0] + " value=" + fitness);
				}
				br.close();
			}

		} catch (IOException e) {
			Log.error("Something happen with file " + OUTPUT_FILE_RANGE);
			e.printStackTrace();
		}
		Log.debug("Cache loaded (" + cache.size() + " entries)");
	}

	@AfterClass
	public static void after() {
		try {
			File file = new File(CACHE_FILE);
			if (!file.exists()) {
				file.createNewFile();
			}
			FileWriter fw = new FileWriter(file, false);
			BufferedWriter cw = new BufferedWriter(fw);
			cw.write("solution|fitness\n");
			for (Entry<String, Double> e : cache.entrySet()) {
				cw.write(e.getKey() + "|" + e.getValue() + "\n");
			}
			cw.flush();
			cw.close();
			Log.debug("Cache writted");
		} catch (IOException e) {
			e.printStackTrace();
		}

		try {
			routeBw.close();
			tileComputingBw.close();
			fitnessBw.close();
			statsBw.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	public void solution() throws ChromosomeException {
		List<String> solutions = new ArrayList<String>();
		solutions.add(
				"5;8;[0.1087471, 0.08661536, 0.03298193, 0.02929223, 0.1341813];[0.05097635, 0.1140289, 0.1631037, 0.1971095, 0.1441857, 0.002121311, 0.2603756, 0.01839940];200");
		solutions.add(
				"8;7;[0.1250971, 0.1104810, 0.02366767, 0.02821063, 0.03128412, 0.003385958, 0.003754854, 0.06593647];[0.05056601, 0.1970449, 0.2695221, 0.07351063, 0.05189130, 0.1573221, 0.1504431];196");
		solutions.add(
				"9;9;[0.007373326, 0.006702973, 0.003414349, 0.01221098, 0.1665975, 0.05966981, 0.004638442, 0.008522537, 0.1226878];[0.3021306, 0.07626197, 0.02612505, 0.1270462, 0.09828779, 0.1066299, 0.03924768, 0.06589493, 0.1086767];195");
		solutions.add(
				"8;5;[0.03231060, 0.01792125, 0.07298367, 0.07134696, 0.06051759, 0.05157211, 0.009613285, 0.07555231];[0.1839413, 0.3751388, 0.1373054, 0.07969130, 0.1742236];206");
		solutions.add(
				"8;9;[0.1005767, 0.03012942, 0.07786210, 0.04811544, 0.02352882, 0.05967663, 0.02460313, 0.02732556];[0.1722069, 0.1456143, 0.1109902, 0.1286699, 0.09663155, 0.1257318, 0.1343247, 0.02649442, 0.009636599];207");
		solutions.add(
				"7;7;[0.05184139, 0.005199026, 0.05592025, 0.01938399, 0.09913154, 0.03342232, 0.1269193];[0.08334861, 0.1125863, 0.1718642, 0.1397370, 0.1858178, 0.1586576, 0.09828883];219");
		solutions.add(
				"6;8;[0.07138102, 0.08842903, 0.06086902, 0.03527851, 0.1030846, 0.03277563];[0.01208357, 0.1658299, 0.1158992, 0.09434656, 0.1429565, 0.06778106, 0.1889086, 0.1624948];200");
		solutions.add(
				"9;6;[0.02052312, 0.05445789, 0.03873917, 0.07974175, 0.05246051, 0.01575686, 0.07898966, 0.002046797, 0.04910216];[0.2008840, 0.04323020, 0.1157717, 0.2618048, 0.05467626, 0.2739334];201");
		solutions.add(
				"8;8;[0.08520065, 0.002241104, 0.1494248, 0.01158230, 0.002261779, 0.001650432, 0.007050251, 0.1324065];[0.09079214, 0.06897818, 0.1032896, 0.2485300, 0.1653727, 0.05356429, 0.1095464, 0.1102271];201");
		solutions.add(
				"8;7;[0.02529080, 0.06532909, 0.08430152, 0.07980562, 0.001102968, 0.06073102, 0.05049116, 0.02476570];[0.2004464, 0.08377279, 0.09192240, 0.1696302, 0.1754317, 0.1904961, 0.03860082];201");
		solutions.add(
				"9;9;[0.02546804, 0.04642365, 0.03359626, 0.05915717, 0.05089360, 0.04104876, 0.05852026, 0.05490367, 0.02180644];[0.09585681, 0.03140977, 0.1568998, 0.04933923, 0.1642923, 0.1090568, 0.1605441, 0.1542420, 0.02865956];198");
		solutions.add(
				"8;9;[0.01493955, 0.004978956, 0.09453823, 0.06012237, 0.07948754, 0.005591256, 0.07185976, 0.06030003];[0.05775014, 0.04304770, 0.2028939, 0.1273523, 0.1105654, 0.03887009, 0.2041740, 0.1642015, 0.001445262];225");
		solutions.add(
				"9;8;[0.03694028, 0.01053905, 0.02091339, 0.1467363, 0.02024038, 0.02269769, 0.003321799, 0.1153110, 0.01511794];[0.07193868, 0.1842545, 0.1326063, 0.1343483, 0.03593089, 0.1120064, 0.1134778, 0.1657377];191");
		solutions.add(
				"6;9;[0.04341655, 0.1827924, 0.03075076, 0.07654581, 0.04031271, 0.01799959];[0.07653066, 0.1208592, 0.05246901, 0.1658503, 0.1041650, 0.08518139, 0.1913036, 0.07549645, 0.07844470];197");
		solutions.add(
				"9;9;[0.001830828, 0.07061733, 0.09002126, 0.05252778, 0.03071054, 0.01126001, 0.002556106, 0.1076348, 0.02465927];[0.05129569, 0.04564000, 0.1334224, 0.03517181, 0.2534813, 0.08772909, 0.02246699, 0.3028217, 0.01827153];192");
		solutions.add(
				"9;9;[0.03134756, 0.06386227, 0.1307029, 0.03462365, 0.05103100, 0.009046513, 0.01046398, 0.04550905, 0.01523090];[0.1293941, 0.09111837, 0.08287468, 0.1159350, 0.1272138, 0.1197235, 0.06099871, 0.1557730, 0.06726905];195");
		solutions.add(
				"5;7;[0.1225701, 0.1046987, 0.02830063, 0.03231938, 0.1039290];[0.1493057, 0.2202918, 0.1776018, 0.08683915, 0.1464185, 0.08142917, 0.08841397];196");
		solutions.add(
				"8;8;[0.06940180, 0.03566663, 0.06004597, 0.07015340, 0.02270404, 0.05167841, 0.05396475, 0.02820285];[0.1116783, 0.1147299, 0.1177042, 0.1920937, 0.1393568, 0.2176941, 0.02520498, 0.03183826];192");
		solutions.add(
				"7;8;[0.07750376, 0.02549146, 0.01654522, 0.1052837, 0.03178061, 0.05565654, 0.07955655];[0.09033617, 0.03394344, 0.2233207, 0.1699964, 0.1063765, 0.02237677, 0.005076097, 0.2988741];184");
		solutions.add(
				"9;9;[0.05801632, 0.05777038, 0.07962458, 0.05161037, 0.009548114, 0.007774325, 0.08987870, 0.03028011, 0.007314958];[0.07422938, 0.2202919, 0.1907229, 0.05108852, 0.003256809, 0.02387094, 0.02099859, 0.1275723, 0.2382690];201");
		solutions.add(
				"8;7;[0.05396092, 0.09919338, 0.05915507, 0.02479823, 0.01871507, 0.07399978, 0.05277200, 0.009223323];[0.1860086, 0.1832902, 0.2506879, 0.03893664, 0.03927072, 0.2413635, 0.01074267];200");
		solutions.add(
				"9;9;[0.02433493, 0.06870006, 0.05315750, 0.02403321, 0.05924977, 0.02554867, 0.05289105, 0.02017778, 0.06372486];[0.1470352, 0.2094769, 0.1591029, 0.04245527, 0.06698109, 0.05440936, 0.03368176, 0.1327351, 0.1044227];182");
		solutions.add(
				"5;8;[0.05502486, 0.009043310, 0.09345698, 0.09898695, 0.1353057];[0.04992113, 0.1890766, 0.09252762, 0.1686062, 0.01726407, 0.06098096, 0.1531797, 0.2187441];204");
		solutions.add(
				"6;8;[0.08592524, 0.01109391, 0.1541062, 0.005161253, 0.1105700, 0.02496129];[0.05467639, 0.03307253, 0.08664245, 0.1719710, 0.04865531, 0.2039121, 0.05600741, 0.2953631];206");
		solutions.add(
				"9;8;[0.09681813, 0.1253641, 0.02789551, 0.01169999, 0.007795784, 0.03114893, 0.01001852, 0.003732321, 0.07734449];[0.02898469, 0.07580416, 0.1009117, 0.09546302, 0.05582385, 0.2275437, 0.07802868, 0.2877406];195");
		solutions.add(
				"9;7;[0.06591313, 0.06983462, 0.03481341, 0.06318905, 0.01981373, 0.04896504, 0.06082737, 0.01281047, 0.01565099];[0.04301154, 0.05520100, 0.07173982, 0.3712895, 0.06240693, 0.02056812, 0.3260834];201");
		solutions.add(
				"6;5;[0.1000583, 0.05393119, 0.08683975, 0.01414077, 0.07934936, 0.05749846];[0.1907574, 0.1768542, 0.1652635, 0.1355666, 0.2818585];192");
		solutions.add(
				"3;9;[0.09994893, 0.1576436, 0.1342253];[0.09513457, 0.1719531, 0.1027491, 0.1125045, 0.03808203, 0.01162315, 0.1331325, 0.03674234, 0.2483791];193");
		solutions.add(
				"9;8;[0.03231214, 0.005146136, 0.01076990, 0.1992725, 0.005183763, 0.01115640, 0.007482353, 0.008372829, 0.1121218];[0.3044806, 0.1659679, 0.01126487, 0.008679230, 0.01505728, 0.1588892, 0.1272119, 0.1587493];208");
		solutions.add(
				"6;7;[0.03221380, 0.2137034, 0.01232624, 0.05630866, 0.02590437, 0.05136139];[0.1475230, 0.05005651, 0.2399475, 0.03914944, 0.1151268, 0.1482673, 0.2102296];243");

		for (String sol : solutions) {
			String[] parts = sol.split(";");
			String latGene = parts[2];
			latGene = latGene.substring(1, latGene.length() - 1);

			String lonGene = parts[3];
			lonGene = lonGene.substring(1, lonGene.length() - 1);

			String[] lats = latGene.split(",");
			String[] lons = lonGene.split(",");
			Individual individual = new Individual(lats.length, lons.length);
			for (int i = 0; i < lats.length; i++) {
				individual.getChromosome().setLatGene(i, new BigDecimal(lats[i].trim(), Problem.MATH_CONTEXT));
			}

			for (int i = 0; i < lons.length; i++) {
				individual.getChromosome().setLonGene(i, new BigDecimal(lons[i].trim(), Problem.MATH_CONTEXT));
			}
			Log.info(individual.toString());

			Problem problem = new Problem(originDestinations, routeBw, tileComputingBw, fitnessBw);
			Log.info("Fitness cache size=" + cache.size());
			problem.addFitnessCache(cache);

			problem.evaluate(individual);

			cache = problem.getFitnessCache();
		}

	}

	@Test
	public void bestSolutions() throws ChromosomeException {
		List<String> solutions = new ArrayList<String>();
		
		solutions.add(
				"9;9;[0.02433493, 0.06870006, 0.05315750, 0.02403321, 0.05924977, 0.02554867, 0.05289105, 0.02017778, 0.06372486];[0.1470352, 0.2094769, 0.1591029, 0.04245527, 0.06698109, 0.05440936, 0.03368176, 0.1327351, 0.1044227];232");
		solutions.add(
				"7;8;[0.07750376, 0.02549146, 0.01654522, 0.1052837, 0.03178061, 0.05565654, 0.07955655];[0.09033617, 0.03394344, 0.2233207, 0.1699964, 0.1063765, 0.02237677, 0.005076097, 0.2988741];241");
		solutions.add(
				"9;9;[0.001830828, 0.07061733, 0.09002126, 0.05252778, 0.03071054, 0.01126001, 0.002556106, 0.1076348, 0.02465927];[0.05129569, 0.04564000, 0.1334224, 0.03517181, 0.2534813, 0.08772909, 0.02246699, 0.3028217, 0.01827153];192");
		

		for (String sol : solutions) {
			String[] parts = sol.split(";");
			String latGene = parts[2];
			latGene = latGene.substring(1, latGene.length() - 1);

			String lonGene = parts[3];
			lonGene = lonGene.substring(1, lonGene.length() - 1);

			String[] lats = latGene.split(",");
			String[] lons = lonGene.split(",");
			Individual individual = new Individual(lats.length, lons.length);
			for (int i = 0; i < lats.length; i++) {
				individual.getChromosome().setLatGene(i, new BigDecimal(lats[i].trim(), Problem.MATH_CONTEXT));
			}

			for (int i = 0; i < lons.length; i++) {
				individual.getChromosome().setLonGene(i, new BigDecimal(lons[i].trim(), Problem.MATH_CONTEXT));
			}
			Log.info(individual.toString());

			Problem problem = new Problem(originDestinations, routeBw, tileComputingBw, fitnessBw);
			problem.setFitnessCache(false);			
			
			for (int i = 0; i < 31; i++) {
				problem.evaluate(individual);
			}

		}

	}
}
