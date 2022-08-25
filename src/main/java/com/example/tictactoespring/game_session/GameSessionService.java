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
        gameSession.setStatus(GameStatus.NOT_READY);
        gameSessionRepository.update(gameSession);

        sendSessionMessageToHost(gameSession);

        return new GameSessionDTO(gameSession.getHost().getNickname(), gameSession.getGuestResult(),
                gameSession.getHostResult(), gameSession.getStatus(), gameSession.getBoard());
    }

    //TODO: add leaving sessions
    public void leaveSession(String token){

    }

    //TODO: add joining random sessions
    public GameSessionDTO joinRandomSession(String token){
        return null;
    }

    //TODO: add AI
    public void addAiToSesson(String token) {

    }

    public void setReady(String token) throws TokenException {
        if(!userService.isTokenValid(token))
            throw new TokenException("Invalid token");

        GameSession gameSession = gameSessionRepository.getByToken(token);

        // check if there is game session played by given player and if there is free place
        if(gameSession == null || gameSession.getGuest() == null){
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
        sendSessionMessages(gameSession);
    }

    public void makeMove(String token, int field) throws TokenException {
        GameSession gameSession = gameSessionRepository.getByToken(token);

        if(!userService.isTokenValid(token) || gameSession == null)
            throw new TokenException("Invalid token or not in game");

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

        // host - O
        // guest - X
        // check if someone win and send message to players
        if(potentialWinner == 'O'){
            gameSession.incrementHostResult();

            gameSession.setStatus(GameStatus.YOU_WON);
            sendSessionMessageToHost(gameSession);

            gameSession.setStatus(GameStatus.OPPONENT_WON);
            sendSessionMessageToGuest(gameSession);

            gameSession.setStatus(GameStatus.NOT_READY);
            gameSession.addPlayedGame();
        }else if(potentialWinner == 'X'){
            gameSession.incrementGuestResult();

            gameSession.setStatus(GameStatus.OPPONENT_WON);
            sendSessionMessageToHost(gameSession);

            gameSession.setStatus(GameStatus.YOU_WON);
            sendSessionMessageToGuest(gameSession);

            gameSession.setStatus(GameStatus.NOT_READY);
            gameSession.addPlayedGame();
        }else if(potentialWinner == 'D'){
            gameSession.addPlayedGame();
            gameSession.setStatus(GameStatus.DRAW);
            sendSessionMessages(gameSession);
            gameSession.setStatus(GameStatus.NOT_READY);
        }else{
            gameSession.switchTurn();
            sendSessionMessages(gameSession);
        }

        gameSessionRepository.update(gameSession);
    }

    private void sendSessionMessageToHost(GameSession gameSession){
        // create DTO object for host, opponent result is guest result and own result is host result
        GameSessionDTO message = new GameSessionDTO(gameSession.getGuest().getNickname(), gameSession.getHostResult(),
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

    private void sendSessionMessageToGuest(GameSession gameSession){
        // create DTO object for guest, opponent result is host result and own result is guest result
        GameSessionDTO message = new GameSessionDTO(gameSession.getHost().getNickname(), gameSession.getGuestResult(),
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

    private void sendSessionMessages(GameSession gameSession){
        sendSessionMessageToHost(gameSession);
        sendSessionMessageToGuest(gameSession);
    }
}
