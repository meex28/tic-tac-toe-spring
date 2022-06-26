package com.example.tictactoespring.game_session;

import com.example.tictactoespring.user.User;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
public class GameSession {
    @Getter
    @Setter
    @Id
    @GeneratedValue
    private int id;

    @Getter
    @Setter
    @OneToOne(optional = false)
    @JoinColumn(name="host_token", referencedColumnName = "token")
    private User host;

    @Getter
    @Setter
    @OneToOne(optional = true)
    @JoinColumn(name="guest_token", referencedColumnName = "token")
    private User guest;


    // on board host is always O and guest is X symbol.
    // empty field is '_'
    @Getter
    @Setter
    private String board;

    @Getter
    @Setter
    private int hostResult;

    @Getter
    @Setter
    private int guestResult;

    @Getter
    @Setter
    private GameStatus status;

    @Getter
    @Setter
    private boolean isAI;

    public GameSession(){}

    public GameSession(User host){
        this.host = host;
        this.board = "_________";
        this.hostResult = 0;
        this.guestResult = 0;
        this.status = GameStatus.WAITING_FOR_OPPONENT;
        this.isAI = false;
    }
}
