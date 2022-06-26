package com.example.tictactoespring.game_session;

public enum GameStatus {
    WAITING_FOR_OPPONENT, NOT_READY, HOST_NOT_READY, OPPONENT_NOT_READY,
    HOST_TURN, GUEST_TURN,
    YOU_WON, OPPONENT_WON, DRAW
}
