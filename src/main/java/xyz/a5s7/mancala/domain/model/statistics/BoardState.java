package xyz.a5s7.mancala.domain.model.statistics;

import lombok.Value;

import java.util.List;

@Value
public class BoardState {
    int idx;
    List<Integer> pits;
    int largePit;
}
