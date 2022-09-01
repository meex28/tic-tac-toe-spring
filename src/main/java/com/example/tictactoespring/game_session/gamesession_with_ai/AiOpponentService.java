package com.example.tictactoespring.game_session.gamesession_with_ai;

import com.example.tictactoespring.game_session.MessagingService;
import com.example.tictactoespring.game_session.entities.GameSession;
import com.example.tictactoespring.game_session.GameSessionRepository;
import com.example.tictactoespring.game_session.GameSessionUtils;
import com.example.tictactoespring.game_session.entities.GameStatus;
import com.example.tictactoespring.game_session.gamesession_with_ai.ai_events.PlayerActionEvent;
import com.example.tictactoespring.game_session.gamesession_with_ai.minimax_algorithm.MiniMaxSolutions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class AiOpponentService {
    private final GameSessionRepository gameSessionRepository;
    private final MessagingService messagingService;

    @Value("${ai.default-depth}")
    private int defaultDepth;

    @Value("${ai.default-symbol}")
    private char defaultSymbol;

    @Autowired
    public AiOpponentService(GameSessionRepository gameSessionRepository, MessagingService messagingService) {
        this.gameSessionRepository = gameSessionRepository;
        this.messagingService = messagingService;
    }

    @EventListener
    public void handlePlayerAction(PlayerActionEvent event){
        GameSession session = gameSessionRepository.getByToken(event.getToken());

        if(session.getStatus().equals(GameStatus.GUEST_TURN)){
            makeMove(session);
        }else if(session.getStatus().equals(GameStatus.GUEST_NOT_READY)){
            setReady(session);
        }
    }

    private void makeMove(GameSession session){
        String newBoard = MiniMaxSolutions.calculateNextMove(session.getBoard(), defaultDepth, defaultSymbol, defaultSymbol);
        session.setBoard(newBoard);

        char potentialWinner = GameSessionUtils.checkWinner(newBoard);

        if(potentialWinner == 'O'){
            session.incrementGuestResult();

            session.setStatus(GameStatus.OPPONENT_WON);
            messagingService.sendSessionMessageToHost(session);

            session.setStatus(GameStatus.HOST_NOT_READY);
            session.addPlayedGame();
        }else if(potentialWinner == 'D'){
            session.setStatus(GameStatus.DRAW);
            messagingService.sendSessionMessageToHost(session);

            session.setStatus(GameStatus.HOST_NOT_READY);
            session.addPlayedGame();
        }else{
            session.switchTurn();
            messagingService.sendSessionMessageToHost(session);
        }

        gameSessionRepository.update(session);
    }

    // set Guest ready only if there is a status GUEST_NOT_READY
    private void setReady(GameSession session){
        session.startGame();
        gameSessionRepository.update(session);
        messagingService.sendSessionMessageToHost(session);

        if(session.getStatus().equals(GameStatus.GUEST_TURN))
            makeMove(session);
    }
}
