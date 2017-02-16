package tdd.vendingMachine;

import org.assertj.core.api.Assertions;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import tdd.vendingMachine.display.ConsoleVendingDisplay;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

public class ConsoleVendingDisplayTest {
    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final PrintStream outStream = new PrintStream(outContent);
    private PrintStream realStream = System.out;

    @Before
    public void before() {
        System.setOut(outStream);
    }

    @After
    public void after() {
        System.setOut(realStream);
    }

    @Test
    public void prints_money_correctly() {
        new ConsoleVendingDisplay().displayMoney(MoneyFactory.createCoin(123, 3));
        Assertions.assertThat(outContent.toString().trim()).isEqualTo("[display] 123,30");
    }

    @Test
    public void prints_string_correctly() {
        new ConsoleVendingDisplay().displayString("test string");
        Assertions.assertThat(outContent.toString().trim()).isEqualTo("[display] test string");
    }
}
