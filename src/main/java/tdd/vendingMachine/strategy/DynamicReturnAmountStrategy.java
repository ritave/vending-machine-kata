package tdd.vendingMachine.strategy;

import javafx.util.Pair;
import tdd.vendingMachine.MoneyFactory;

import java.math.BigDecimal;
import java.util.*;

/*
 * Since we don't know if we're operating in canonical coin system we have to resort to dynamic programming instead of
 * greedy one. The problem with decimals here is that the difference between minimal coin and target can be very large
 * so creating an array of minimal steps is not practical, we have to resort to maps
 *
 * Got a little carried away here, in practical scenario a greedy algorithm will be enough, all current nations use
 * canonical coin systems and even if not, vending machines don't have expensive stuff in it so dynamic programming with
 * minimal step will be fine too
 */
public class DynamicReturnAmountStrategy implements ReturnAmountByDenominationsStrategy {
    private final int BACKTRACKING_FINISHED = -1;

    private Map<BigDecimal, State> optimalAchievedValue;
    private List<Pair<BigDecimal, Integer>> denominations;
    private BigDecimal target;

    @Override
    public Optional<List<Integer>> execute(List<Pair<BigDecimal, Integer>> denominationsCount, BigDecimal returnAmount) {
        optimalAchievedValue = new HashMap<>();
        denominations = denominationsCount;
        target = returnAmount;

        calculateOptimalValues();
        if (!optimalAchievedValue.containsKey(returnAmount))
            return Optional.empty();

        List<Integer> result = calculateUsedCoinsList();

        optimalAchievedValue = null;
        denominations = null;
        target = null;

        return Optional.of(result);
    }

    /*
     * A mix between normal coin change problem dynamic solution and a dijkstra.
     * Each iteration of out-most loop updates optimalAchievedValue for the optimal solution using only
     * that denomination and all previous ones.
     * Since we can't use an array as in normal dynamic solution we use maps.
     * In doing so we can't iterate from lowest to highest achievable values easily (while updating the list in place)
     * and so we treat it as a graph and travel using a sorted list of achievable values needing an update. By going
     * through them we achieve the same solution as normal dynamic problem on arrays, only we skip the unachievable
     * elements
     */
    private void calculateOptimalValues() {
        Map<BigDecimal, Integer> usedCoins = new HashMap<>();
        Queue<BigDecimal> updateList = new PriorityQueue<>();
        optimalAchievedValue.put(MoneyFactory.zero(), new State(0, BACKTRACKING_FINISHED));

        for (int coinIndex = 0; coinIndex < denominations.size(); coinIndex++) {
            BigDecimal coin = denominations.get(coinIndex).getKey();
            assert coin.compareTo(MoneyFactory.zero()) > 0;
            Integer count = denominations.get(coinIndex).getValue();

            usedCoins.clear();
            updateList.clear();

            for (Map.Entry<BigDecimal, State> cached : optimalAchievedValue.entrySet()) {
                updateList.add(cached.getKey());
                usedCoins.put(cached.getKey(), 0);
            }

            while (!updateList.isEmpty()) {
                BigDecimal currentValue = updateList.remove();
                assert usedCoins.get(currentValue) != null;
                if (usedCoins.get(currentValue).equals(count))
                    continue;

                State currentState = optimalAchievedValue.get(currentValue);
                Integer currentUsedCoins = usedCoins.get(currentValue);
                assert currentState != null;
                assert currentUsedCoins != null;

                BigDecimal jump = currentValue.add(coin);
                if (jump.compareTo(target) > 0)
                    continue;

                State jumpedState = optimalAchievedValue.get(jump);
                if (jumpedState == null || jumpedState.numOfCoins > currentState.numOfCoins + 1) {
                    updateList.add(jump);
                    usedCoins.put(jump, currentUsedCoins + 1);
                    optimalAchievedValue.put(jump, new State(currentState.numOfCoins + 1, coinIndex));
                }
            }
        }
    }


    /*
     * We backtrack starting from the returned amount solution and use lastCoinIndex to recreate a list of used coins
     * in optimal solution
     */
    private List<Integer> calculateUsedCoinsList() {
        assert optimalAchievedValue.containsKey(target);

        List<Integer> result = new ArrayList<>(denominations.size());
        for (int i = 0; i < denominations.size(); i++)
            result.add(0);

        BigDecimal currentValue = target;
        State currentState = optimalAchievedValue.get(currentValue);
        while (currentState.lastUsedCoinIndex != BACKTRACKING_FINISHED) {
            result.set(currentState.lastUsedCoinIndex, result.get(currentState.lastUsedCoinIndex) + 1);
            currentValue = currentValue.subtract(denominations.get(currentState.lastUsedCoinIndex).getKey());
            currentState = optimalAchievedValue.get(currentValue);
        }
        return result;
    }

    private static class State {
        int numOfCoins;
        int lastUsedCoinIndex;

        State(int numOfCoins, int lastUsedCoinIndex) {
            this.numOfCoins = numOfCoins;
            this.lastUsedCoinIndex = lastUsedCoinIndex;
        }
    }
}
