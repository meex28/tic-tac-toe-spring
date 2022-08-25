package com.example.tictactoespring;

import com.example.tictactoespring.game_session.*;
import com.example.tictactoespring.user.User;
import com.example.tictactoespring.user.UserController;
import com.example.tictactoespring.user.UserRepository;
import com.example.tictactoespring.user.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Date;

@SpringBootTest
class TicTacToeSpringApplicationTests {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private UserController userController;

    @Autowired
    private GameSessionRepository gameSessionRepository;

    @Autowired
    private GameSessionService gameSessionService;

    @Test
    void contextLoads() {
    }

    @Test
    void testUserRepo(){
        User user = new User("testToken", "testNickname", new Date());
        userRepository.add(user);
        System.out.println(userRepository.get("testToken"));
        userRepository.delete("testToken");
        System.out.println(userRepository.get("testToken"));
    }

    @Test
    void testAddUser() throws TokenException {
//        userService.createUser("asd");
        userController.userStart("asd");
    }

//    @Test
//    void testTokenGeneration(){
//        for(int i = 0; i<10; i++){
//            try{
//                System.out.println(userService.generateToken("nickname"));
//            }catch(Exception e){
//                e.printStackTrace();
//            }
//        }
//    }

    @Test
    void testCheckingWinner(){
        String board = "_________";
        System.out.println(GameSessionUtils.checkWinner(board) == '_');

        board = "X___X___X";
        System.out.println(GameSessionUtils.checkWinner(board) == 'X');

        board = "O___O___O";
        System.out.println(GameSessionUtils.checkWinner(board) == 'O');

        board = "__X_X_X__";
        System.out.println(GameSessionUtils.checkWinner(board) == 'X');

        board = "XXX_O___O";
        System.out.println(GameSessionUtils.checkWinner(board) == 'X');

        board = "X_X__X__X";
        System.out.println(GameSessionUtils.checkWinner(board) == 'X');

        board = "X_X_____X";
        System.out.println(GameSessionUtils.checkWinner(board) == '_');
    }

    @Test
    void testValidToken(){
        String token = "d5a21065445efd460";
        System.out.println(userService.isTokenValid(token));
    }

    @Test
    void getGameSession(){
        String hostToken = "d5a21065445efd460";
        GameSession gameSession = gameSessionRepository.getByToken(hostToken);
        System.out.println(gameSession);
    }

    @Test
    void settingReady() throws TokenException {
        String host = userService.createUser("host");
        String guest = userService.createUser("guest");

        gameSessionService.createSession(host);
        gameSessionService.joinSession(host, guest);

        GameSession session = gameSessionRepository.getByToken(host);
        assert session.getStatus()  == GameStatus.NOT_READY;

        gameSessionService.setReady(guest);
        session = gameSessionRepository.getByToken(host);
        assert session.getStatus()  == GameStatus.HOST_NOT_READY;

        gameSessionService.setReady(host);
        session = gameSessionRepository.getByToken(host);
        assert session.getStatus()  == GameStatus.HOST_TURN;

        session = gameSessionRepository.getByToken(host);
        session.setStatus(GameStatus.NOT_READY);
        gameSessionRepository.update(session);

        gameSessionService.setReady(host);
        session = gameSessionRepository.getByToken(host);
        assert session.getStatus()  == GameStatus.GUEST_NOT_READY;

        gameSessionService.setReady(guest);
        session = gameSessionRepository.getByToken(host);
        assert session.getStatus()  == GameStatus.HOST_TURN;
    }
}
