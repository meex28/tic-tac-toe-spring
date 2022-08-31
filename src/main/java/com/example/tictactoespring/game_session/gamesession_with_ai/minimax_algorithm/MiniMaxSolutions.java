package com.example.tictactoespring.game_session.gamesession_with_ai.minimax_algorithm;

public class MiniMaxSolutions {
    public static String calculateNextMove(String board, int depth, char player, char onMove){
        Solution solution = new Solution(board, depth, player, onMove);
        return solution.calculateMoves();
    }
}
