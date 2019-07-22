import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

public class Util {

	public static final String LS = System.getProperty("line.separator");

	public static final BigDecimal B1 = new BigDecimal("1");
	public static final BigDecimal B3 = new BigDecimal("3");
	public static final BigDecimal B100 = new BigDecimal("100");

	private static final double DUPLI = Double.parseDouble("0.00000001");
	private static final String PATTERN = "^.[0-9]{4}/[0-9]{2}/[0-9]{2}.$";

	public static BigDecimal halfUp(String currency, BigDecimal value) {
		BigDecimal result = new BigDecimal(value.toString());

		if ("JPY".equals(currency.substring(3))) {
			result = result.setScale(1, BigDecimal.ROUND_HALF_UP);
		} else {
			result = result.setScale(3, BigDecimal.ROUND_HALF_UP);
		}

		return result;
	}

	public static BigDecimal halfUp(BigDecimal value, int scale) {
		BigDecimal result = new BigDecimal(value.toString());

		return result.setScale(scale, BigDecimal.ROUND_HALF_UP);
	}

	public static BigDecimal getMedian(String currency, File folder) throws Exception {
		File[] files = folder.listFiles();
		File target = null;
		for (File file : files) {
			if (file.getName().contains(currency)) {
				target = file;
			}
		}

		List<Candle> candleList = read(target);
		Double result = getMedian(candleList, currency);

		return new BigDecimal(result.toString());
	}

	private static double getMedian(List<Candle> candleList, String currency) {
		TreeMap<Double, Candle> sort = new TreeMap<Double, Candle>();

		for (Candle candle : candleList) {
			Double highLow = Math.abs(candle.high.doubleValue() - candle.low.doubleValue());
			if (!currency.contains("JPY")) {
				highLow = highLow * B100.doubleValue();
			}
			if (sort.get(highLow) == null) {
				sort.put(highLow.doubleValue(), candle);
			} else {
				for (double i = DUPLI;; i += DUPLI) {
					if (sort.get(highLow + i) == null) {
						sort.put(highLow + i, candle);
						break;
					}
				}
			}
		}
		List<Double> list = new ArrayList<Double>(sort.keySet());
		double result = (list.get(25) + list.get(26)) / Double.parseDouble("2");

		return result;
	}

	private static List<Candle> read(File file) throws Exception {
		// results
		List<Candle> results = new ArrayList<Candle>();

		FileReader fr = new FileReader(file);
		BufferedReader br = new BufferedReader(fr);

		while (true) {
			String line = br.readLine();
			if (line == null) {
				break;
			}
			String[] cols = line.split(",");
			if (cols.length < 2) {
				continue;
			}
			if (!cols[1].matches(PATTERN)) {
				continue;
			}

			Candle candle = new Candle();
			candle.open = new BigDecimal(cols[2]);
			candle.high = new BigDecimal(cols[3]);
			candle.low = new BigDecimal(cols[4]);
			candle.close = new BigDecimal(cols[5]);

			results.add(candle);
			if (results.size() == 52) {
				break;
			}
		}
		br.close();

		return results;
	}
}
