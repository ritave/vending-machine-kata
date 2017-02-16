package tdd.vendingMachine;

import org.assertj.core.api.Assertions;
import org.junit.Test;

import java.math.BigDecimal;

public class MoneyFactoryTest {

    @Test
    public void zero_constructor_has_proper_scale() {
        Assertions.assertThat(MoneyFactory.zero().scale()).isEqualTo(2);
    }

    @Test
    public void zero_equal_integer() {
        Assertions.assertThat(MoneyFactory.zero()).isEqualTo(MoneyFactory.createCoin(0));
    }

    @Test
    public void zero_equal_integer_decimal() {
        Assertions.assertThat(MoneyFactory.zero()).isEqualTo(MoneyFactory.createCoin(0, 0));
    }

    @Test
    public void integer_equal_decimal() {
        Assertions.assertThat(MoneyFactory.createCoin(2)).isEqualTo(MoneyFactory.createCoin(2, 0));
    }

    @Test
    public void integer_equal_decimal_after_substract() {
        BigDecimal beforeSubstract = MoneyFactory.createCoin(2, 5);
        BigDecimal substractPart = MoneyFactory.createCoin(0, 5);
        Assertions.assertThat(MoneyFactory.createCoin(2)).isEqualTo(beforeSubstract.subtract(substractPart));
    }

    @Test
    public void not_equal() {
        Assertions.assertThat(MoneyFactory.createCoin(1, 5)).isNotEqualTo(MoneyFactory.createCoin(1));
    }
}
