import java.io.File;
import java.math.BigDecimal;
import java.util.ResourceBundle;

public class MedianList {

    private static final ResourceBundle TARGET = ResourceBundle.getBundle("target");
    private static final ResourceBundle RATE = ResourceBundle.getBundle("rate");
    private static final BigDecimal AMOUNT = new BigDecimal(TARGET.getString("amount"));

    private static File targetIn = new File(System.getProperty("user.dir") + "/all");


    private static void exe(String currency) throws Exception {
        // jpy rate
        BigDecimal jpyRate = new BigDecimal(RATE.getString(currency.substring(3)));

        // median
        BigDecimal median = Util.getMedian(currency, targetIn, true);
        median = Util.halfUp(median, 1);

        // Lots
        BigDecimal work1 = Util.B100.divide(jpyRate, 10, BigDecimal.ROUND_HALF_UP);
        BigDecimal work2 = Util.B1.divide(median, 10, BigDecimal.ROUND_HALF_UP);
        BigDecimal work3 = work1.multiply(work2);
        BigDecimal lots = AMOUNT.multiply(work3);
        lots = lots.multiply(Util.B100);
        System.out.println(currency + "     : " + lots.intValue());
        if (!"JPY".equals(currency.substring(3))) {
            median = median.divide(Util.B100);
        }
        System.out.println("median     : " + median);
        System.out.println("jpy rate   : " + jpyRate);
        if (!"USDJPY".equals(currency)) {
            System.out.println("");
            System.out.println("");
        }
    }

    public static void main(String[] args) throws Exception {
        if (!targetIn.exists()) {
            targetIn = new File(System.getProperty("user.dir") + "/src/all");
        }
        System.out.println("amount     : " + AMOUNT);
        System.out.println("");
        exe("AUDCHF");
        exe("AUDJPY");
        exe("AUDNZD");
        exe("AUDUSD");
        exe("CADJPY");
        exe("CHFJPY");
        exe("EURAUD");
        exe("EURCHF");
        exe("EURGBP");
        exe("EURJPY");
        exe("EURUSD");
        exe("GBPAUD");
        exe("GBPCHF");
        exe("GBPJPY");
        exe("GBPUSD");
        exe("NZDJPY");
        exe("NZDUSD");
        exe("USDCAD");
        exe("USDCHF");
        exe("USDJPY");
    }
}
