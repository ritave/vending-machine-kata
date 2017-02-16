package tdd.vendingMachine;

import javafx.util.Pair;
import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import tdd.vendingMachine.display.VendingDisplay;
import tdd.vendingMachine.strategy.ReturnAmountByDenominationsStrategy;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.contains;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class VendingMachineTest {
    @Mock
    VendingDisplay fakeDisplay;
    @Mock
    ReturnAmountByDenominationsStrategy fakeStrategy;

    @Before
    public void before() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void just_a_stupid_passing_test_to_ensure_that_tests_are_run() {
        Assertions.assertThat(new VendingMachine()).isNotNull();
    }

    @Test
    public void simple_fake_buy() {
        ProductType fakeProduct = new ProductType(MoneyFactory.createCoin(1), "fake product");
        VendingMachine subject = new VendingMachine(
            fakeDisplay,
            fakeStrategy,
            Collections.singletonList(new VendingShelf(fakeProduct, 1)),
            Collections.singletonList(new Pair<>(MoneyFactory.createCoin(1), 0))
        );

        when(fakeStrategy.execute(any(), any())).thenReturn(Optional.of(Collections.singletonList(1)));

        subject.selectShelf(0);
        subject.insertCoin(MoneyFactory.createCoin(1));

        Assertions.assertThat(subject.receiveItem()).isEqualTo(fakeProduct);
        Assertions.assertThat(subject.receiveItem()).isNull();
        Assertions.assertThat(subject.receiveChange()).isEqualTo(MoneyFactory.zero());
    }

    @Test
    public void no_item_without_enough_money() {
        ProductType fakeProduct = new ProductType(MoneyFactory.createCoin(3), "fake product");
        VendingMachine subject = new VendingMachine(
            fakeDisplay,
            fakeStrategy,
            Collections.singletonList(new VendingShelf(fakeProduct, 1)),
            Collections.singletonList(new Pair<>(MoneyFactory.createCoin(1), 0))
        );

        when(fakeStrategy.execute(any(), any())).thenReturn(Optional.of(Collections.singletonList(1)));

        subject.insertCoin(MoneyFactory.createCoin(1));
        subject.selectShelf(0);

        Assertions.assertThat(subject.receiveItem()).isNull();
    }

    @Test
    public void cancel_drops_money() {
        ProductType fakeProduct = new ProductType(MoneyFactory.createCoin(2), "fake product");
        VendingMachine subject = new VendingMachine(
            fakeDisplay,
            fakeStrategy,
            Collections.singletonList(new VendingShelf(fakeProduct, 1)),
            Collections.singletonList(new Pair<>(MoneyFactory.createCoin(1), 0))
        );

        when(fakeStrategy.execute(any(), any())).thenReturn(Optional.of(Collections.singletonList(1)));

        subject.insertCoin(MoneyFactory.createCoin(1));
        subject.selectShelf(0);
        subject.cancelOrder();

        Assertions.assertThat(subject.receiveItem()).isNull();
        Assertions.assertThat(subject.receiveChange()).isEqualTo(MoneyFactory.createCoin(1));
    }

    @Test
    public void buy_drops_change() {
        ProductType fakeProduct = new ProductType(MoneyFactory.createCoin(1), "fake product");
        VendingMachine subject = new VendingMachine(
            fakeDisplay,
            fakeStrategy,
            Collections.singletonList(new VendingShelf(fakeProduct, 1)),
            Arrays.asList(
                new Pair<>(MoneyFactory.createCoin(1), 2),
                new Pair<>(MoneyFactory.createCoin(3), 0)
            )
        );

        when(fakeStrategy.execute(any(), any())).thenReturn(Optional.of(Arrays.asList(2, 0)));

        subject.insertCoin(MoneyFactory.createCoin(3));
        subject.selectShelf(0);

        Assertions.assertThat(subject.receiveItem()).isNotNull();
        Assertions.assertThat(subject.receiveChange()).isEqualTo(MoneyFactory.createCoin(2));
    }

    @Test
    public void can_reuse_inserted_coins() {
        ProductType fakeProduct = new ProductType(MoneyFactory.createCoin(1), "fake product");
        VendingMachine subject = new VendingMachine(
            fakeDisplay,
            fakeStrategy,
            Collections.singletonList(new VendingShelf(fakeProduct, 2)),
            Arrays.asList(
                new Pair<>(MoneyFactory.createCoin(1), 2),
                new Pair<>(MoneyFactory.createCoin(3), 0),
                new Pair<>(MoneyFactory.createCoin(4), 0)
            )
        );

        when(fakeStrategy.execute(any(), eq(MoneyFactory.createCoin(2))))
            .thenReturn(Optional.of(Arrays.asList(2, 0, 0)));
        when(fakeStrategy.execute(any(), eq(MoneyFactory.createCoin(3))))
            .thenReturn(Optional.of(Arrays.asList(0, 1, 0)));

        subject.insertCoin(MoneyFactory.createCoin(3));
        subject.selectShelf(0);

        Assertions.assertThat(subject.receiveItem()).isNotNull();
        Assertions.assertThat(subject.receiveChange()).isEqualTo(MoneyFactory.createCoin(2));


        subject.selectShelf(0);
        subject.insertCoin(MoneyFactory.createCoin(4));

        Assertions.assertThat(subject.receiveItem()).isNotNull();
        Assertions.assertThat(subject.receiveChange()).isEqualTo(MoneyFactory.createCoin(3));
    }

    @Test
    public void doesnt_sell_if_cant_give_change() {
        ProductType fakeProduct = new ProductType(MoneyFactory.createCoin(1), "fake product");
        VendingMachine subject = new VendingMachine(
            fakeDisplay,
            fakeStrategy,
            Collections.singletonList(new VendingShelf(fakeProduct, 1)),
            Collections.singletonList(new Pair<>(MoneyFactory.createCoin(2), 0))
        );

        when(fakeStrategy.execute(any(), eq(MoneyFactory.createCoin(1)))).thenReturn(Optional.empty());
        when(fakeStrategy.execute(any(), eq(MoneyFactory.createCoin(2))))
            .thenReturn(Optional.of(Collections.singletonList(1)));

        subject.insertCoin(MoneyFactory.createCoin(2));
        subject.selectShelf(0);

        verify(fakeDisplay).displayString(contains("Warning"));
        Assertions.assertThat(subject.receiveItem()).isNull();
        Assertions.assertThat(subject.receiveChange()).isEqualTo(MoneyFactory.createCoin(2));
    }

    @Test
    public void cancel_returns_money() {
        ProductType fakeProduct = new ProductType(MoneyFactory.createCoin(2), "fake product");
        VendingMachine subject = new VendingMachine(
            fakeDisplay,
            fakeStrategy,
            Collections.singletonList(new VendingShelf(fakeProduct, 1)),
            Collections.singletonList(new Pair<>(MoneyFactory.createCoin(1), 0))
        );

        when(fakeStrategy.execute(any(), any())).thenReturn(Optional.of(Collections.singletonList(1)));

        subject.insertCoin(MoneyFactory.createCoin(1));
        subject.cancelOrder();

        Assertions.assertThat(subject.receiveItem()).isNull();
        Assertions.assertThat(subject.receiveChange()).isEqualTo(MoneyFactory.createCoin(1));
    }

    @Test
    public void doesnt_give_product_from_empty_shelf() {
        ProductType fakeProduct = new ProductType(MoneyFactory.createCoin(1), "fake product");
        VendingMachine subject = new VendingMachine(
            fakeDisplay,
            fakeStrategy,
            Collections.singletonList(new VendingShelf(fakeProduct, 0)),
            Collections.singletonList(new Pair<>(MoneyFactory.createCoin(1), 0))
        );

        when(fakeStrategy.execute(any(), any())).thenReturn(Optional.of(Collections.singletonList(1)));

        subject.insertCoin(MoneyFactory.createCoin(1));
        subject.selectShelf(0);

        verify(fakeDisplay).displayString(contains("Warning"));
        Assertions.assertThat(subject.receiveItem()).isNull();

        Assertions.assertThat(subject.receiveChange()).isEqualTo(MoneyFactory.zero());

        subject.cancelOrder();

        Assertions.assertThat(subject.receiveChange()).isEqualTo(MoneyFactory.createCoin(1));
    }

    @Test
    public void shows_price() {
        ProductType fakeProduct = new ProductType(MoneyFactory.createCoin(3), "fake product");
        VendingMachine subject = new VendingMachine(
            fakeDisplay,
            fakeStrategy,
            Collections.singletonList(new VendingShelf(fakeProduct, 1)),
            Collections.singletonList(new Pair<>(MoneyFactory.createCoin(1), 0))
        );

        when(fakeStrategy.execute(any(), any())).thenReturn(Optional.of(Collections.singletonList(0)));

        subject.selectShelf(0);
        for (int i = 0; i < 3; i++)
            subject.insertCoin(MoneyFactory.createCoin(1));

        // Price
        for (int i = 3; i >=0; i--)
            verify(fakeDisplay).displayMoney(MoneyFactory.createCoin(i));
    }

    @Test
    public void sample_integration_test() {
        final int CROISSANT_SHELF_NUM = 4;
        VendingMachine subject = new VendingMachine();

        ProductType croissant = Configuration.getDefaultProductsSet().get(CROISSANT_SHELF_NUM);
        subject.selectShelf(CROISSANT_SHELF_NUM);
        subject.insertCoin(MoneyFactory.createCoin(5));
        subject.insertCoin(MoneyFactory.createCoin(2));
        subject.insertCoin(MoneyFactory.createCoin(2));

        Assertions.assertThat(subject.receiveItem()).isEqualToComparingFieldByField(croissant);
        Assertions.assertThat(subject.receiveItem()).isNull();
        Assertions.assertThat(subject.receiveChange()).isEqualTo(MoneyFactory.createCoin(0, 3));
        Assertions.assertThat(subject.receiveChange()).isEqualTo(MoneyFactory.zero());
    }

    /*
     * And so on, and so on. Think that's enough for an example project
     */
}
