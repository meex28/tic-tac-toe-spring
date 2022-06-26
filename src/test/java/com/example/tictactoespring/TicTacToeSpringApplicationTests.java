package com.example.tictactoespring;

import com.example.tictactoespring.game_session.GameSessionUtils;
import com.example.tictactoespring.user.User;
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
}
