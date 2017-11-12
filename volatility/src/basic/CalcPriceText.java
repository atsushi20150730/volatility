package basic;

import java.io.File;
import java.io.FileWriter;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CalcPriceText {

    private final static String LS = System.getProperty("line.separator");
    private final static String COMMA = ",";

    private Map<String, PriceBean> priceMap;
    private Integer amount;
    private StringBuilder sb = new StringBuilder();

    private void calc(String currency, PriceBean bean) {
        DecimalFormat lotFormat = new DecimalFormat("##0");
        DecimalFormat rangeFormat = new DecimalFormat("###0");

        /* lots */
        Double lot = amount.doubleValue() * (1.0 / bean.highLow) * 0.1;
        Double highLow = bean.highLow * 1000.0;

        sb.append(currency + COMMA);
        sb.append(lotFormat.format(lot) + COMMA);
        sb.append(rangeFormat.format(highLow) + LS + LS);
    }

    private void exe() throws Exception {
        List<String> currencyList = new ArrayList<String>(priceMap.keySet());

        for (String currency : currencyList) {
            calc(currency, priceMap.get(currency));
        }
    }

    public CalcPriceText(Map<String, PriceBean> pm, int am) throws Exception {
        priceMap = pm;
        amount = am;

        exe();

        File resultFile = new File(System.getProperty("user.home"), "result.csv");
        FileWriter fw = new FileWriter(resultFile);
        fw.write(sb.toString());
        fw.close();
        System.out.println("amount : " + amount);
    }
}
