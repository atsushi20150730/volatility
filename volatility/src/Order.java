import java.io.File;
import java.math.BigDecimal;
import java.util.ResourceBundle;

public class Order {

    private static File targetIn = new File(System.getProperty("user.dir") + "/all");
    private static ResourceBundle target = ResourceBundle.getBundle("target");
    private static ResourceBundle open = ResourceBundle.getBundle("open");

    private static void exe() throws Exception {
        // currency
        String currency = target.getString("currency").toUpperCase();

        // open price
        BigDecimal openPrice = new BigDecimal(open.getString(currency));
        openPrice = Util.halfUp(currency, openPrice);

        // median
        BigDecimal median = Util.getMedian(currency, targetIn);
        BigDecimal medianLimit = Util.halfUp(median, 1);
        if (!"JPY".equals(currency.substring(3))) {
            medianLimit = medianLimit.divide(Util.B100);
        }

        // Lots
        BigDecimal price = new BigDecimal(target.getString("price"));
        System.out.print(currency + " price : " + price + " median : " + medianLimit + Util.LS + Util.LS + Util.LS);

        BigDecimal limit = price.add(medianLimit);
        BigDecimal stop = price.subtract(medianLimit);
        System.out.print("Order : " + limit + " " + stop + Util.LS);
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
