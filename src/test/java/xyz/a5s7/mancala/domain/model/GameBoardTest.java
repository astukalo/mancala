package xyz.a5s7.mancala.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import xyz.a5s7.mancala.domain.model.statistics.BoardState;
import xyz.a5s7.mancala.domain.model.statistics.GameStat;
import xyz.a5s7.mancala.domain.model.statistics.PlayerStat;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

public class GameBoardTest {
    @DisplayName("1. Player 0 plays a turn, returns to the same pit and captures stones from Player 1.")
    @Test
    public void testPlayer0PlaysTurnAndCapturesStonesFromPlayer1() {
        checkScenario(
                List.of(4, 9, 4, 4), 0,
                List.of(4, 4, 4, 4), 0,
                0, //turn
                1, //selectedPit
                List.of(5, 0, 5, 5), //expectedP0Pits
                7, //expectedP0Score
                List.of(5, 0, 5, 5), //expectedP1Pits
                0, //expectedP1Score
                false, //isGameFinished
                1 //nextPlayer
        );
    }

    @DisplayName("2. Player 0 plays a turn, lands in his large pit and gets another turn.")
    @Test
    public void testPlayer0LandsInLargePitAndGetsAnotherTurn() {
        checkScenario(
                List.of(4, 9, 4, 4), 8,
                List.of(4, 4, 2, 4), 10,
                1, //turn
                2, //selectedPit
                List.of(4, 9, 4, 4), //expectedP0Pits
                8, //expectedP0Score
                List.of(4, 4, 0, 5), //expectedP1Pits
                11, //expectedP1Score
                false, //isGameFinished
                1 //nextPlayer
        );
    }

    @DisplayName("3. Player 0 plays a turn, ends last stone and loses the game.")
    @Test
    public void testPlayer0LosesGameAfterLastStoneEndsAndPlayer1Wins() {
        checkScenario(
                List.of(0, 0, 0, 1), 8,
                List.of(4, 4, 4, 4), 0,
                0, //turn
                3, //selectedPit
                List.of(0, 0, 0, 0), //expectedP0Pits
                9, //expectedP0Score
                List.of(0, 0, 0, 0), //expectedP1Pits
                16, //expectedP1Score
                true, //isGameFinished
                0 //nextPlayer
        );
    }

    @DisplayName("4. Player 0 sow to large pit 2 times and gets another turn.")
    @Test
    public void testPlayer0SowsToLargePitAndGetsAnotherTurn() {
        checkScenario(
                List.of(4, 12, 4, 4), 10,
                List.of(4, 4, 4, 4), 0,
                0, //turn
                1, //selectedPit
                List.of(5, 1, 6, 6), //expectedP0Pits
                12, //expectedP0Score
                List.of(5, 5, 5, 5), //expectedP1Pits
                0, //expectedP1Score
                false, //isGameFinished
                0 //nextPlayer
        );
    }

    @DisplayName("5. Player 0 sow to large pit 2 times and ends at other player's pit.")
    @Test
    public void testPlayer0SowsToLargePitTwoTimesAndEndsAtOtherPlayersPit() {
        checkScenario(
                List.of(4, 14, 4, 4), 10,
                List.of(4, 4, 4, 4), 0,
                0, //turn
                1, //selectedPit
                List.of(5, 1, 6, 6), //expectedP0Pits
                12, //expectedP0Score
                List.of(6, 6, 5, 5), //expectedP1Pits
                0, //expectedP1Score
                false, //isGameFinished
                1 //nextPlayer
        );
    }

    @DisplayName("6. Player 0 ends at other player's empty pit.")
    @Test
    public void testPlayer0EndsAtOtherPlayerEmptyPit() {
        checkScenario(
                List.of(4, 4, 4, 2), 10,
                List.of(0, 4, 4, 4), 0,
                0, //turn
                3, //selectedPit
                List.of(4, 4, 4, 0), //expectedP0Pits
                11, //expectedP0Score
                List.of(1, 4, 4, 4), //expectedP1Pits
                0, //expectedP1Score
                false, //isGameFinished
                1 //nextPlayer
        );
    }

    @DisplayName("7. Player 0 ends at empty pit, but there are not stones in the opposite pit.")
    @Test
    public void testPlayer0EndsAtEmptyPitButNoStonesInOppositePit() {
        checkScenario(
                List.of(1, 0, 6), 3,
                List.of(1, 0, 5), 2,
                0, //turn
                0, //selectedPit
                List.of(0, 1, 6), //expectedP0Pits
                3, //expectedP0Score
                List.of(1, 0, 5), //expectedP1Pits
                2, //expectedP1Score
                false, //isGameFinished
                1 //nextPlayer
        );
    }

