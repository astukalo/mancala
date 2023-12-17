package xyz.a5s7.mancala.app.rest;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import xyz.a5s7.mancala.domain.model.GamePlayer;
import xyz.a5s7.mancala.domain.model.statistics.BoardState;
import xyz.a5s7.mancala.domain.model.statistics.GameStat;
import xyz.a5s7.mancala.domain.model.statistics.PlayerStat;
import xyz.a5s7.mancala.domain.service.GameService;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class GameControllerTest {
    private final String playerId = "9258c238-a450-4259-92a2-b9217964effd";
    private final String gameId = "533a07bb-a710-4da6-9ba1-19bd43705ff4";

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private GameService gameService;

    @Test
    public void testCreateGame() throws Exception {
        when(gameService.createGame()).thenReturn(gameId);

        mockMvc.perform(post("/games/create")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(content().string(gameId));
    }

    @Test
    public void testRegisterPlayer() throws Exception {
        when(gameService.registerPlayer(gameId, playerId)).thenReturn(new GamePlayer(playerId, gameId, 1));

        mockMvc.perform(post("/games/{gameId}/players", gameId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"playerId\":\"" + playerId + "\"}")
                )
                .andExpect(status().isCreated())
                .andExpect(content()
                        .json("{\"playerId\":\"" + playerId + "\",\"gameId\":\"" + gameId + "\",\"turn\":1}")
                );
    }

    @Test
    public void testGetGameStat() throws Exception {
        when(gameService.getGameStat(gameId)).thenReturn(
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

        mockMvc.perform(get("/games/{gameId}", gameId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(
                        "{\"nextPlayer\":0," +
                                "\"leaderBoard\":[{\"idx\":1,\"score\":12},{\"idx\":0,\"score\":8}]," +
                                "\"state\":[{\"idx\":0,\"pits\":[0,0,1],\"largePit\":8}," +
                                "{\"idx\":1,\"pits\":[0,2,0],\"largePit\":12}],\"gameOver\":false}"));
    }

    @Test
    public void testPlay() throws Exception {
        int pit = 3;
        when(gameService.play(gameId, playerId, pit)).thenReturn(
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

        mockMvc.perform(post("/games/{gameId}/players/{playerId}/play?pit={pit}", gameId, playerId, pit)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(
                        "{\"nextPlayer\":0," +
                                "\"leaderBoard\":[{\"idx\":1,\"score\":12},{\"idx\":0,\"score\":8}]," +
                                "\"state\":[{\"idx\":0,\"pits\":[0,0,1],\"largePit\":8}," +
                                "{\"idx\":1,\"pits\":[0,2,0],\"largePit\":12}],\"gameOver\":false}"));
    }

    @Test
    public void testIllegalArgumentException() throws Exception {
        when(gameService.registerPlayer("invalid", "Anton")).thenThrow(new IllegalArgumentException("Invalid game id"));

        mockMvc.perform(post("/games/invalid/players")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testIllegalStateException() throws Exception {
        when(gameService.play("invalid", "1", 3)).thenThrow(new IllegalStateException("Game not ready"));

        mockMvc.perform(post("/games/invalid/players/1/play?pit=3")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isConflict());
    }
}