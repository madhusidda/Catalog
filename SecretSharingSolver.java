import java.io.FileReader;
import java.math.BigInteger;
import java.util.*;

public class SecretSharingSolver {

    static class Root {
        int x;
        BigInteger y;

        Root(int x, BigInteger y) {
            this.x = x;
            this.y = y;
        }
    }

    public static void main(String[] args) throws Exception {
        if (args.length < 1) {
            System.out.println("Usage: java SecretSharingSolver <input.json>");
            return;
        }

        String filename = args[0];
        JsonObject jsonObject = JsonParser.parseReader(new FileReader(filename)).getAsJsonObject();

        JsonObject keys = jsonObject.getAsJsonObject("keys");
        int n = keys.get("n").getAsInt();
        int k = keys.get("k").getAsInt();

        List<Root> roots = new ArrayList<>();

        for (Map.Entry<String, JsonElement> entry : jsonObject.entrySet()) {
            String key = entry.getKey();
            if (key.equals("keys")) continue;

            int x = Integer.parseInt(key);
            JsonObject rootObj = entry.getValue().getAsJsonObject();
            String baseStr = rootObj.get("base").getAsString();
            String valueStr = rootObj.get("value").getAsString();

            int base = Integer.parseInt(baseStr);
            BigInteger y = new BigInteger(valueStr, base);

            roots.add(new Root(x, y));
        }

        // Use only first k roots to solve polynomial
        List<Root> selectedRoots = roots.subList(0, k);

        BigInteger secret = lagrangeInterpolation(selectedRoots);
        System.out.println("Secret (constant term c) = " + secret);
    }

    // Lagrange interpolation to find f(0) = constant term c
    private static BigInteger lagrangeInterpolation(List<Root> points) {
        BigInteger result = BigInteger.ZERO;

        for (int i = 0; i < points.size(); i++) {
            BigInteger xi = BigInteger.valueOf(points.get(i).x);
            BigInteger yi = points.get(i).y;

            BigInteger numerator = BigInteger.ONE;
            BigInteger denominator = BigInteger.ONE;

            for (int j = 0; j < points.size(); j++) {
                if (i != j) {
                    BigInteger xj = BigInteger.valueOf(points.get(j).x);
                    numerator = numerator.multiply(xj.negate());
                    denominator = denominator.multiply(xi.subtract(xj));
                }
            }

            BigInteger term = yi.multiply(numerator).divide(denominator);
            result = result.add(term);
        }

        return result;
    }
}
