package tdd.vendingMachine;

/*
 * Written by Olaf "Ritave" Tomalka
 * There were some decisions that were taken during development that I wanted to document:
 *  - I've probably wouldn't abstract so much stuff in real scenario because it would never be reused again,
 *    but this is a show-off for Object-Oriented programming
 *  - Decided to make the vending machine not support currencies at all because this is an exercise task.
 *    This allows for easier algorithms and allows me to use BigDecimal instead of abstracting money
 *  - I went a bit overboard on coin return strategy because it was a fun problem, a simpler algorithm would be enough
 *    for practical use-case but I wanted to solve it for a general one
 */
import javafx.util.Pair;
import tdd.vendingMachine.display.ConsoleVendingDisplay;
import tdd.vendingMachine.display.VendingDisplay;
import tdd.vendingMachine.strategy.DynamicReturnAmountStrategy;
import tdd.vendingMachine.strategy.ReturnAmountByDenominationsStrategy;

import java.math.BigDecimal;
import java.util.*;

public class VendingMachine {
    private VendingDisplay display;
    private ReturnAmountByDenominationsStrategy returnStrategy;

    private List<VendingShelf> shelves;
    private List<Pair<BigDecimal, Integer>> coins;
    // This mimics real vending machines, from my own experience what I input was not always what I got back if I
    // cancelled
    private BigDecimal userEnteredMoney = MoneyFactory.zero();

    private VendingShelf selectedShelf;
    private Integer selectedShelfIndex;
    private Queue<ProductType> droppedItems = new LinkedList<>();
    private BigDecimal droppedChange = MoneyFactory.zero();

    public VendingMachine() {
        this(
            new ConsoleVendingDisplay(),
            new DynamicReturnAmountStrategy(),
            Configuration.getDefaultShelfSet(),
            Configuration.getDefaultCoinSet()
        );
    }

    public VendingMachine(
        VendingDisplay display,
        ReturnAmountByDenominationsStrategy returnStrategy,
        List<VendingShelf> shelves,
        List<Pair<BigDecimal, Integer>> coins
    ) {
        this.display = display;
        this.returnStrategy = returnStrategy;
        this.shelves = new ArrayList<>(shelves);
        this.coins = new ArrayList<>(coins);
    }

    public void insertCoin(BigDecimal coin) {
        if (addCoin(coin)) {
            userEnteredMoney = userEnteredMoney.add(coin);
            displayLeftCost();
            tryToBuyItem();
        } else {
            droppedChange = droppedChange.add(coin);
        }
    }

    public void selectShelf(int shelfNumber) {
        selectedShelf = shelves.get(shelfNumber);
        selectedShelfIndex = shelfNumber;

        if (selectedShelf.getProductCount() == 0)
            display.displayString("Warning! Not enough " + selectedShelf.getName());

        displayLeftCost();
        tryToBuyItem();
    }

    public ProductType receiveItem() {
        return droppedItems.poll();
    }

    public BigDecimal receiveChange() {
        BigDecimal result = droppedChange;
        droppedChange = MoneyFactory.zero();
        return result;
    }

    public void cancelOrder() {
        List<Integer> usedCoins = returnStrategy
            .execute(coins, userEnteredMoney)
            .orElseThrow(() -> new AssertionError("The machine must always be able to return the money inserted by user"));
        removeCoins(usedCoins);

        droppedChange = droppedChange.add(userEnteredMoney);
        clearMachineState();
        display.displayString("Order canceled, money returned");
    }

    private void tryToBuyItem() {
        if (selectedShelf != null &&
            selectedShelf.getProductCount() > 0 &&
            userEnteredMoney.compareTo(selectedShelf.getProductType().getPrice()) >= 0) {
            BigDecimal change = userEnteredMoney.subtract(selectedShelf.getProductType().getPrice());
            Optional<List<Integer>> changeCoinsOpt = returnStrategy.execute(coins, change);
            if (!changeCoinsOpt.isPresent()) {
                display.displayString("Warning! Can't return change with owned coins! Not selling product");
                cancelOrder();
            } else {
                // Simplification, removing unreceived item if not received
                droppedItems.add(shelves.get(selectedShelfIndex).getProductType());
                droppedChange = droppedChange.add(change);
                removeCoins(changeCoinsOpt.get());
                removeProduct(selectedShelfIndex);
                clearMachineState();
                display.displayString("Item bought, change returned");
            }
        }
    }

    private void displayLeftCost() {
        if (selectedShelf != null)
            display
                .displayMoney(
                    selectedShelf
                        .getProductType()
                        .getPrice()
                        .subtract(userEnteredMoney)
                );
    }

    private boolean addCoin(BigDecimal coin) {
        for (int i = 0; i < coins.size(); i++) {
            Pair<BigDecimal, Integer> pair = coins.get(i);
            if (pair.getKey().equals(coin)) {
                coins.set(i, new Pair<>(coin, pair.getValue() + 1));
                return true;
            }
        }
        return false;
    }

    private void removeCoins(List<Integer> usedCoins) {
        assert usedCoins.size() == coins.size();
        for (int i = 0; i < coins.size(); i++) {
            Pair<BigDecimal, Integer> pair = coins.get(i);
            coins.set(i, new Pair<>(pair.getKey(), pair.getValue() - usedCoins.get(i)));
        }
    }

    private void removeProduct(int shelfIndex) {
        VendingShelf shelf = shelves.get(shelfIndex);
        assert shelf.getProductCount() > 0;
        shelves.set(shelfIndex, new VendingShelf(shelf.getProductType(), shelf.getProductCount() - 1));
    }

    private void clearMachineState() {
        userEnteredMoney = MoneyFactory.zero();
        selectedShelf = null;
        selectedShelfIndex = null;
    }
}
