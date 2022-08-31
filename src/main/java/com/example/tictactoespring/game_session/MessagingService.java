package com.example.tictactoespring.game_session;

import com.example.tictactoespring.game_session.entities.GameSession;
import com.example.tictactoespring.game_session.entities.GameSessionDTO;
import com.example.tictactoespring.game_session.entities.GameStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class MessagingService {
    private final SimpMessagingTemplate messagingTemplate;

    @Autowired
    public MessagingService(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    public void sendSessionMessageToHost(GameSession gameSession){
        // create DTO object for host, opponent result is guest result and own result is host result
        String opponent = gameSession.getGuest() == null ? "NONE" : gameSession.getGuest().getNickname();
        if(gameSession.isAI())
            opponent = "AI";

        GameSessionDTO message = new GameSessionDTO(opponent, gameSession.getHostResult(),
                gameSession.getGuestResult(), gameSession.getStatus(),
                gameSession.getBoard());

        // translate game status for host
        GameStatus status = message.getStatus();
        switch (status){
            case HOST_NOT_READY -> message.setStatus(GameStatus.NOT_READY);
            case GUEST_NOT_READY -> message.setStatus(GameStatus.OPPONENT_NOT_READY);
            case HOST_TURN -> message.setStatus(GameStatus.YOUR_TURN);
            case GUEST_TURN -> message.setStatus(GameStatus.OPPONENT_TURN);
        }

        messagingTemplate.convertAndSend("/topic/"+gameSession.getHost().getToken(), message);
    }

    public void sendSessionMessageToGuest(GameSession gameSession){
        // check if there is guest in session
        if(gameSession.getGuest() == null) return;

        // create DTO object for guest, opponent result is host result and own result is guest result
        String opponent = gameSession.getHost() == null ? "NONE" : gameSession.getHost().getNickname();
        GameSessionDTO message = new GameSessionDTO(opponent, gameSession.getGuestResult(),
                gameSession.getHostResult(), gameSession.getStatus(),
                gameSession.getBoard());

        // translate game status for guest
        GameStatus status = message.getStatus();
        switch (status){
            case HOST_NOT_READY -> message.setStatus(GameStatus.OPPONENT_NOT_READY);
            case GUEST_NOT_READY -> message.setStatus(GameStatus.NOT_READY);
            case HOST_TURN -> message.setStatus(GameStatus.OPPONENT_TURN);
            case GUEST_TURN -> message.setStatus(GameStatus.YOUR_TURN);
        }

        messagingTemplate.convertAndSend("/topic/"+gameSession.getGuest().getToken(), message);
    }

    public void sendSessionMessages(GameSession gameSession){
        sendSessionMessageToHost(gameSession);
        sendSessionMessageToGuest(gameSession);
    }
}
