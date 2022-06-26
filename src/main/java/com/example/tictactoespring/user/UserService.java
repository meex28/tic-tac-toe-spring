package com.example.tictactoespring.user;

import com.example.tictactoespring.TokenException;
import com.google.common.hash.Hashing;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpServerErrorException;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Random;

@Service
public class UserService {
    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // generating string using User nickname, pseudo random number and current time
    // using SHA-256 algorithm
    private String generateToken(String nickname) throws TokenException {
        String now = (new Date()).toString();
        Random rand = new Random();
        String random = String.valueOf(rand.nextInt(100000000, 999999999));

        String token = Hashing.sha256()
                .hashString(nickname+now+random, StandardCharsets.UTF_8)
                .toString()
                .substring(0, 17);

        // check if token is unique
        int attempts = 5;
        boolean isUnique = isTokenUnique(token);

        while (!isUnique && attempts > 0){
            random = String.valueOf(rand.nextInt(100000000, 999999999));
            now = (new Date()).toString();
            token = Hashing.sha256()
                    .hashString(nickname+now+random, StandardCharsets.UTF_8)
                    .toString()
                    .substring(0, 16);
            isUnique = isTokenUnique(token);
            attempts--;
        }

        // if there was 5 attempts to generate then throw exception
        if(attempts == 0)
            throw new TokenException("Cannot generate unique token.");

        return token;
    }

    private boolean isTokenUnique(String token){
        User user = userRepository.get(token);
        return user == null;
    }

    public String createUser(String nickname) throws TokenException {
        String token = generateToken(nickname);
        User user = new User(token, nickname, new Date());

        userRepository.add(user);

        return user.getToken();
    }

    public void deleteUser(String token) {
        userRepository.delete(token);
    }

    public User getUserByToken(String token){
        return userRepository.get(token);
    }

    public boolean isTokenValid(String token){
        return userRepository.get(token) != null;
    }
}