    @Test
    public void gameDemo() {
        GameBoard gameBoard = new GameBoard(2, 3, 3, 0);
        verifyState(0, 1, List.of(3, 0, 4), 1, List.of(4, 3, 3), 0, false, 1, gameBoard);
        verifyState(1, 0, List.of(4, 0, 4), 1, List.of(0, 4, 4), 1, false, 0, gameBoard);
        verifyState(0, 0, List.of(0, 1, 5), 2, List.of(1, 4, 4), 1, false, 1, gameBoard);
        verifyState(1, 1, List.of(1, 2, 5), 2, List.of(1, 0, 5), 2, false, 0, gameBoard);
        verifyState(0, 1, List.of(1, 0, 6), 3, List.of(1, 0, 5), 2, false, 0, gameBoard);
        verifyState(0, 0, List.of(0, 1, 6), 3, List.of(1, 0, 5), 2, false, 1, gameBoard);
        verifyState(1, 0, List.of(0, 0, 6), 3, List.of(0, 0, 5), 4, false, 0, gameBoard);
        verifyState(0, 2, List.of(1, 0, 0), 6, List.of(1, 0, 6), 4, false, 1, gameBoard);
        verifyState(1, 2, List.of(2, 0, 1), 6, List.of(2, 0, 0), 7, false, 0, gameBoard);
        verifyState(0, 2, List.of(2, 0, 0), 7, List.of(2, 0, 0), 7, false, 0, gameBoard);
        verifyState(0, 0, List.of(0, 1, 1), 7, List.of(2, 0, 0), 7, false, 1, gameBoard);
        verifyState(1, 0, List.of(0, 1, 0), 7, List.of(0, 1, 0), 9, false, 0, gameBoard);
        verifyState(0, 1, List.of(0, 0, 1), 7, List.of(0, 1, 0), 9, false, 1, gameBoard);
        verifyState(1, 1, List.of(0, 0, 0), 7, List.of(0, 0, 0), 11, true, 0, gameBoard);
    }

    private void checkScenario(List<Integer> p0Pits, int p0Score, List<Integer> p1Pits, int p1Score,
               int turn, int selectedPit,
               List<Integer> expectedP0Pits, int expectedP0Score, List<Integer> expectedP1Pits, int expectedP1Score,
               boolean isGameFinished, int nextPlayer) {
        GameBoard gameBoard = new GameBoard(List.of(
                new PlayerBoard(p0Pits, p0Score),
                new PlayerBoard(p1Pits, p1Score)
        ), turn);
        verifyState(turn, selectedPit, expectedP0Pits, expectedP0Score, expectedP1Pits, expectedP1Score, isGameFinished, nextPlayer, gameBoard);
    }

    private static void verifyState(int turn, int selectedPit,
                                    List<Integer> expectedP0Pits, int expectedP0Score,
                                    List<Integer> expectedP1Pits, int expectedP1Score,
                                    boolean isGameFinished, int nextPlayer, GameBoard gameBoard) {
        gameBoard.play(turn, selectedPit);
        GameStat gameStat = gameBoard.getGameStat();

        assertThat(gameStat.getState().get(0).getPits()).isEqualTo(expectedP0Pits);
        assertThat(gameStat.getState().get(1).getPits()).isEqualTo(expectedP1Pits);
        assertThat(gameStat.getState().get(0).getLargePit()).isEqualTo(expectedP0Score);
        assertThat(gameStat.getState().get(1).getLargePit()).isEqualTo(expectedP1Score);

        assertThat(gameStat.isGameOver()).isEqualTo(isGameFinished);
        assertThat(gameStat.getNextPlayer()).isEqualTo(nextPlayer);
    }

    @Test
    public void shouldNotAllowPlayOnIncorrectTurn() {
        GameBoard gameBoard = new GameBoard(2, 6, 4, 0);

        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> gameBoard.play(1, 3));
        assertEquals("Not your turn", exception.getMessage());
    }

    @Test
    public void shouldNotAllowPlayOnOutOfRangePit() {
        GameBoard gameBoard = new GameBoard(2, 6, 4, 0);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> gameBoard.play(0, 6));
        assertEquals("Pit must be in range 0..5", exception.getMessage());
    }

    @Test
    public void shouldNotAllowPlayOnEmptyPit() {
        GameBoard gameBoard = new GameBoard(List.of(
                new PlayerBoard(List.of(0, 0, 0), 0),
                new PlayerBoard(List.of(0, 0, 0), 0)
        ), 0);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> gameBoard.play(0, 0));
        assertEquals("Pit must have stones", exception.getMessage());
    }

    @Test
    public void shouldNotAllowPlayAfterGameHasFinished() {
        GameBoard gameBoard = new GameBoard(List.of(
                new PlayerBoard(List.of(0, 0, 1), 0),
                new PlayerBoard(List.of(0, 0, 0), 0)
        ), 0);
        gameBoard.play(0, 2);

        // assuming the game finishes after one turn
        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> gameBoard.play(0, 1));
        assertEquals("Game is finished", exception.getMessage());
    }

    @Test
    void shouldReturnGameStat() {
        GameBoard gameBoard =  new GameBoard(List.of(
                new PlayerBoard(List.of(0, 0, 1), 8),
                new PlayerBoard(List.of(0, 2, 0), 12)
        ), 0);
        GameStat gameStat = gameBoard.getGameStat();

        assertThat(gameStat).isEqualTo(
                new GameStat(
                        0,
                        List.of(
                                new PlayerStat(1, 12),
                                new PlayerStat(0, 8)
                        ),
                        List.of(
                                new BoardState(0, List.of(0, 0, 1), 8),
                                new BoardState(1, List.of(0, 2, 0), 12)
                        ),
                false
                )
        );
    }

    @Test
    public void shouldNotCallPlayIfTurnIsNotFinished() throws InterruptedException {
        GameBoard gameBoard = new GameBoard(2, 6, 4, 0);

        ExecutorService executorService = Executors.newFixedThreadPool(2);
        CountDownLatch latch = new CountDownLatch(2);

        executorService.execute(() -> {
            try {
                gameBoard.play(0, 3);
            } finally {
                latch.countDown();
            }
        });

        executorService.execute(() -> {
            try {
                assertThrows(IllegalStateException.class, () -> gameBoard.play(0, 2));
            } finally {
                latch.countDown();
            }
        });

        latch.await(5, TimeUnit.SECONDS);
    }
}