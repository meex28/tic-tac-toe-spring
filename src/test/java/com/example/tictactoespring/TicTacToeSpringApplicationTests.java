package com.example.tictactoespring;

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
}
