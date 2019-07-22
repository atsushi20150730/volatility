import java.io.File;
import java.math.BigDecimal;
import java.util.ResourceBundle;

public class Calc {

	private static File targetIn = new File(System.getProperty("user.dir") + "/all");
	private static ResourceBundle target = ResourceBundle.getBundle("target");
	private static ResourceBundle rate = ResourceBundle.getBundle("rate");
	private static ResourceBundle open = ResourceBundle.getBundle("open");

	private static void exe() throws Exception {
		// currency
		String currency = target.getString("currency").toUpperCase();

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
		System.out.print(currency + " : " + lots.intValue() + Util.LS + Util.LS + Util.LS);

		// buy
		BigDecimal startBuy = openPrice.subtract(medianStart);
		BigDecimal limitBuy = startBuy.add(medianLimit);
		BigDecimal stopBuy = startBuy.subtract(medianLimit);
		System.out.print("Buy  : " + startBuy + " " + limitBuy + " " + stopBuy + Util.LS + Util.LS + Util.LS + Util.LS
				+ Util.LS);

		// sell
		BigDecimal startSell = openPrice.add(medianStart);
		BigDecimal limitSell = startSell.subtract(medianLimit);
		BigDecimal stopSell = startSell.add(medianLimit);
		System.out.print("Sell : " + startSell + " " + limitSell + " " + stopSell + Util.LS);
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
