package com.example.tictactoespring.game_session;

public class GameSessionUtils {
    public static String setBoardField(int field, String board, char symbol){
        StringBuilder boardBuilder = new StringBuilder(board);
        boardBuilder.setCharAt(field, symbol);
        return boardBuilder.toString();
    }

    // check if there is a win in given board
    // returns:
    // O - host win
    // X - guest win
    // _ - no result
    // D - draw
    public static char checkWinner(String board){
        // Board indexes:
        // 0 1 2
        // 3 4 5
        // 6 7 8

        for(int i = 0; i<3; i++){
            // check if there is a win in row
            if(board.charAt(i*3) == board.charAt(i*3+1) && board.charAt(i*3) == board.charAt(i*3+2) && board.charAt(i*3) != '_')
                return board.charAt(i*3);

            // check if there is a win in column
            if(board.charAt((i)) == board.charAt(i+3) && board.charAt(i) == board.charAt(i+6) && board.charAt(i) != '_')
                return board.charAt(i);
        }

        // check diagonals
        if(board.charAt(0) == board.charAt(4) && board.charAt(0) == board.charAt(8) && board.charAt(0) != '_')
            return board.charAt(0);

        if(board.charAt(2) == board.charAt(4) && board.charAt(2) == board.charAt(6) && board.charAt(2) != '_')
            return board.charAt(2);

        // check if there is a draw
        for(int i = 0; i<board.length(); ++i){
            if(board.charAt(i) == '_')
                return '_';
        }

        return 'D';
    }
}

