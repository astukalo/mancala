package xyz.a5s7.mancala.domain.model.statistics;

import lombok.Value;

import java.util.List;

@Value
public class GameStat {
    int nextPlayer;
    List<PlayerStat> leaderBoard;
    List<BoardState> state;

    boolean gameOver;
}
