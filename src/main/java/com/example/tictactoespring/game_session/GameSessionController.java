package com.example.tictactoespring.game_session;

import com.example.tictactoespring.TokenException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/api/game")
@RestController
public class GameSessionController {
    private final GameSessionService gameSessionService;

    @Autowired
    public GameSessionController(GameSessionService gameSessionService) {
        this.gameSessionService = gameSessionService;
    }

    @PostMapping("/join")
    public ResponseEntity<GameSessionDTO> createSession(@RequestBody String token){
        try{
            GameSessionDTO gameSession = gameSessionService.createSession(token);
            return ResponseEntity.ok().body(gameSession);
        } catch (TokenException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
    }

    @PostMapping("/join/{opponentToken}")
    public ResponseEntity<GameSessionDTO> joinSession(@PathVariable String opponentToken, @RequestBody String token){
        try{
            GameSessionDTO gameSession = gameSessionService.joinSession(opponentToken, token);
            return ResponseEntity.ok().body(gameSession);
        } catch (TokenException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
    }

    @PostMapping("/leave")
    public ResponseEntity<String> leaveSession(@RequestBody String token){
        try{
            gameSessionService.leaveSession(token);
            return ResponseEntity.ok(buildJSONMessage("OK"));
        }catch (TokenException e){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(buildJSONMessage(e.getMessage()));
        }
    }

    @PostMapping(value = "/ready", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> setReady(@RequestBody String token){
        try{
            gameSessionService.setReady(token);
            return ResponseEntity.ok(buildJSONMessage("OK"));
        }catch (TokenException e){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(buildJSONMessage(e.getMessage()));
        }
    }

    @PostMapping(value = "/move")
    public ResponseEntity<String> makeMove(@RequestBody String token, @RequestParam int field){
        try{
            gameSessionService.makeMove(token, field);
            return ResponseEntity.ok(buildJSONMessage("OK"));
        } catch (TokenException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(buildJSONMessage(e.getMessage()));
        }
    }

    private String buildJSONMessage(String message){
        return "{\"message\":\""+message+"\"}";
    }
}
