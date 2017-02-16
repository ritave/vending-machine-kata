package tdd.vendingMachine.strategy;

import javafx.util.Pair;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * Calculates how to obtain a given amount of money with provided coins
 */
public interface ReturnAmountByDenominationsStrategy {
    /**
     *
     * @param denominationsCount pair of denomination and amount of that denomination
     * @return empty Optional if can't return correct amount, list in input order of how many coins to use of each
     * denomination otherwise
     */
    Optional<List<Integer>> execute(List<Pair<BigDecimal, Integer>> denominationsCount, BigDecimal returnAmount);
}
