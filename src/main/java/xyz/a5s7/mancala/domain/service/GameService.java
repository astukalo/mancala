package xyz.a5s7.mancala.domain.service;

import jakarta.validation.constraints.NotNull;
import org.springframework.stereotype.Service;
import xyz.a5s7.mancala.domain.model.GameBoard;
import xyz.a5s7.mancala.domain.model.statistics.GameStat;
import xyz.a5s7.mancala.domain.model.GamePlayer;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class GameService {
    private static final int MAX_NUMBER_OF_GAMES = 1000;
    private static final int DEFAULT_STONES = 6;
    private static final int DEFAULT_PITS = 6;
    private static final int DEFAULT_PLAYERS_NUM = 2;

    //TODO these maps can be moved to separate Repos
    private final Map<String, GameBoard> games = new ConcurrentHashMap<>();
    private final Map<String, List<GamePlayer>> playersOfGame = new ConcurrentHashMap<>();

    public String createGame() {
        if (games.size() >= MAX_NUMBER_OF_GAMES) {
            throw new IllegalStateException("Too many games. Try again later");
        }

        GameBoard gameBoard = new GameBoard(DEFAULT_PLAYERS_NUM, DEFAULT_PITS, DEFAULT_STONES, new Random().nextInt(DEFAULT_PLAYERS_NUM));
        String id = generateId();
        games.put(id, gameBoard);
        return id;
    }

    public GamePlayer registerPlayer(@NotNull final String gameId, @NotNull final String playerId) {
        GameBoard gameBoard = getGameBoard(gameId);

        synchronized (gameBoard) {
            List<GamePlayer> playerList = playersOfGame.get(gameId);
            if (playerList != null) {
                var gamePlayer = findPlayerById(playerId, playerList);

                if (gamePlayer.isPresent()) {
                    throw new IllegalArgumentException("Player with this id is already registered");
                }
            } else {
                playerList = new ArrayList<>();
                this.playersOfGame.put(gameId, playerList);
            }
            int size = playerList.size();
            if (size >= gameBoard.getNumberOfPlayers()) {
                throw new IllegalArgumentException("Game is full");
            }
            GamePlayer player = new GamePlayer(playerId, gameId, size);
            playerList.add(player);
            return player;
        }
    }

    private static Optional<GamePlayer> findPlayerById(final String playerId, final List<GamePlayer> playerList) {
        return playerList.stream()
                .filter(p -> playerId.equals(p.getPlayerId()))
                .findFirst();
    }

    private GameBoard getGameBoard(final String gameId) {
        GameBoard gameBoard = games.get(gameId);
        if (gameBoard == null) {
            throw new IllegalArgumentException("Game not found");
        }
        return gameBoard;
    }

    //TODO could be another Response class with the list of players
    public GameStat getGameStat(@NotNull final String gameId) {
        return getGameBoard(gameId).getGameStat();
    }

    public GameStat play(@NotNull final String gameId, @NotNull final String playerId, int pit) {
        List<GamePlayer> playerList = playersOfGame.get(gameId);
        if (playerList == null) {
            throw new IllegalArgumentException("Players are not registered");
        }
        GameBoard gameBoard = getGameBoard(gameId);
        if (playerList.size() != gameBoard.getNumberOfPlayers()) {
            throw new IllegalStateException("Game is not full");
        }
        var player = findPlayerById(playerId, playerList);
        if (player.isEmpty()) {
            throw new IllegalArgumentException("Player is not registered");
        }
        synchronized (gameBoard) {
            gameBoard.play(player.get().getTurn(), pit);
            return gameBoard.getGameStat();
        }
    }

    private String generateId() {
        return UUID.randomUUID().toString();
    }

    public boolean hasGame(@NotNull final String gameId) {
        Objects.requireNonNull(gameId);

        return games.containsKey(gameId);
    }

    public boolean isPlayerRegistered(@NotNull final String gameId, @NotNull final String playerId) {
        List<GamePlayer> playerList = playersOfGame.get(gameId);
        if (playerList == null) {
            return false;
        }
        return findPlayerById(playerId, playerList).isPresent();
    }
}
