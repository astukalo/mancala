package xyz.a5s7.mancala.domain.service;

import jakarta.validation.constraints.NotNull;
import org.springframework.stereotype.Service;
import xyz.a5s7.mancala.domain.model.Player;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class PlayerService {
    private final Map<String, Player> players = new ConcurrentHashMap<>();

    public String createPlayer(@NotNull final String playerName) {
        if (playerName == null || playerName.isBlank()) {
            throw new IllegalArgumentException("Player name must not be empty");
        }
        if (players.get(playerName) != null) {
            throw new IllegalArgumentException("Player with this name is already exists");
        }
        String id = generateId();
        players.put(playerName, new Player(id, playerName));

        return id;
    }

    public Player getPlayerByName(@NotNull final String playerName) {
        return players.get(playerName);
    }

    private String generateId() {
        return UUID.randomUUID().toString();
    }
}
