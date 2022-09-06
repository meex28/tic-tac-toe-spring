package com.example.tictactoespring.user.user_stats;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

public class UserActivityEvent extends ApplicationEvent {
    @Getter
    private final String token;

    public UserActivityEvent(Object source, String token) {
        super(source);
        this.token = token;
    }
}
