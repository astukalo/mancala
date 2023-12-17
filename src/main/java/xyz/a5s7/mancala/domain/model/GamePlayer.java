package xyz.a5s7.mancala.domain.model;

import lombok.Value;

/**
 * Player registered in a game.
 */
@Value
public class GamePlayer {
    String playerId;
    String gameId;
    /**
     * Player's turn in a game.
     */
    int turn;
}
