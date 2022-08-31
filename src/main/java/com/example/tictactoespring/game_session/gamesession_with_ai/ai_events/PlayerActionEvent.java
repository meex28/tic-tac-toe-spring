package com.example.tictactoespring.game_session.gamesession_with_ai.ai_events;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

public class PlayerActionEvent extends ApplicationEvent{
    @Getter
    private final String token;

    public PlayerActionEvent(Object source, String token) {
        super(source);
        this.token = token;
    }
}
