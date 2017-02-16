package tdd.vendingMachine;

import javafx.util.Pair;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Configuration {
    public static List<Pair<BigDecimal, Integer>> getDefaultCoinSet() {
        return Arrays.asList(
            new Pair<>(MoneyFactory.createCoin(5), 3),
            new Pair<>(MoneyFactory.createCoin(2), 3),
            new Pair<>(MoneyFactory.createCoin(1), 3),
            new Pair<>(MoneyFactory.createCoin(0, 5), 3),
            new Pair<>(MoneyFactory.createCoin(0, 2), 3),
            new Pair<>(MoneyFactory.createCoin(0, 1), 3)
        );
    }

    public static List<VendingShelf> getDefaultShelfSet() {
        List<ProductType> products = getDefaultProductsSet();
        List<VendingShelf> result = new ArrayList<>(products.size());
        for (ProductType product : products) {
            result.add(new VendingShelf(product, 3));
        }
        return result;
    }

    public static List<ProductType> getDefaultProductsSet() {
        return Arrays.asList(
            new ProductType(MoneyFactory.createCoin(2), "Soda drink"),
            new ProductType(MoneyFactory.createCoin(1, 5), "Cookie"),
            new ProductType(MoneyFactory.createCoin(0, 9), "Candy"),
            new ProductType(MoneyFactory.createCoin(0, 4), "Water"),
            new ProductType(MoneyFactory.createCoin(8, 7), "Croissant")
        );
    }
}
