package com.example.tictactoespring.game_session;

import com.example.tictactoespring.TokenException;
import com.example.tictactoespring.game_session.entities.GameSessionDTO;
import com.example.tictactoespring.game_session.gamesession_with_ai.ai_events.PlayerActionEvent;
import com.example.tictactoespring.user.user_stats.UserActivityEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/api/game")
@RestController
public class GameSessionController {
    private final GameSessionService gameSessionService;
    private final ApplicationEventPublisher applicationEventPublisher;

    @Autowired
    public GameSessionController(GameSessionService gameSessionService,
                                 ApplicationEventPublisher applicationEventPublisher) {
        this.gameSessionService = gameSessionService;
        this.applicationEventPublisher = applicationEventPublisher;
    }

    @PostMapping("/join")
    public ResponseEntity<GameSessionDTO> createSession(@RequestBody String token, @RequestParam boolean isAi){
        sendUserActivityEvent(token);
        try{
            GameSessionDTO gameSession = gameSessionService.createSession(token, isAi);
            return ResponseEntity.ok().body(gameSession);
        } catch (TokenException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
    }

    @PostMapping("/join/{opponentToken}")
    public ResponseEntity<GameSessionDTO> joinSession(@PathVariable String opponentToken, @RequestBody String token){
        sendUserActivityEvent(token);
        try{
            GameSessionDTO gameSession = gameSessionService.joinSession(opponentToken, token);
            return ResponseEntity.ok().body(gameSession);
        } catch (TokenException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
    }

    @PostMapping("/leave")
    public ResponseEntity<String> leaveSession(@RequestBody String token){
        sendUserActivityEvent(token);
        try{
            gameSessionService.leaveSession(token);
            return ResponseEntity.ok(buildJSONMessage("OK"));
        }catch (TokenException e){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(buildJSONMessage(e.getMessage()));
        }
    }

    @PostMapping(value = "/ready", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> setReady(@RequestBody String token){
        sendUserActivityEvent(token);
        try{
            gameSessionService.setReady(token);
            return ResponseEntity.ok(buildJSONMessage("OK"));
        }catch (TokenException e){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(buildJSONMessage(e.getMessage()));
        }
    }

    @PostMapping(value = "/move")
    public ResponseEntity<String> makeMove(@RequestBody String token, @RequestParam int field){
        sendUserActivityEvent(token);
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

    private void sendUserActivityEvent(String token){
        applicationEventPublisher.publishEvent(new UserActivityEvent(this, token));
    }
}
