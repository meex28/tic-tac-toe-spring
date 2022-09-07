package com.example.tictactoespring.game_session;

import com.example.tictactoespring.TokenException;
import com.example.tictactoespring.game_session.entities.GameSession;
import com.example.tictactoespring.game_session.entities.GameSessionDTO;
import com.example.tictactoespring.game_session.entities.GameStatus;
import com.example.tictactoespring.game_session.gamesession_with_ai.ai_events.PlayerActionEvent;
import com.example.tictactoespring.user.User;
import com.example.tictactoespring.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

@Service
public class GameSessionService {
    private final GameSessionRepository gameSessionRepository;
    private final UserService userService;
    private final ApplicationEventPublisher applicationEventPublisher;
    private final MessagingService messagingService;

    @Autowired
    public GameSessionService(GameSessionRepository gameSessionRepository,
                              UserService userService,
                              ApplicationEventPublisher applicationEventPublisher,
                              MessagingService messagingService) {
        this.gameSessionRepository = gameSessionRepository;
        this.userService = userService;
        this.applicationEventPublisher = applicationEventPublisher;
        this.messagingService = messagingService;
    }

    public GameSessionDTO createSession(String token, boolean isAi) throws TokenException{
        if(!userService.isTokenValid(token))
            throw new TokenException("Invalid token");

        User host = userService.getUserByToken(token);
        GameSession gameSession = new GameSession(host);
        gameSession.setAI(isAi);
        String opponentName = "None";

        if(isAi){
            opponentName = "AI";
            gameSession.initialize();
        }

        gameSessionRepository.save(gameSession);

        if(isAi)
            applicationEventPublisher.publishEvent(new PlayerActionEvent(this, token));

        return new GameSessionDTO("None", gameSession.getHostResult(),
                                    gameSession.getGuestResult(), gameSession.getStatus(),
                                    gameSession.getBoard());
    }

    public GameSessionDTO joinSession(String hostToken, String guestToken) throws TokenException {
        if(!userService.isTokenValid(guestToken))
            throw new TokenException("Invalid token");

        GameSession gameSession = gameSessionRepository.getByToken(hostToken);

        // check if there is game session played by given player and if there is free place
        if(gameSession == null || gameSession.getGuest() != null){
            throw new TokenException("There is no session with given token or its already full");
        }

        // get guest user by token, update him in as player in session and change game status
        User guest = userService.getUserByToken(guestToken);
        gameSession.setGuest(guest);
        gameSession.initialize();

        gameSessionRepository.update(gameSession);

        messagingService.sendSessionMessageToHost(gameSession);

        return new GameSessionDTO(gameSession.getHost().getNickname(), gameSession.getGuestResult(),
                gameSession.getHostResult(), gameSession.getStatus(), gameSession.getBoard());
    }

    public void leaveSession(String token) throws TokenException {
        if(!userService.isTokenValid(token))
            throw new TokenException("Invalid token");

        GameSession gameSession = gameSessionRepository.getByToken(token);

        if(gameSession == null){
            throw new TokenException("Player is not in session");
        }

        // if player with token is guest then session stays active
        // if player is host then session is ended
        User guest = gameSession.getGuest();
        if(guest != null && guest.getToken().equals(token)){
            gameSession.setGuest(null);
            gameSession.setStatus(GameStatus.OPPONENT_LEFT);
            messagingService.sendSessionMessageToHost(gameSession);
            gameSession.setStatus(GameStatus.WAITING_FOR_OPPONENT);
            gameSessionRepository.update(gameSession);
        }else{
            gameSessionRepository.delete(gameSession);
            gameSession.setHost(null);
            gameSession.setStatus(GameStatus.GAME_ENDED);
            if(gameSession.getGuest() != null)
                messagingService.sendSessionMessageToGuest(gameSession);
        }
    }

    public void setReady(String token) throws TokenException {
        if(!userService.isTokenValid(token))
            throw new TokenException("Invalid token");

        GameSession gameSession = gameSessionRepository.getByToken(token);

        // check if there is game session played by given player and if there is free place
        if(gameSession == null || (gameSession.getGuest() == null && !gameSession.isAI())){
            throw new TokenException("There is no session with given token or there is no opponent");
        }

        GameStatus status = gameSession.getStatus();

        switch (status){
            case NOT_READY -> {
                if(gameSession.getHost().getToken().equals(token))
                    gameSession.setStatus(GameStatus.GUEST_NOT_READY);
                else
                    gameSession.setStatus(GameStatus.HOST_NOT_READY);
            }
            case HOST_NOT_READY -> {
                if (gameSession.getHost().getToken().equals(token))
                    gameSession.startGame();
            }
            case GUEST_NOT_READY -> {
                if(gameSession.getGuest().getToken().equals(token))
                    gameSession.startGame();
            }
        }

        gameSessionRepository.update(gameSession);
        messagingService.sendSessionMessages(gameSession);
        if(gameSession.isAI())
            applicationEventPublisher.publishEvent(new PlayerActionEvent(this, token));
    }

    public void makeMove(String token, int field) throws TokenException {
        GameSession gameSession = gameSessionRepository.getByToken(token);

        if(!userService.isTokenValid(token) || gameSession == null)
            throw new TokenException("Invalid token or not in game");

        if(gameSession.getGuest() == null && !gameSession.isAI())
            return;

        if(!(gameSession.getHost().getToken().equals(token) && gameSession.getStatus().equals(GameStatus.HOST_TURN))
        && !(gameSession.getGuest().getToken().equals(token) && gameSession.getStatus().equals(GameStatus.GUEST_TURN)))
            return;

        if(field < 0 || field > 8)
            return;

        char playerSymbol;
        // check what symbol will be used
        if(gameSession.getHost().getToken().equals(token))
            playerSymbol = 'X';
        else
            playerSymbol = 'O';

        String board = gameSession.getBoard();

        // only empty field can be marked
        if(board.charAt(field) != '_')
            return;

        board = GameSessionUtils.setBoardField(field, board, playerSymbol);
        gameSession.setBoard(board);

        char potentialWinner = GameSessionUtils.checkWinner(board);

        // host - X
        // guest - O
        // check if someone win and send message to players
        if(potentialWinner == 'X'){
            gameSession.incrementHostResult();

            gameSession.setStatus(GameStatus.YOU_WON);
            messagingService.sendSessionMessageToHost(gameSession);

            gameSession.setStatus(GameStatus.OPPONENT_WON);
            messagingService.sendSessionMessageToGuest(gameSession);

            gameSession.setStatus(GameStatus.NOT_READY);
            gameSession.addPlayedGame();
        }else if(potentialWinner == 'O'){
            gameSession.incrementGuestResult();

            gameSession.setStatus(GameStatus.OPPONENT_WON);
            messagingService.sendSessionMessageToHost(gameSession);

            gameSession.setStatus(GameStatus.YOU_WON);
            messagingService.sendSessionMessageToGuest(gameSession);

            gameSession.setStatus(GameStatus.NOT_READY);
            gameSession.addPlayedGame();
        }else if(potentialWinner == 'D'){
            gameSession.addPlayedGame();
            gameSession.setStatus(GameStatus.DRAW);
            messagingService.sendSessionMessages(gameSession);
            gameSession.setStatus(GameStatus.NOT_READY);
        }else{
            gameSession.switchTurn();
            messagingService.sendSessionMessages(gameSession);
        }

        gameSessionRepository.update(gameSession);

        if(gameSession.isAI())
            applicationEventPublisher.publishEvent(new PlayerActionEvent(this, token));
    }
}
