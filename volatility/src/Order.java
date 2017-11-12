import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Order {

    private final static String LS = System.getProperty("line.separator");

    private static File targetIn = new File(System.getProperty("user.dir") + "/src/txt/in");
    private static String currency;
    private static Double amount;
    private static Double bid;
    private static Double ask;
    private static Double jpyRate;

    private static void calc(File file) throws Exception {
        double start = 0.0;
        // high low
        Double pips = Volatility.getHighLow(read(file, 65), currency);
        BigDecimal bPips = new BigDecimal(pips.toString());
        pips = bPips.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();

        BigDecimal bOpen = new BigDecimal((bid + ask) / 2.0);
        double open = bOpen.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
        
        Double lot = amount * 3.0;
        lot *= (1.0 / pips);
        lot *= (100.0 / jpyRate);
        lot /= 10.0;
        lot *= 1000.0;
        if (!currency.contains("JPY")) {
        	pips = pips / 100.0;
        	bPips = new BigDecimal(pips.toString());
        	pips = bPips.setScale(4, BigDecimal.ROUND_HALF_UP).doubleValue();
        	open = bOpen.setScale(4, BigDecimal.ROUND_HALF_UP).doubleValue();
        }

        StringBuilder sb = new StringBuilder();
        sb.append(currency + " : " + open + LS);
        sb.append("RATE   : " + jpyRate + LS);
        sb.append("pips   : " + pips + LS + LS + LS);

        start = open + pips;
        sb.append("BUY  : " + lot.intValue() + LS);
        sb.append("    start : " + format(start) + LS);
        sb.append("    limit : " + format(start + pips) + LS);
        sb.append("    stop  : " + format(start - (pips / 2.0)) + LS + LS);

        start = open - pips;
        sb.append("SELL : " + lot.intValue() + LS);
        sb.append("    start : " + format(start) + LS);
        sb.append("    limit : " + format(start - pips) + LS);
        sb.append("    stop  : " + format(start + (pips / 2.0)) + LS + LS + LS + LS + LS);

        File userHome = new File(System.getProperty("user.home"), "order.txt");
        FileWriter fw = new FileWriter(userHome, true);
        fw.write(sb.toString());
        fw.close();
    }

    private static String format(double value) {
        DecimalFormat df = new DecimalFormat("#0.00");
        if (!currency.contains("JPY")) {
            df = new DecimalFormat("#0.0000");
        }
        String format = df.format(value);

        return format;
    }

    private static List<Double[]> read(File file, int times) throws Exception {
        // results
        List<Double[]> results = new ArrayList<Double[]>();

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

            results.add(prices);
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
            if (file.isFile() && file.getName().contains(currency)) {
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
            targetIn = new File(System.getProperty("user.dir") + "/txt/in");
        }

        try {
            amount = new Double(args[0]);
            currency = args[1].toUpperCase();
            bid = new Double(args[2]);
            ask = new Double(args[3]);
            if (args.length > 4) {
                jpyRate = new Double(args[4]);
            } else {
                jpyRate = new Double("100");
            }
        } catch (Exception e) {
            System.out.println("java Order 352.8 USDJPY 110.001 110.011 100");
            System.exit(0);
        }

        exe();
    }
}
