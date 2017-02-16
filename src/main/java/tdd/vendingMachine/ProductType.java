package tdd.vendingMachine;

import java.math.BigDecimal;

public class ProductType {
    private BigDecimal price;
    private String name;

    public ProductType(BigDecimal price, String name) {
        this.price = price;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public BigDecimal getPrice() {
        return price;
    }
}
