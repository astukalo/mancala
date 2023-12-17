package xyz.a5s7.mancala.domain.model;

import lombok.Getter;
import xyz.a5s7.mancala.domain.model.statistics.BoardState;
import xyz.a5s7.mancala.domain.model.statistics.GameStat;
import xyz.a5s7.mancala.domain.model.statistics.PlayerStat;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.IntStream;

/**
 * Mancala game for specified number of players.
 * <p>
 * Game Play:
 * The player who begins with the first move picks up all the stones in any of his own
 * six pits, and sows the stones on to the right, one in each of the following pits,
 * including his own big pit. No stones are put in the opponents' big pit. If the player's
 * last stone lands in his own big pit, he gets another turn. This can be repeated
 * several times before it's the other player's turn.
 * <p>
 * Capturing Stones:
 * During the game the pits are emptied on both sides. Always when the last stone
 * lands in an own empty pit, the player captures his own stone and all stones in the
 * opposite pit (the other player’s pit) and puts them in his own (big or little?) pit.
 * <p>
 * The Game Ends:
 * The game is over as soon as one of the sides runs out of stones. The player who
 * still has stones in his pits keeps them and puts them in his big pit. The winner of
 * the game is the player who has the most stones in his big pit.
 */
public class GameBoard {
    public static final int MIN_PLAYERS = 2;
    public static final int MAX_PLAYERS = 6;
    public static final int MIN_PITS_PER_PLAYER = 1;
    private final List<PlayerBoard> playerBoards;
    private int curPlayer;
    @Getter
    private final int numberOfPlayers;
    private final int pitsPerPlayer;

    private boolean gameFinished;
    @Getter
    private volatile boolean gameReady;

    public GameBoard(int numberOfPlayers, int pits, int stones, int curPlayer) {
        this.numberOfPlayers = numberOfPlayers;
        this.pitsPerPlayer = pits;
        this.curPlayer = curPlayer;
        checkArguments();
        if (stones <= 0) {
            throw new IllegalArgumentException("Pit must have stones");
        }
        playerBoards = new ArrayList<>(numberOfPlayers);
        for (int i = 0; i < numberOfPlayers; i++) {
            playerBoards.add(new PlayerBoard(pitsPerPlayer, stones));
        }
        gameReady = true;
    }

    private void checkArguments() {
        if (curPlayer < 0 || curPlayer >= numberOfPlayers) {
            throw new IllegalArgumentException("Current player must be in range 0.." + numberOfPlayers);
        }
        if (numberOfPlayers < MIN_PLAYERS || numberOfPlayers > MAX_PLAYERS) {
            throw new IllegalArgumentException("Number of players must be in range 2..6");
        }
        if (pitsPerPlayer < MIN_PITS_PER_PLAYER) {
            throw new IllegalArgumentException("Number of pits must be >= 1");
        }
    }

    public GameBoard(List<PlayerBoard> playerBoards, int curPlayer) {
        this.playerBoards = playerBoards;
        this.numberOfPlayers = playerBoards.size();
        this.curPlayer = curPlayer;
        this.pitsPerPlayer = playerBoards.get(0).getPits().size();
        checkArguments();
        for (PlayerBoard playerBoard : playerBoards) {
            if (playerBoard.getPits().size() != pitsPerPlayer) {
                throw new IllegalArgumentException("All players must have the same number of pits");
            }
        }
        gameReady = true;
    }

    public void play(int player, int pit) {
        if (curPlayer != player) {
            throw new IllegalStateException("Not your turn");
        }
        if (pit < 0 || pit >= pitsPerPlayer) {
            throw new IllegalArgumentException("Pit must be in range 0.." + (pitsPerPlayer-1));
        }
        if (gameFinished) {
            throw new IllegalStateException("Game is finished");
        }
        if (!gameReady) {
            throw new IllegalStateException("Game is not ready. Please wait for your turn");
        }

        gameReady = false;
        PlayerBoard curPlayerBoard = playerBoards.get(curPlayer);
        if (curPlayerBoard.getStonesInPit(pit) == 0) {
            gameReady = true;
            throw new IllegalArgumentException("Pit must have stones");
        }

        int stones = curPlayerBoard.emptyPit(pit);
        int start = pit + 1;
        int playerIdx = curPlayer;
        boolean isCurPlayerSowing = true;
        while (stones > 0) {//O(stones), but can be O(players * pits), constant time
            isCurPlayerSowing = playerIdx == curPlayer;
            stones = playerBoards.get(playerIdx).sowStones(start, stones, isCurPlayerSowing);
            start = 0;
            playerIdx = getNextPlayerIdx(playerIdx);
        }
        if (isCurPlayerSowing) {// if landed in own pit
            if (stones < 0) {
                int landedPit = pitsPerPlayer + stones;
                if (curPlayerBoard.getStonesInPit(landedPit) == 1) {
                    int nextPlayerStones = getNextPlayerBoard(curPlayer).emptyPit(landedPit);
                    //only capture stones if there are stones in the opposite pit
                    if (nextPlayerStones > 0) {
                        //player captures his own stone and all stones in the other player’s pit
                        int captured = nextPlayerStones + curPlayerBoard.emptyPit(landedPit);
                        curPlayerBoard.captureStones(captured);
                    }
                }
                curPlayer = getNextPlayerIdx(curPlayer);
            }
        } else {
            curPlayer = getNextPlayerIdx(curPlayer);
        }

        gameFinished = isGameFinished();
        if (gameFinished) {
            playerBoards.forEach(PlayerBoard::finish);
        }
        gameReady = true;
    }

    public GameStat getGameStat() {
        return new GameStat(curPlayer, getLeaderBoard(), getGameView(), gameFinished);
    }

    private List<BoardState> getGameView() {
        return IntStream.range(0, playerBoards.size())
                .mapToObj(i -> new BoardState(i, playerBoards.get(i).getPits(), playerBoards.get(i).getLargePit()))
                .toList();
    }

    private List<PlayerStat> getLeaderBoard() {
        return IntStream.range(0, playerBoards.size())
                .mapToObj(i -> new PlayerStat(i, playerBoards.get(i).getLargePit()))
                .sorted(Comparator.comparingInt(PlayerStat::getScore).reversed())
                .toList();
    }


    private boolean isGameFinished() {
        for (PlayerBoard board : playerBoards) {
            if (!board.hasStones()) {
                return true;
            }
        }
        return false;
    }

    private int getNextPlayerIdx(int curIdx) {
        return (curIdx + MIN_PITS_PER_PLAYER) % numberOfPlayers;
    }

    private PlayerBoard getNextPlayerBoard(int curIdx) {
        return playerBoards.get(getNextPlayerIdx(curIdx));
    }

}
