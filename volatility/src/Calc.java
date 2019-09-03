import java.io.File;
import java.io.FileWriter;
import java.math.BigDecimal;
import java.util.ResourceBundle;

public class Calc {

    private static File targetIn = new File(System.getProperty("user.dir") + "/all");
    private static ResourceBundle target = ResourceBundle.getBundle("target");
    private static ResourceBundle rate = ResourceBundle.getBundle("rate");
    private static ResourceBundle open = ResourceBundle.getBundle("open");
    private static StringBuilder sb = new StringBuilder();
    private static String currency = "";

    private static void exe() throws Exception {
        // currency
        currency = target.getString("currency").toUpperCase();

        // jpy rate
        BigDecimal jpyRate = new BigDecimal(rate.getString(currency.substring(3)));

        // open price
        BigDecimal openPrice = new BigDecimal(open.getString(currency));
        openPrice = Util.halfUp(currency, openPrice);

        // total amount
        BigDecimal amount = new BigDecimal(target.getString("amount"));

        // median
        BigDecimal median = Util.getMedian(currency, targetIn);
        BigDecimal medianStart = Util.halfUp(median.divide(Util.B3, 10, BigDecimal.ROUND_HALF_UP), 1);
        BigDecimal medianLimit = Util.halfUp(median, 1);
        if (!"JPY".equals(currency.substring(3))) {
            medianStart = medianStart.divide(Util.B100);
            medianLimit = medianLimit.divide(Util.B100);
        }

        // Lots
        BigDecimal work1 = Util.B100.divide(jpyRate, 10, BigDecimal.ROUND_HALF_UP);
        BigDecimal work2 = Util.B1.divide(median, 10, BigDecimal.ROUND_HALF_UP);
        BigDecimal lots = amount.multiply(work1);
        lots = lots.multiply(work2).multiply(Util.B100);
        println(currency + " : " + lots.intValue());
        println("median : " + medianLimit + Util.LS + Util.LS);

        // buy
        BigDecimal startBuy = openPrice.subtract(medianStart);
        BigDecimal limitBuy = startBuy.add(medianLimit);
        BigDecimal stopBuy = startBuy.subtract(medianLimit);
        println("Buy  : " + startBuy);
        println("       " + limitBuy);
        println("       " + stopBuy + Util.LS + Util.LS);

        // sell
        BigDecimal startSell = openPrice.add(medianStart);
        BigDecimal limitSell = startSell.subtract(medianLimit);
        BigDecimal stopSell = startSell.add(medianLimit);
        println("Sell : " + startSell);
        println("       " + limitSell);
        println("       " + stopSell);
    }

    private static void println(String val) {
        sb.append(val + Util.LS);
    }

    public static void main(String[] args) throws Exception {
        //System.out.println("user.dir : " + System.getProperty("user.dir"));
        // execute
        if (!targetIn.exists()) {
            targetIn = new File(System.getProperty("user.dir") + "/src/all");
        }
        exe();

        File output = new File(System.getProperty("user.home"), currency + ".txt");
        FileWriter fw = new FileWriter(output);
        fw.write(sb.toString());
        fw.close();
    }
}
