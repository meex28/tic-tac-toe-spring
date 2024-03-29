package com.example.tictactoespring.game_session.entities;

import com.example.tictactoespring.user.User;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
public class GameSession {
    private final String startingBoard = "_________";

    @Getter
    @Setter
    @Id
    @GeneratedValue
    private int id;

    @Getter
    @Setter
    @OneToOne(optional = false, targetEntity = User.class)
    private User host;

    @Getter
    @Setter
    @OneToOne(optional = true, targetEntity = User.class)
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

    @Getter
    @Setter
    private int playedGames;

    public void incrementHostResult(){
        this.hostResult++;
    }

    public void incrementGuestResult(){
        this.guestResult++;
    }

    public void addPlayedGame(){
        this.playedGames++;
    }

    public void startGame(){
        this.status = (this.playedGames % 2 == 0) ? GameStatus.HOST_TURN : GameStatus.GUEST_TURN;
        this.board = startingBoard;
    }

    public void switchTurn(){
        if(this.status == GameStatus.HOST_TURN)
            this.status = GameStatus.GUEST_TURN;
        else if(this.status == GameStatus.GUEST_TURN)
            this.status = GameStatus.HOST_TURN;
    }

    public void initialize(){
        this.status = GameStatus.NOT_READY;
        this.board = startingBoard;
        this.hostResult = 0;
        this.guestResult = 0;
        this.playedGames = 0;
    }

    public GameSession(){}

    public GameSession(User host){
        this.host = host;
        this.board = startingBoard;
        this.hostResult = 0;
        this.guestResult = 0;
        this.status = GameStatus.WAITING_FOR_OPPONENT;
        this.isAI = false;
    }
}
