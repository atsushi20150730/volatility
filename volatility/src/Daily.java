import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import basic.CalcPriceText;
import basic.PriceBean;

public class Daily {

    private static Map<String, PriceBean> priceMap = new TreeMap<String, PriceBean>();
    private static Integer amount = 388;
    // private static String currency = "usd".toUpperCase() + "JPY";
    private static File targetIn = new File(System.getProperty("user.dir") + "/src/txt/in");

    private static void calc(File file) throws Exception {
        // reader
        String currency = extract(file.getName(), "[A-Z]{6}");
        TreeMap<Timestamp, Double[]> map = read(file);
        PriceBean bean = new PriceBean();
        priceMap.put(currency, bean);

        bean.highLow = getHighLow(map, 65, 0);
    }

    private static double getHighLow(Map<Timestamp, Double[]> map, int n, int start) {
        Map<Double, Double> sortMap = new TreeMap<Double, Double>();
        List<Double> sortList;

        List<Timestamp> list = new ArrayList<Timestamp>(map.keySet());
        int count = 0;
        int startIdx = start + 1;
        for (int i = list.size() - startIdx; i >= 0 && count < n; i--) {
            Double[] candle = map.get(list.get(i));
            Double keyValue = Math.abs(candle[1] - candle[2]);
            if (sortMap.get(keyValue) == null) {
                sortMap.put(keyValue, keyValue);
            } else {
                // System.err.println(keyValue);
                while (true) {
                    keyValue += 0.0000001;
                    if (sortMap.get(keyValue) == null) {
                        sortMap.put(keyValue, keyValue);
                        break;
                    }
                }
            }

            count++;
        }
        sortList = new ArrayList<Double>(sortMap.keySet());

        return sortList.get(32);
    }

    private static TreeMap<Timestamp, Double[]> read(File file) throws Exception {
        // results
        TreeMap<Timestamp, Double[]> map = new TreeMap<Timestamp, Double[]>();

        FileReader fr = new FileReader(file);
        BufferedReader br = new BufferedReader(fr);

        for (;;) {
            String line = br.readLine();
            if (line == null) {
                break;
            }
            String[] cols = line.split(",");
            String date = extract(line, "[0-9]{4}/[0-9]{1,2}/[0-9]{1,2}");

            if (cols.length < 6 || date == null) {
                continue;
            }
            Double open = new Double(cols[2]);
            Double high = new Double(cols[3]);
            Double low = new Double(cols[4]);
            Double close = new Double(cols[5]);
            Double[] prices = { open, high, low, close };

            // timestamp
            Timestamp key = getTime(date);

            map.put(key, prices);
        }
        br.close();

        return map;
    }

    private static void exe() throws Exception {
        // read csv files
        File[] folders = targetIn.listFiles();
        for (File file : folders) {
            if (file.isFile() && file.getName().contains("JPY")) {
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

    private static Timestamp getTime(String time) {
        if (time == null || (!time.matches("^[0-9]{4}/[0-9]{1,2}/[0-9]{1,2}$")
                && !time.matches("^[0-9]{1,2}/[0-9]{1,2}/[0-9]{4} .+$"))) {
            return null;
        }
        String[] cols = time.split("[^0-9]");

        int year = Integer.parseInt(cols[0]);
        int month = Integer.parseInt(cols[1]) - 1;
        int date = Integer.parseInt(cols[2]);
        if (time.matches("^[0-9]{1,2}/[0-9]{1,2}/[0-9]{4} .+$")) {
            month = Integer.parseInt(cols[0]) - 1;
            date = Integer.parseInt(cols[1]);
            year = Integer.parseInt(cols[2]);
        }

        Calendar cal = new GregorianCalendar(year, month, date);

        return new Timestamp(cal.getTimeInMillis());
    }

    public static void main(String[] args) throws Exception {
        // execute
        if (args != null && args.length > 0) {
            amount = Integer.parseInt(args[0]);
            targetIn = new File(System.getProperty("user.dir") + "/txt/in");
        }
        exe();

        new CalcPriceText(priceMap, amount);
    }
}
