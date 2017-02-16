package tdd.vendingMachine;

import java.math.BigDecimal;

public class VendingShelf {
    private ProductType productType;
    private int productCount;

    public VendingShelf(ProductType productType, int productCount) {
        this.productType = productType;
        this.productCount = productCount;
    }

    public ProductType getProductType() {
        return productType;
    }

    public int getProductCount() {
        return productCount;
    }

    public String getName() {
        return getProductType().getName();
    }

    public BigDecimal getPrice() {
        return getProductType().getPrice();
    }
}
