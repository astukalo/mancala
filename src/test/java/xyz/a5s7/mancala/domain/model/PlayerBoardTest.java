package xyz.a5s7.mancala.domain.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class PlayerBoardTest {
    PlayerBoard playerBoard = new PlayerBoard(4, 4);

    @ParameterizedTest
    @CsvSource({
        "1, 4, true, 0",
        "1, 4, false, 1",
        "1, 2, true, -2",
        "0, 1, false, -4",
        "0, 8, true, 3",
        "0, 8, false, 4",
        "4, 4, true, 3",
        "4, 4, false, 4",
        "1, 3, true, -1"
    })
    void shouldReturnCorrectNumberOfStonesToSow(int start, int stones, boolean isSowLargePit, int expectedStones) {
        int actualOutput = playerBoard.sowStones(start, stones, isSowLargePit);

        assertThat(actualOutput).isEqualTo(expectedStones);
    }

    @ParameterizedTest
    @MethodSource("sowingStonesParameters")
    void shouldCorrectlyFillPits(int pit, int stones, boolean isSowLargePit, int expectedLargePit, List<Integer> expectedPits) {
        PlayerBoard playerBoard = new PlayerBoard(List.of(0, 1, 5, 3), 12);
        playerBoard.sowStones(pit, stones, isSowLargePit);
        assertThat(playerBoard.getLargePit()).isEqualTo(expectedLargePit);
        assertThat(playerBoard.getPits()).isEqualTo(expectedPits);
    }

    private static Stream<Arguments> sowingStonesParameters() {
        return Stream.of(
                Arguments.of(1, 4, true, 13, List.of(0, 2, 6, 4)),
                Arguments.of(1, 4, false, 12, List.of(0, 2, 6, 4)),
                Arguments.of(1, 2, true, 12, List.of(0, 2, 6, 3)),
                Arguments.of(0, 8, true, 13, List.of(1, 2, 6, 4)),
                Arguments.of(0, 8, false, 12, List.of(1, 2, 6, 4)),
                Arguments.of(0, 1, false, 12, List.of(1, 1, 5, 3))
        );
    }

    @Test
    void shouldThrowExceptionIfIncorrectInput(){
        assertThrows(IllegalArgumentException.class, () -> {
            playerBoard.sowStones(-1, 4, true);
        });

        assertThrows(IllegalArgumentException.class, () -> {
            playerBoard.sowStones(5, 4, true);
        });

        assertThrows(IllegalArgumentException.class, () -> {
            playerBoard.sowStones(3, 0, true);
        });
    }

    @Test
    public void shouldEmptyPitWhenPitContainsStones() {
        var initialPits = List.of(5, 4, 3, 7, 1);
        PlayerBoard playerBoard = new PlayerBoard(initialPits, 10);

        int stonesCollected = playerBoard.emptyPit(2);
        assertThat(stonesCollected).isEqualTo(3);
        assertThat(playerBoard.getPits().get(2)).isEqualTo(0);
        assertThat(playerBoard.getLargePit()).isEqualTo(10);
    }

    @Test
    public void shouldEmptyPitWhenPitIsEmpty() {
        var initialPits = List.of(5, 4, 0, 7, 1);
        PlayerBoard playerBoard = new PlayerBoard(initialPits, 10);

        int stonesCollected = playerBoard.emptyPit(2);
        assertThat(stonesCollected).isEqualTo(0);
        assertThat(playerBoard.getPits().get(2)).isEqualTo(0);
        assertThat(playerBoard.getLargePit()).isEqualTo(10);
    }

    @ParameterizedTest
    @MethodSource("finishingStates")
    public void shouldFinishBoard(final List<Integer> pits, final int largePit, int expectedLargePit, int expectedLeftStones) {
        PlayerBoard playerBoard = new PlayerBoard(pits, largePit);

        int leftStones = playerBoard.finish();

        assertThat(leftStones).isEqualTo(expectedLeftStones);
        assertThat(playerBoard.getLargePit()).isEqualTo(expectedLargePit);
        assertThat(playerBoard.getPits()).containsOnly(0);
    }

    public static Stream<Arguments> finishingStates() {
        return Stream.of(
                Arguments.of(List.of(1, 2, 3, 4), 0, 10, 10),
                Arguments.of(List.of(1, 2, 3, 4), 12, 22, 10)
        );
    }
}