package com.example.tictactoespring.game_session;

import lombok.Getter;
import lombok.Setter;

public class GameSessionDTO {
    @Getter
    @Setter
    private String opponent;

    @Getter
    @Setter
    private int ownResult;

    @Getter
    @Setter
    private int opponentResult;

    @Getter
    @Setter
    private GameStatus status;

    @Getter
    @Setter
    private String board;

    public GameSessionDTO() {
    }

    public GameSessionDTO(String opponent, int ownResult, int opponentResult, GameStatus status, String board) {
        this.opponent = opponent;
        this.ownResult = ownResult;
        this.opponentResult = opponentResult;
        this.status = status;
        this.board = board;
    }
}
