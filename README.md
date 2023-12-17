# Mancala game web application

## Description
This is a REST service to play Mancala game.
The game is played by two players, each with a row of six holes, and a store to the right.

### Game Play:
The player who begins with the first move picks up all the stones in any of his own
six pits, and sows the stones on to the right, one in each of the following pits,
including his own big pit. No stones are put in the opponents' big pit. If the player's
last stone lands in his own big pit, he gets another turn. This can be repeated
several times before it's the other player's turn.
### Capturing Stones:
During the game the pits are emptied on both sides. Always when the last stone
lands in an own empty pit, the player captures his own stone and all stones in the
opposite pit (the other player’s pit) and puts them in his own (big or little?) pit.
### The Game Ends:
The game is over as soon as one of the sides runs out of stones. The player who
still has stones in his pits keeps them and puts them in his big pit. The winner of
the game is the player who has the most stones in his big pit.

## How to build
To build the project, run the following command:
```
mvn clean install
```

## How to run
To run the project, run the following command:
```
mvn spring-boot:run
```

## Show swagger documentation
To show swagger documentation, open the following url in a browser:
```
http://localhost:8080/swagger-ui.html
```

## How to play

### Register players
```
curl -X POST -H "Content-Type: application/json" -d '{"playerName":"Anton"}' http://localhost:8080/players
```
This will return a player id, which will be used to register the player in a game.
Remember the player id, as it will be used to join a game.
Do not share the player id with other players as they can use it to make moves on your behalf.

To get a player by name:
```
curl -X GET 'http://localhost:8080/players?name=Anton'
```

### Create a new game
```
curl -X POST http://localhost:8080/games/create
```
This will return a game id, which will be used to join the game.

### Join a game
Use the player id returned from the register player call to join a game.
Use the game id returned from the create game call to join the game.
```
curl -X POST -H "Content-Type: application/json" -d '{"playerId":"player_id"}' http://localhost:8080/games/{gameId}/players
```
There should be two players registered in a game before it can be started.

### Get game status
To show the board and the next player to make a move:
```
curl -X GET http://localhost:8080/games/{gameId}
```
### Make a move
```
curl -X POST http://localhost:8080/games/{gameId}/players/{playerId}/play?pit={pitNumber}
```
The game board will be returned in the response body after the move.



