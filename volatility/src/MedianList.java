import java.io.File;
import java.math.BigDecimal;

public class MedianList {

    public final static String HT = new String(new byte[] { 9 });

    private static File targetIn = new File(System.getProperty("user.dir") + "/all");

    private static void exe(String currency) throws Exception {
        // median
        BigDecimal median = Util.getMedian(currency, targetIn, true);
        median = Util.halfUp(median, 1);

        System.out.println(currency + HT + median);
    }

    public static void main(String[] args) throws Exception {
        if (!targetIn.exists()) {
            targetIn = new File(System.getProperty("user.dir") + "/src/all");
        }
        exe("AUDUSD");
        exe("EURUSD");
        exe("GBPUSD");
        exe("NZDUSD");
        exe("USDCAD");
        exe("USDCHF");
        exe("USDJPY");
    }
}
