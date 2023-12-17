package xyz.a5s7.mancala.domain.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import xyz.a5s7.mancala.domain.model.statistics.GameStat;
import xyz.a5s7.mancala.domain.model.GamePlayer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class GameServiceTest {
    private GameService gameService;
    private final String playerId = "123456";

    @BeforeEach
    void setUp() {
        gameService = new GameService();
    }

    @Test
    public void shouldCreateGame() {
        String gameId = gameService.createGame();

        assertThat(gameId).isNotNull();
        assertThat(gameService.hasGame(gameId)).isTrue();
    }

    @Test
    public void shouldRegisterPlayer() {
        String gameId = gameService.createGame();

        GamePlayer player = gameService.registerPlayer(gameId, playerId);

        assertThat(gameService.isPlayerRegistered(gameId, playerId)).isNotNull();
        assertThat(player).isNotNull();
        assertThat(player.getGameId()).isEqualTo(gameId);
        assertThat(player.getTurn()).isEqualTo(0);
    }

    @Test
    public void shouldThrowExceptionIfRegisteredForNonexistentGame() {
        assertThrows(IllegalArgumentException.class, () -> gameService.registerPlayer("nonexistentGame", "123456"));
    }

    @Test
    public void shouldThrowExceptionWhenGameIsFull() {
        String gameId = gameService.createGame();

        gameService.registerPlayer(gameId, playerId);
        gameService.registerPlayer(gameId, "abcd");

        assertThrows(IllegalArgumentException.class, () -> gameService.registerPlayer(gameId, "Anton"));
    }

    @Test
    public void shouldReturnStatisticsWhenGameExists() {
        String gameId = gameService.createGame();

        GameStat result = gameService.getGameStat(gameId);

        assertThat(result).isNotNull();
    }

    @Test
    public void shouldThrowExceptionIfGettingStatForNonexistentGame() {
        assertThrows(IllegalArgumentException.class, () -> gameService.getGameStat("nonexistentGame"));
    }

    @Test
    public void shouldPlayGameIfReady() {
        String gameId = gameService.createGame();
        GamePlayer player1 = gameService.registerPlayer(gameId, playerId);
        GamePlayer player2 = gameService.registerPlayer(gameId, "abcd");

        var nextPlayerId = player1.getPlayerId();
        if (gameService.getGameStat(gameId).getNextPlayer() == 1) {
            nextPlayerId = player2.getPlayerId();
        }

        GameStat result = gameService.play(gameId, nextPlayerId, 5);

        assertThat(result).isNotNull();
    }

    @Test
    public void shouldThrowExceptionIfNotEnoughPlayers() {
        String gameId = gameService.createGame();
        GamePlayer player1 = gameService.registerPlayer(gameId, playerId);

        assertThrows(IllegalStateException.class, () -> gameService.play(gameId, player1.getPlayerId(), 0));
    }

    @Test
    public void shouldThrowExceptionIfPlayNonexistentGame() {
        assertThrows(IllegalArgumentException.class, () -> gameService.play("nonexistentGame", "player1", 0));
    }

    @Test
    public void shouldThrowExceptionIfPlayerDoNotExist() {
        String gameId = gameService.createGame();

        assertThrows(IllegalArgumentException.class, () -> gameService.play(gameId, "nonexistentPlayer", 0));
    }
}