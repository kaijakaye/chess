package chess;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

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
        queenMoves.addAll(getBishopPieceMoves());
        queenMoves.addAll(getRookPieceMoves());
        return queenMoves;
    }

    public Collection<ChessMove> getBishopPieceMoves(){
        var bishopMoves = new HashSet<ChessMove>();

        // Diagonal directions: up-right, down-right, down-left, up-left
        int[][] directions = {
                {1, 1},
                {-1, 1},
                {-1, -1},
                {1, -1}
        };

        for (int[] dir : directions) {
            addDirectionalMoves(bishopMoves, dir[0], dir[1]);
        }

        return bishopMoves;
    }

    private void addDirectionalMoves(Set<ChessMove> moves, int rowDelta, int colDelta) {
        int changeByThisMuch = 1;

        while (true) {
            int newRow = position.getRow() + changeByThisMuch * rowDelta;
            int newCol = position.getColumn() + changeByThisMuch * colDelta;

            // Stop if out of bounds
            if (newRow < 1 || newRow > 8 || newCol < 1 || newCol > 8){
                break;
            }

            ChessPosition trial = new ChessPosition(newRow, newCol);
            ChessPiece occupant = board.getPiece(trial);

            if (occupant == null) {
                moves.add(new ChessMove(position, trial, null));
                changeByThisMuch++;
            } else {
                if (occupant.getTeamColor() != piece.getTeamColor()) {
                    moves.add(new ChessMove(position, trial, null));
                }
                break; // Stop at first occupied square
            }
        }
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

        // directions: up, right, down, left
        int[][] directions = {
                {1, 0},
                {0, 1},
                {-1, 0},
                {0, -1}
        };

        for (int[] dir : directions) {
            addDirectionalMoves(rookMoves, dir[0], dir[1]);
        }

        return rookMoves;
    }

    public Collection<ChessMove> getPawnPieceMoves() {
        var pawnMoves = new HashSet<ChessMove>();
        boolean isWhite = piece.getTeamColor() == ChessGame.TeamColor.WHITE;
        int direction = isWhite ? 1 : -1; // +1 for white, -1 for black
        int startRow = isWhite ? 2 : 7;
        int promoteRow = isWhite ? 8 : 1;

        ChessPosition[] trialsForward = {
                new ChessPosition(position.getRow() + direction, position.getColumn()),
                new ChessPosition(position.getRow() + 2 * direction, position.getColumn())
        };

        ChessPosition[] trialsDiag = {
                new ChessPosition(position.getRow() + direction, position.getColumn() - 1),
                new ChessPosition(position.getRow() + direction, position.getColumn() + 1)
        };

        // --- Diagonal captures ---
        for (ChessPosition trial : trialsDiag) {
            if (!isInBounds(trial)) continue;
            ChessPiece occupant = board.getPiece(trial);
            if (occupant == null) continue;
            if (occupant.getTeamColor() == piece.getTeamColor()) continue;

            if (trial.getRow() == promoteRow) {
                addPromotions(pawnMoves, position, trial);
            } else {
                pawnMoves.add(new ChessMove(position, trial, null));
            }
        }

        // --- Forward moves ---
        ChessPosition oneAhead = trialsForward[0];
        if (isInBounds(oneAhead) && board.getPiece(oneAhead) == null) {
            if (oneAhead.getRow() == promoteRow) {
                addPromotions(pawnMoves, position, oneAhead);
            } else {
                pawnMoves.add(new ChessMove(position, oneAhead, null));

                // double move from start
                if (position.getRow() == startRow) {
                    ChessPosition twoAhead = trialsForward[1];
                    if (isInBounds(twoAhead) && board.getPiece(twoAhead) == null) {
                        pawnMoves.add(new ChessMove(position, twoAhead, null));
                    }
                }
            }
        }

        return pawnMoves;
    }

    private boolean isInBounds(ChessPosition pos) {
        return pos.getRow() >= 1 && pos.getRow() <= 8 && pos.getColumn() >= 1 && pos.getColumn() <= 8;
    }

    private void addPromotions(Set<ChessMove> moves, ChessPosition from, ChessPosition to) {
        moves.add(new ChessMove(from, to, ChessPiece.PieceType.KNIGHT));
        moves.add(new ChessMove(from, to, ChessPiece.PieceType.QUEEN));
        moves.add(new ChessMove(from, to, ChessPiece.PieceType.ROOK));
        moves.add(new ChessMove(from, to, ChessPiece.PieceType.BISHOP));
    }


}
