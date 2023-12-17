package xyz.a5s7.mancala.domain.model;

import lombok.Getter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Mancala's player board.
 */
public class PlayerBoard {
    private final List<Integer> pits;
    private final int pitsNum;
    @Getter
    private int largePit = 0;

    public PlayerBoard(int pitsNum, int stones) {
        pits = new ArrayList<>(Collections.nCopies(pitsNum, stones));
        this.pitsNum = pitsNum;
    }

    public PlayerBoard(List<Integer> pits, int largePit) {
        this.pits = new ArrayList<>(pits);
        this.pitsNum = pits.size();
        this.largePit = largePit;
    }

    /**
     * Empties pit and returns number of stones in it.
     * @param pit index of pit to empty
     * @return number of stones in pit
     */
    public int emptyPit(int pit) {
        int stonesInPit = pits.get(pit);
        pits.set(pit, 0);
        return stonesInPit;
    }

    /**
     * Sows stones starting from the specified pit until all stones are sown
     * or the end of the board is reached.
     * @param start index of pit to start sowing from, pitsNum, if starting from large pit
     * @param stones number of stones to sow
     * @param isSowLargePit whether to sow large pit
     * @return number of stones left to sow
     * if positive - number of stones left to sow
     * if 0 - all stones were sown, the last stone landed in the large pit
     * if negative - the position of a pit where the last stone was sown
     */
    public int sowStones(int start, int stones, boolean isSowLargePit) {
        if (start < 0 || start > pitsNum) {
            throw new IllegalArgumentException("Pit must be in range 0.." + pitsNum);
        }

        if (stones == 0) {
            throw new IllegalArgumentException("Stones must be > 0");
        }
        int i = start;
        while (i < pitsNum && stones > 0) {
            pits.set(i, pits.get(i) + 1);
            i++;
            stones--;
        }
        if (isSowLargePit && stones > 0 && i == pitsNum) {
            largePit++;
            i++;
            stones--;
        }
        if (stones > 0) {
            return stones;
        }
        return -(pitsNum+1 - i);
    }

    /**
     * Captures stones from the specified pit.
     * @param nextPlayerStones number of stones in the next player's pit
     */
    public void captureStones(int nextPlayerStones) {
        largePit += nextPlayerStones;
    }

    /**
     * Checks if player has any stones left on the board.
     * @return true if player has stones left
     */
    public boolean hasStones() {
        return pits.stream().anyMatch(stones-> stones > 0);
    }

    /**
     * Finishes the game by collecting all stones from the board.
     * @return number of stones collected
     */
    public int finish() {
        int totalStones = pits.stream().mapToInt(Integer::intValue).sum();
        largePit += totalStones;
        Collections.fill(pits, 0);
        return totalStones;
    }

    public List<Integer> getPits() {
        return Collections.unmodifiableList(pits);
    }

    public int getStonesInPit(int idx) {
        return pits.get(idx);
    }
}
