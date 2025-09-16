package chess;

import java.util.Collection;
import java.util.HashSet;

public class PieceMovesCalculator {

    ChessBoard board;
    ChessPosition position;
    ChessPiece piece;

    //overloaded constructor
    public PieceMovesCalculator(ChessBoard theBoard, ChessPosition thePosition) {
        board = theBoard;
        position = thePosition;
        piece = board.getPiece(position);
    }

    public Collection<ChessMove> pieceMoves(){
        var moves = new HashSet<ChessMove>();

        //sorting out which algorithm to use
        if(piece.getPieceType()== ChessPiece.PieceType.KING){
            moves = (HashSet<ChessMove>) getKingPieceMoves();
        }
        else if(piece.getPieceType()== ChessPiece.PieceType.QUEEN){
            moves = (HashSet<ChessMove>) getQueenPieceMoves();
        }
        else if(piece.getPieceType()== ChessPiece.PieceType.BISHOP){
            moves = (HashSet<ChessMove>) getBishopPieceMoves();
        }
        else if(piece.getPieceType()== ChessPiece.PieceType.KNIGHT){
            moves = (HashSet<ChessMove>) getKnightPieceMoves();
        }
        else if(piece.getPieceType()== ChessPiece.PieceType.ROOK){
            moves = (HashSet<ChessMove>) getRookPieceMoves();
        }
        else if(piece.getPieceType()== ChessPiece.PieceType.PAWN){
            moves = (HashSet<ChessMove>) getPawnPieceMoves();
        }

        return moves;

    }

    public Collection<ChessMove> getKingPieceMoves(){
        var kingMoves = new HashSet<ChessMove>();

        //make a trial array of each potential position the king can move to
        ChessPosition[] trials = new ChessPosition[8];

        //above
        trials[0] = new ChessPosition(position.getRow()+1,position.getColumn());
        //above right
        trials[1] = new ChessPosition(position.getRow()+1,position.getColumn()+1);
        //right
        trials[2] = new ChessPosition(position.getRow(),position.getColumn()+1);
        //below right
        trials[3] = new ChessPosition(position.getRow() - 1,position.getColumn()+1);
        //below
        trials[4] = new ChessPosition(position.getRow() - 1,position.getColumn());
        //below left
        trials[5] = new ChessPosition(position.getRow()-1,position.getColumn()-1);
        //left
        trials[6] = new ChessPosition(position.getRow(),position.getColumn()-1);
        //above left
        trials[7] = new ChessPosition(position.getRow()+1,position.getColumn()-1);

        //make a for each loop to test each trial position
        for(ChessPosition trial : trials){
            //making sure it's in bounds
            if(trial.getRow()<9 && trial.getColumn()<9 && trial.getRow()>0 && trial.getColumn()>0){
                //if the spot is empty
                if(board.getPiece(trial)==null){
                    kingMoves.add(new ChessMove(position, trial, null));
                }
                else{
                    ChessPiece occupant = board.getPiece(trial);
                    //if the two pieces are on different teams
                    if(occupant.getTeamColor()!=piece.getTeamColor()){
                        kingMoves.add(new ChessMove(position, trial, null));
                    }
                }
            }
        }
        return kingMoves;
    }

    public Collection<ChessMove> getQueenPieceMoves(){
        var queenMoves = new HashSet<ChessMove>();
        return queenMoves;
    }

    public Collection<ChessMove> getBishopPieceMoves(){
        var bishopMoves = new HashSet<ChessMove>();
        return bishopMoves;
    }

    public Collection<ChessMove> getKnightPieceMoves(){
        var knightMoves = new HashSet<ChessMove>();

        //make a trial array of each potential position the king can move to
        ChessPosition[] trials = new ChessPosition[8];

        //above
        trials[0] = new ChessPosition(position.getRow()+2,position.getColumn()+1);
        //above right
        trials[1] = new ChessPosition(position.getRow()+1,position.getColumn()+2);
        //right
        trials[2] = new ChessPosition(position.getRow()-1,position.getColumn()+2);
        //below right
        trials[3] = new ChessPosition(position.getRow() - 2,position.getColumn()+1);
        //below
        trials[4] = new ChessPosition(position.getRow() - 2,position.getColumn()-1);
        //below left
        trials[5] = new ChessPosition(position.getRow()-1,position.getColumn()-2);
        //left
        trials[6] = new ChessPosition(position.getRow()+1,position.getColumn()-2);
        //above left
        trials[7] = new ChessPosition(position.getRow()+2,position.getColumn()-1);

        //make a for each loop to test each trial position
        for(ChessPosition trial : trials){
            //making sure it's in bounds
            if(trial.getRow()<9 && trial.getColumn()<9 && trial.getRow()>0 && trial.getColumn()>0){
                //if the spot is empty
                if(board.getPiece(trial)==null){
                    knightMoves.add(new ChessMove(position, trial, null));
                }
                else{
                    ChessPiece occupant = board.getPiece(trial);
                    //if the two pieces are on different teams
                    if(occupant.getTeamColor()!=piece.getTeamColor()){
                        knightMoves.add(new ChessMove(position, trial, null));
                    }
                }
            }
        }
        return knightMoves;
    }

