import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Volatility {

    private final static String LS = System.getProperty("line.separator");
    private final static double D100 = Double.parseDouble("100");

    private static File targetIn = new File(System.getProperty("user.dir") + "/src/txt");
    private static StringBuilder sb = new StringBuilder();

    private static void calc(File file) throws Exception {
        // sd
        String currency = extract(file.getName(), "[A-Z]{6}");
        sb.append(currency + ",");

        // high low
        List<Double[]> results = read(file, 0, 65);
        double hl = getHighLow(results, currency);
        sb.append(hl + LS);
    }

    public static double getHighLow(List<Double[]> array, String currency) {
        TreeMap<Double, Double> sort = new TreeMap<Double, Double>();

        for (Double[] candle : array) {
            Double highLow = Math.abs(candle[1] - candle[2]);
            if (!currency.contains("JPY")) {
                highLow = highLow * D100;
            }
            if (sort.get(highLow) == null) {
                sort.put(highLow, highLow);
            } else {
                for (Double i = 0.00001;; i += 0.00001) {
                    if (sort.get(highLow + i) == null) {
                        sort.put(highLow + i, highLow + i);
                        break;
                    }
                }
            }
        }
        List<Double> list = new ArrayList<Double>(sort.keySet());
        System.out.println(MessageFormat.format("currency : {0}, size : {1}", currency, list.size()));

        return list.get(61);
    }

    private static List<Double[]> read(File file, int start, int times) throws Exception {
        // results
        List<Double[]> results = new ArrayList<Double[]>();
        int count = 0;

        FileReader fr = new FileReader(file);
        BufferedReader br = new BufferedReader(fr);

        while (true) {
            String line = br.readLine();
            if (line == null) {
                break;
            }
            String[] cols = line.split(",");
            String date = extract(line, "[0-9]{4}/[0-9]{1,2}/[0-9]{1,2}");

            if (cols.length < 5 || date == null) {
                continue;
            }
            Double open = new Double(cols[2]);
            Double high = new Double(cols[3]);
            Double low = new Double(cols[4]);
            Double close = new Double(cols[5]);
            Double[] prices = { open, high, low, close };

            if (count >= start) {
                results.add(prices);
            }
            count++;
            if (results.size() == times) {
                break;
            }
        }
        br.close();

        return results;
    }

    private static void exe() throws Exception {
        // read csv files
        File[] folders = targetIn.listFiles();
        for (File file : folders) {
            if (file.isFile() && file.getName().endsWith(".csv")) {
                calc(file);
            }
        }
    }

    private static String extract(String target, String regex) {
        String result = null;
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(target);
        if (m.find()) {
            result = m.group();
        }

        return result;
    }

    public static void main(String[] args) throws Exception {
        // execute
        if (!targetIn.exists()) {
            targetIn = new File(System.getProperty("user.dir") + "/txt");
        }
        exe();
        FileWriter fw = new FileWriter(new File(System.getProperty("user.home"), "result.csv"));
        fw.write(sb.toString());
        fw.close();
    }
}
