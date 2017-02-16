package tdd.vendingMachine;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;

public class MoneyFactory {
    public static BigDecimal zero() {
        return createCoin(0, 0);
    }

    /**
     *
     * @param integerPart Value between Integer.MIN_VALUE and Integer.MAX_VALUE represents integer part of the value
     * @param decimalPart Value between 0 and 99 inclusive representing hundredth parts of the coin
     * @return
     */
    public static BigDecimal createCoin(int integerPart, int decimalPart) {
        if (decimalPart < 0 || decimalPart > 99)
            throw new IllegalArgumentException("Decimal part is not between 0-99");
        return new BigDecimal(integerPart)
            .multiply(new BigDecimal(10))
            .add(new BigDecimal(decimalPart))
            .movePointLeft(1)
            .setScale(2, RoundingMode.UNNECESSARY);
    }

    public static BigDecimal createCoin(int integerPart) {
        return createCoin(integerPart, 0);
    }
}
