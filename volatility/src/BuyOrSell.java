
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;

public class BuyOrSell {

    private int getRandom(int val) {
        Random ran = new Random();

        return ran.nextInt(val);
    }

    private Map<Integer, Integer> execute() {
        Map<Integer, Integer> result = new TreeMap<Integer, Integer>();

        while (true) {
            result.put(getRandom(7), getRandom(2));

            if (result.size() == 6) {
                int total = 0;
                for (Integer key : result.keySet()) {
                    total += result.get(key);
                }
                if (total == 3) {
                    break;
                }
                result = new TreeMap<Integer, Integer>();
            }
        }

        return result;
    }

    public BuyOrSell() {
        // random
        Map<Integer, Integer> result = execute();
        for (Integer key : result.keySet()) {
            // currency
            if (key == 0) {
                System.out.print("AUDJPY");
            } else if (key == 1) {
                System.out.print("CADJPY");
            } else if (key == 2) {
                System.out.print("CHFJPY");
            } else if (key == 3) {
                System.out.print("EURJPY");
            } else if (key == 4) {
                System.out.print("GBPJPY");
            } else if (key == 5) {
                System.out.print("NZDJPY");
            } else if (key == 6) {
                System.out.print("USDJPY");
            }

            // buy or sell
            if (result.get(key) == 1) {
                System.out.println("    買");
            } else {
                System.out.println("    売");
            }
            System.out.println("");
        }
    }

    public static void main(String[] args) {
        new BuyOrSell();
    }
}
