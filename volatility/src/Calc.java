import java.io.File;
import java.math.BigDecimal;
import java.util.ResourceBundle;

public class Calc {

    private static File targetIn = new File(System.getProperty("user.dir") + "/all");
    private static ResourceBundle target = ResourceBundle.getBundle("target");
    private static ResourceBundle rate = ResourceBundle.getBundle("rate");
    private static String currency = "";

    private static void exe() throws Exception {
        // currency
        currency = target.getString("currency").toUpperCase();

        // jpy rate
        BigDecimal jpyRate = new BigDecimal(rate.getString(currency.substring(3)));

        // open price
        BigDecimal openPrice = new BigDecimal(target.getString("price"));

        // total amount
        BigDecimal amount = new BigDecimal(target.getString("amount"));

        // median
        BigDecimal median = Util.getMedian(currency, targetIn, true);
        median = Util.halfUp(median, 1);
        BigDecimal limitStop = new BigDecimal(median.toString());
        if (!"JPY".equals(currency.substring(3))) {
            limitStop = limitStop.divide(Util.B100);
        }

        // Lots
        BigDecimal work1 = Util.B100.divide(jpyRate, 10, BigDecimal.ROUND_HALF_UP);
        BigDecimal work2 = Util.B1.divide(median, 10, BigDecimal.ROUND_HALF_UP);
        BigDecimal work3 = work1.multiply(work2);
        BigDecimal lots = amount.multiply(work3);
        lots = lots.multiply(Util.B100);
        System.out.println(currency + "     : " + lots.intValue());
        System.out.println("jpy rate   : " + jpyRate);
        System.out.println("amount     : " + amount);
        System.out.println("median     : " + median);
        System.out.println("open price : " + openPrice + Util.LS + Util.LS);

        // buy
        BigDecimal limitBuy = openPrice.add(limitStop);
        BigDecimal stopBuy = openPrice.subtract(limitStop);
        System.out.println("Buy  : " + limitBuy);
        System.out.println("       " + stopBuy + Util.LS + Util.LS);

        // sell
        BigDecimal limitSell = openPrice.subtract(limitStop);
        BigDecimal stopSell = openPrice.add(limitStop);
        System.out.println("Sell : " + limitSell);
        System.out.println("       " + stopSell);
    }

    public static void main(String[] args) throws Exception {
        //System.out.println("user.dir : " + System.getProperty("user.dir"));
        // execute
        if (!targetIn.exists()) {
            targetIn = new File(System.getProperty("user.dir") + "/src/all");
        }
        exe();
    }
}
