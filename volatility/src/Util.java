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
    public static final BigDecimal B2 = new BigDecimal("2");
    public static final BigDecimal B3 = new BigDecimal("3");
    public static final BigDecimal B100 = new BigDecimal("100");

    private static final int LIST_SIZE = 52;
    private static final int MEDIAN = 25;
    private static final BigDecimal DUPLI = new BigDecimal("0.0000000001");

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

    public static BigDecimal getMedian(String currency, File folder, boolean highLowFlag) throws Exception {
        File[] files = folder.listFiles();
        File target = null;
        for (File file : files) {
            if (file.getName().contains(currency)) {
                target = file;
            }
        }

        List<Candle> candleList = read(target);
        BigDecimal result = getMedian(candleList, currency, highLowFlag);

        return result;
    }

    private static BigDecimal getMedian(List<Candle> candleList, String currency, boolean highLowFlag) {
        TreeMap<BigDecimal, Candle> sort = new TreeMap<>();

        for (Candle candle : candleList) {
            BigDecimal abs = candle.high.subtract(candle.low).abs();
            if (!highLowFlag) {
                abs = candle.open.subtract(candle.high).abs();
                BigDecimal openLow = candle.open.subtract(candle.low).abs();
                if (openLow.compareTo(abs) > 0) {
                    abs = openLow;
                }
            }
            if (!currency.contains("JPY")) {
                abs = abs.multiply(B100);
            }
            if (sort.get(abs) == null) {
                sort.put(abs, candle);
            } else {
                while (true) {
                    abs = abs.add(DUPLI);
                    if (sort.get(abs) == null) {
                        sort.put(abs, candle);
                        break;
                    }
                }
            }
        }
        List<BigDecimal> list = new ArrayList<>(sort.keySet());
        BigDecimal result = list.get(MEDIAN).add(list.get(MEDIAN + 1)).divide(B2);

        return result;
    }

    private static List<Candle> read(File file) throws Exception {
        // results
        List<Candle> results = new ArrayList<>();

        FileReader fr = new FileReader(file);
        BufferedReader br = new BufferedReader(fr);

        for (int i = 0;; i++) {
            String line = br.readLine();
            if (line == null) {
                break;
            }
            if (i < 1) {
                continue;
            }
            String[] cols = line.split(",");

            Candle candle = new Candle();
            candle.open = new BigDecimal(cols[2]);
            candle.high = new BigDecimal(cols[3]);
            candle.low = new BigDecimal(cols[4]);
            candle.close = new BigDecimal(cols[5]);

            results.add(candle);
            if (results.size() == LIST_SIZE) {
                break;
            }
        }
        br.close();

        return results;
    }
}
