package com.example.tictactoespring.game_session;

import com.example.tictactoespring.TokenException;
import com.example.tictactoespring.user.User;
import com.example.tictactoespring.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class GameSessionService {
    private final GameSessionRepository gameSessionRepository;
    private final UserService userService;
    private final SimpMessagingTemplate messagingTemplate;

    @Autowired
    public GameSessionService(GameSessionRepository gameSessionRepository, UserService userService, SimpMessagingTemplate messagingTemplate) {
        this.gameSessionRepository = gameSessionRepository;
        this.userService = userService;
        this.messagingTemplate = messagingTemplate;
    }

    public GameSessionDTO createSession(String token) throws TokenException{
        if(!userService.isTokenValid(token))
            throw new TokenException("Invalid token");

        User host = userService.getUserByToken(token);
        GameSession gameSession = new GameSession(host);

        gameSessionRepository.save(gameSession);

        return new GameSessionDTO("None", gameSession.getHostResult(),
                                    gameSession.getGuestResult(), gameSession.getStatus(),
                                    gameSession.getBoard());
    }

    public GameSessionDTO joinSession(String guestToken, String hostToken) throws TokenException {
        if(!userService.isTokenValid(guestToken))
            throw new TokenException("Invalid token");

        GameSession gameSession = gameSessionRepository.getByToken(hostToken);

        // check if there is game session played by given player and if there is free place
        if(gameSession == null || gameSession.getGuest() != null)
            throw new TokenException("There is no session with given token or its already full");

        // get guest user by token, update him in as player in session and change game status
        User guest = userService.getUserByToken(guestToken);
        gameSession.setGuest(guest);
        gameSession.setStatus(GameStatus.NOT_READY);
        gameSessionRepository.update(gameSession);

        sendSessionMessageToHost(gameSession);

        return new GameSessionDTO(gameSession.getHost().getNickname(), gameSession.getGuestResult(),
                gameSession.getHostResult(), gameSession.getStatus(), gameSession.getBoard());
    }

    private void sendSessionMessageToHost(GameSession gameSession){
        // create DTO object for host, opponent result is guest result and own result is host result
        GameSessionDTO message = new GameSessionDTO(gameSession.getGuest().getNickname(), gameSession.getHostResult(),
                                                    gameSession.getGuestResult(), gameSession.getStatus(),
                                                    gameSession.getBoard());

        messagingTemplate.convertAndSend("/topic/"+gameSession.getHost().getToken(), message);
    }

    private void sendSessionMessageToGuest(GameSession gameSession){
        // create DTO object for guest, opponent result is host result and own result is guest result
        GameSessionDTO message = new GameSessionDTO(gameSession.getHost().getNickname(), gameSession.getGuestResult(),
                gameSession.getHostResult(), gameSession.getStatus(),
                gameSession.getBoard());

        messagingTemplate.convertAndSend("/topic/"+gameSession.getGuest().getToken(), message);
    }

    private void sendSessionMessages(GameSession gameSession){
        sendSessionMessageToHost(gameSession);
        sendSessionMessageToGuest(gameSession);
    }
}
