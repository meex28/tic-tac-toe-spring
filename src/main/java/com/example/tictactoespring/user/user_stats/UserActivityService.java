package com.example.tictactoespring.user.user_stats;

import com.example.tictactoespring.TokenException;
import com.example.tictactoespring.game_session.GameSessionRepository;
import com.example.tictactoespring.game_session.GameSessionService;
import com.example.tictactoespring.game_session.MessagingService;
import com.example.tictactoespring.game_session.entities.GameSession;
import com.example.tictactoespring.user.User;
import com.example.tictactoespring.user.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

@Component
@Configuration
@EnableScheduling
public class UserActivityService {
    private final UserRepository userRepository;
    private final GameSessionService gameSessionService;
    private final Logger logger = LoggerFactory.getLogger(UserActivityService.class);

    @Autowired
    public UserActivityService(UserRepository userRepository,
                               GameSessionService gameSessionService) {
        this.userRepository = userRepository;
        this.gameSessionService = gameSessionService;
    }

    @Async
    @EventListener
    public void handleUserActivity(UserActivityEvent event){
        String token = event.getToken();

        User user = userRepository.get(token);

        if(user != null){
            Date date = new Date(event.getTimestamp());
            user.setLastActivity(date);
            userRepository.update(user);
        }
    }

    // scheduled every 15 minutes
    @Scheduled(fixedDelay = 1000*60*15)
    public void deleteInactiveUsers(){
        logger.info("Deleting inactive users");

        // create Date object of current time - 30 minutes
        Date cutOffDate = new Date(System.currentTimeMillis() - 1800 * 1000);
        List<User> inactiveUsers = userRepository.getUsersByLastActivityAfterDate(cutOffDate);
        inactiveUsers.forEach(this::deleteUser);
    }

    private void deleteUser(User user){
        try{
            gameSessionService.leaveSession(user.getToken());
        }catch (TokenException ignored){}

        userRepository.delete(user);
    }
}
