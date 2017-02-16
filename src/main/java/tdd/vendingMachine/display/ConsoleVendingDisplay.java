package tdd.vendingMachine.display;

import java.math.BigDecimal;
import java.text.DecimalFormat;

public class ConsoleVendingDisplay implements VendingDisplay {
    @Override
    public void displayMoney(BigDecimal money) {
        displayString(new DecimalFormat("0.00").format(money));
    }

    public void displayString(String string) {
        System.out.println("[display] " + string);
    }
}
