package com.example.tictactoespring.game_session.entities;

public enum GameStatus {
    WAITING_FOR_OPPONENT, NOT_READY, HOST_NOT_READY, GUEST_NOT_READY, OPPONENT_NOT_READY,
    HOST_TURN, GUEST_TURN, YOUR_TURN, OPPONENT_TURN,
    YOU_WON, OPPONENT_WON, DRAW,
    OPPONENT_LEFT, GAME_ENDED
}
