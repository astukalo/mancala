package xyz.a5s7.mancala.app.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import xyz.a5s7.mancala.app.rest.model.RegisterPlayerRequest;
import xyz.a5s7.mancala.domain.model.statistics.GameStat;
import xyz.a5s7.mancala.domain.service.GameService;
import xyz.a5s7.mancala.domain.model.GamePlayer;

@RestController
@RequestMapping("/games")
public class GameController {

    private final GameService gameService;

    @Autowired
    public GameController(GameService gameService) {
        this.gameService = gameService;
    }

    @PostMapping("/create")
    public ResponseEntity<String> createGame() {
        String gameId = gameService.createGame();
        return new ResponseEntity<>(gameId, HttpStatus.CREATED);
    }

    @PostMapping("/{gameId}/players")
    public ResponseEntity<GamePlayer> registerPlayer(@PathVariable String gameId,
                                                     @Validated @RequestBody RegisterPlayerRequest registerPlayerRequest) {
        GamePlayer player = gameService.registerPlayer(gameId, registerPlayerRequest.getPlayerId());
        return new ResponseEntity<>(player, HttpStatus.CREATED);
    }

    @GetMapping("/{gameId}")
    public ResponseEntity<GameStat> getGameStat(@PathVariable String gameId) {
        GameStat gameStat = gameService.getGameStat(gameId);
        return new ResponseEntity<>(gameStat, HttpStatus.OK);
    }

    @PostMapping("/{gameId}/players/{playerId}/play")
    public ResponseEntity<GameStat> play(@PathVariable String gameId, @PathVariable String playerId, @RequestParam int pit) {
        GameStat gameStat = gameService.play(gameId, playerId, pit);
        return new ResponseEntity<>(gameStat, HttpStatus.OK);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleIllegalArgumentException(IllegalArgumentException ex) {
        //TODO return proper error object with codes
        return ex.getMessage();
    }

    @ExceptionHandler(IllegalStateException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public String handleIllegalStateException(IllegalStateException ex) {
        return ex.getMessage();
    }
}
