package com.example.tictactoespring.game_session.gamesession_with_ai.minimax_algorithm;

import com.example.tictactoespring.game_session.GameSessionUtils;

import java.util.ArrayList;
import java.util.List;

public class Solution {
    private final String board;
    private int solutionQuality;
    private String boardAfterMove;
    private final int depth;
    private final char player;
    private final char onMove;

    public Solution(String board, int depth, char player, char onMove) {
        this.board = board;
        this.depth = depth;
        this.player = player;
        this.onMove = onMove;
    }

    public String calculateMoves(){
        if(checkBoardWinner()){
            this.boardAfterMove = this.board;
            return this.boardAfterMove;
        }
        List<Solution> nextMoves = new ArrayList<>();

        for(int i = 0; i<this.board.length(); ++i){
            if(this.board.charAt(i) == '_'){
                StringBuilder nextMoveBoard = new StringBuilder(this.board);
                nextMoveBoard.setCharAt(i, onMove);
                Solution newSolution = new Solution(nextMoveBoard.toString(), depth-1, player, nextPlayerOnMove());
                newSolution.calculateMoves();
                nextMoves.add(newSolution);
            }
        }

        this.solutionQuality = nextMoves.get(0).solutionQuality;
        this.boardAfterMove = nextMoves.get(0).board;

        nextMoves.forEach(solution ->{
            if(player == onMove){
                if(this.solutionQuality < solution.solutionQuality){
                    this.solutionQuality = solution.solutionQuality;
                    this.boardAfterMove = solution.board;
                }
            }else{
                if(this.solutionQuality > solution.solutionQuality){
                    this.solutionQuality = solution.solutionQuality;
                    this.boardAfterMove = solution.board;
                }
            }
        });

        return this.boardAfterMove;
    }

    private char nextPlayerOnMove(){
        return onMove == 'O' ? 'X' : 'O';
    }

    // check if there is already winner in this position
    // return 1 if player win, 0 if draw (or no result), -1 if opponent win
    private boolean checkBoardWinner(){
        char potentialWinner = GameSessionUtils.checkWinner(board);

        if(potentialWinner == '_' && depth > 0)
            return false;
        else if(potentialWinner == '_' && depth == 0){
            this.solutionQuality = 0;
            return true;
        }
        else if(potentialWinner == 'D'){
            this.solutionQuality = 0;
            return true;
        }else if(potentialWinner == player){
            this.solutionQuality = 1;
            return true;
        }else{
            this.solutionQuality = -1;
            return true;
        }
    }
}
