package tdd.vendingMachine;

import javafx.util.Pair;
import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;
import tdd.vendingMachine.strategy.DynamicReturnAmountStrategy;

import java.util.*;

public class DynamicReturnAmountStrategyTest {
    private DynamicReturnAmountStrategy subject;

    @Before
    public void before() {
        subject = new DynamicReturnAmountStrategy();
    }

    @Test
    public void no_shelves() {
        Assertions.assertThat(subject.execute(
            new ArrayList<>(),
            MoneyFactory.createCoin(1)
        )).isEmpty();
    }

    @Test
    public void one_shelf_possible() {
        Assertions.assertThat(subject.execute(
            Collections.singletonList(new Pair<>(MoneyFactory.createCoin(1), 2)),
            MoneyFactory.createCoin(2)
        )).hasValue(Collections.singletonList(2));
    }

    @Test
    public void on_shelf_impossible() {
        Assertions.assertThat(subject.execute(
            Collections.singletonList(new Pair<>(MoneyFactory.createCoin(1), 1)),
            MoneyFactory.createCoin(2)
        )).isEmpty();
    }

    @Test
    public void greedy_multiple_shelves() {
        Assertions.assertThat(subject.execute(
            Arrays.asList(
                new Pair<>(MoneyFactory.createCoin(1), 3),
                new Pair<>(MoneyFactory.createCoin(2), 3),
                new Pair<>(MoneyFactory.createCoin(5), 3)
            ),
            MoneyFactory.createCoin(8)
        )).hasValue(Arrays.asList(1, 1, 1));
    }

    @Test
    public void optimal_solution() {
        Assertions.assertThat(subject.execute(
            Arrays.asList(
                new Pair<>(MoneyFactory.createCoin(1), 2),
                new Pair<>(MoneyFactory.createCoin(3), 2),
                new Pair<>(MoneyFactory.createCoin(4), 1)
            ),
            MoneyFactory.createCoin(6)
        )).hasValue(Arrays.asList(0, 2, 0));
    }

    @Test
    public void multiple_shelves_impossible() {
        Assertions.assertThat(subject.execute(
            Arrays.asList(
                new Pair<>(MoneyFactory.createCoin(3), 2),
                new Pair<>(MoneyFactory.createCoin(5), 2),
                new Pair<>(MoneyFactory.createCoin(6, 9), 1)
            ),
            MoneyFactory.createCoin(7)
        )).isEmpty();
    }

    @Test
    public void fraction_parts() {
        Assertions.assertThat(subject.execute(
            Arrays.asList(
                new Pair<>(MoneyFactory.createCoin(4), 2),
                new Pair<>(MoneyFactory.createCoin(9), 1),
                new Pair<>(MoneyFactory.createCoin(0, 5), 2),
                new Pair<>(MoneyFactory.createCoin(4, 4), 2)
            ),
            MoneyFactory.createCoin(4, 9)
        )).hasValue(Arrays.asList(0, 0, 1, 1));
    }

    @Test
    public void zero_money() {
        Assertions.assertThat(subject.execute(
            Arrays.asList(
                new Pair<>(MoneyFactory.createCoin(1), 3),
                new Pair<>(MoneyFactory.createCoin(0, 3), 4)
            ),
            MoneyFactory.zero()
        )).hasValue(Arrays.asList(0, 0));
    }
}
