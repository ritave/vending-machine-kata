package tdd.vendingMachine.display;

import java.math.BigDecimal;

public interface VendingDisplay {
    void displayMoney(BigDecimal money);
    void displayString(String string);
}
