package com.example.tictactoespring.user;

import com.example.tictactoespring.TokenException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

@RestController
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/start")
    public String userStart(@RequestBody String nickname){
        System.out.println("Conn: "+nickname);

        // create new user and return token
        // if there is error return empty string instead of token
        String token = null;
        try{
            token = userService.createUser(nickname);
        }catch (TokenException e){
            token = "";
        }

        System.out.println(token);
        return token;
    }

    @PostMapping("/end")
    public void userEnd(@RequestBody String token){
        userService.deleteUser(token);
    }
}
