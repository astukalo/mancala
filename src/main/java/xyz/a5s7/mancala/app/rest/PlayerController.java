package xyz.a5s7.mancala.app.rest;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import xyz.a5s7.mancala.app.rest.model.CreatePlayerRequest;
import xyz.a5s7.mancala.domain.model.Player;
import xyz.a5s7.mancala.domain.service.PlayerService;

@RestController
@RequestMapping("/players")
public class PlayerController {
    private final PlayerService playerService;

    public PlayerController(PlayerService playerService) {
        this.playerService = playerService;
    }

    @PostMapping
    public ResponseEntity<String> createPlayer(@RequestBody @Valid CreatePlayerRequest request) {
        String playerId = playerService.createPlayer(request.getPlayerName());
        return new ResponseEntity<>(playerId, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<Player> getPlayerByName(@RequestParam String name) {
        Player player = playerService.getPlayerByName(name);
        if (player == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(player, HttpStatus.OK);
    }


    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleIllegalArgumentException(IllegalArgumentException ex) {
        return ex.getMessage();
    }
}