    public Collection<ChessMove> getRookPieceMoves(){
        var rookMoves = new HashSet<ChessMove>();

        //make a while loop for each of the four directions
        //up loop
        var changeByThisMuch = 1;
        var shouldIBreak = false;
        while(!shouldIBreak){

            ChessPosition trial = new ChessPosition(position.getRow()+changeByThisMuch,position.getColumn());
            //making sure it's in bounds
            if(trial.getRow()<9 && trial.getColumn()<9 && trial.getRow()>0 && trial.getColumn()>0){
                //if the spot is empty
                if(board.getPiece(trial)==null){
                    rookMoves.add(new ChessMove(position, trial, null));
                    ++changeByThisMuch;
                }
                else{
                    ChessPiece occupant = board.getPiece(trial);
                    //if the two pieces are on different teams
                    if(occupant.getTeamColor()!=piece.getTeamColor()){
                        rookMoves.add(new ChessMove(position, trial, null));
                    }
                    //regardless of what kind of piece it is, the loop ends here
                    shouldIBreak = true;
                }
            }
            else{
                shouldIBreak = true;
            }
        }

        //right loop
        changeByThisMuch = 1;
        shouldIBreak = false;
        while(!shouldIBreak){

            ChessPosition trial = new ChessPosition(position.getRow(),position.getColumn() + changeByThisMuch);
            //making sure it's in bounds
            if(trial.getRow()<9 && trial.getColumn()<9 && trial.getRow()>0 && trial.getColumn()>0){
                //if the spot is empty
                if(board.getPiece(trial)==null){
                    rookMoves.add(new ChessMove(position, trial, null));
                    ++changeByThisMuch;
                }
                else{
                    ChessPiece occupant = board.getPiece(trial);
                    //if the two pieces are on different teams
                    if(occupant.getTeamColor()!=piece.getTeamColor()){
                        rookMoves.add(new ChessMove(position, trial, null));
                    }
                    //regardless of what kind of piece it is, the loop ends here
                    shouldIBreak = true;
                }
            }
            else{
                shouldIBreak = true;
            }
        }

        //down loop
        changeByThisMuch = 1;
        shouldIBreak = false;
        while(!shouldIBreak){

            ChessPosition trial = new ChessPosition(position.getRow() - changeByThisMuch,position.getColumn());
            //making sure it's in bounds
            if(trial.getRow()<9 && trial.getColumn()<9 && trial.getRow()>0 && trial.getColumn()>0){
                //if the spot is empty
                if(board.getPiece(trial)==null){
                    rookMoves.add(new ChessMove(position, trial, null));
                    ++changeByThisMuch;
                }
                else{
                    ChessPiece occupant = board.getPiece(trial);
                    //if the two pieces are on different teams
                    if(occupant.getTeamColor()!=piece.getTeamColor()){
                        rookMoves.add(new ChessMove(position, trial, null));
                    }
                    //regardless of what kind of piece it is, the loop ends here
                    shouldIBreak = true;
                }
            }
            else{
                shouldIBreak = true;
            }
        }

        //left loop
        changeByThisMuch = 1;
        shouldIBreak = false;
        while(!shouldIBreak){

            ChessPosition trial = new ChessPosition(position.getRow(),position.getColumn() - changeByThisMuch);
            //making sure it's in bounds
            if(trial.getRow()<9 && trial.getColumn()<9 && trial.getRow()>0 && trial.getColumn()>0){
                //if the spot is empty
                if(board.getPiece(trial)==null){
                    rookMoves.add(new ChessMove(position, trial, null));
                    ++changeByThisMuch;
                }
                else{
                    ChessPiece occupant = board.getPiece(trial);
                    //if the two pieces are on different teams
                    if(occupant.getTeamColor()!=piece.getTeamColor()){
                        rookMoves.add(new ChessMove(position, trial, null));
                    }
                    //regardless of what kind of piece it is, the loop ends here
                    shouldIBreak = true;
                }
            }
            else{
                shouldIBreak = true;
            }
        }

        return rookMoves;
    }

    public Collection<ChessMove> getPawnPieceMoves(){
        var pawnMoves = new HashSet<ChessMove>();
        return pawnMoves;
    }

}
