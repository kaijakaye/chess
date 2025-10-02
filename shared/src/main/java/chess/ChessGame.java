package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {

    private TeamColor whoseTurn;
    private ChessBoard board = new ChessBoard();

    public ChessGame() {
        whoseTurn = TeamColor.WHITE;
    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return whoseTurn;
    }

    /**
     * Set's which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        whoseTurn = team;
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }

    /**
     * Gets a valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        ChessPiece current = board.getPiece(startPosition);
        if(current==null) return null;
        //valid moves BEFORE taking out ones that would put the king in check
        Collection<ChessMove> rawValids = current.pieceMoves(board,startPosition);
        Collection<ChessMove> finalValids = new ArrayList<>(rawValids);

        //you'll put this one away
        ChessBoard originalCopy = board.clone();

        for(ChessMove move : rawValids){
            //preserve pieces that could get captured to restore at the end
            ChessPiece moving = board.getPiece(move.getStartPosition());
            ChessPiece captured = board.getPiece(move.getEndPosition());

            //"move" the piece on the test board first
            board.addPiece(move.getEndPosition(),moving);
            //"clear" the old spot by adding a null piece
            board.addPiece(move.getStartPosition(),null);

            if(isInCheck(current.getTeamColor())){
                finalValids.remove(move);
            }

            //get the board back to normal for the next loop
            board.addPiece(move.getStartPosition(),moving);
            board.addPiece(move.getEndPosition(),captured);
        }
        //make sure board is unchanged
        setBoard(originalCopy);
        return finalValids;

    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to perform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {

        //making sure there's a piece there
        if(board.getPiece(move.getStartPosition())==null){
            throw new InvalidMoveException("Invalid move");
        }

        //making sure it's the right team
        if(board.getPiece(move.getStartPosition()).getTeamColor()!=whoseTurn){
            throw new InvalidMoveException("Invalid move");
        }

        Collection<ChessMove> validMoves = validMoves(move.getStartPosition());

        var didTheMoveHappen = false;
        //checking to make sure that the given move is within the list of valid moves for that piece
        for(ChessMove trialMove : validMoves){
            //if it is a valid move
            if(trialMove.equals(move)){
                ChessPiece atSpot = board.getPiece(move.getStartPosition());
                if(move.getPromotionPiece()!=null){
                    board.addPiece(move.getEndPosition(),new ChessPiece(atSpot.getTeamColor(),move.getPromotionPiece()));
                    board.addPiece(move.getStartPosition(),null);
                    didTheMoveHappen = true;
                }
                else {
                    //"move" the piece on the test board first
                    board.addPiece(move.getEndPosition(), board.getPiece(move.getStartPosition()));
                    //"clear" the old spot by adding a null piece
                    board.addPiece(move.getStartPosition(), null);

                    didTheMoveHappen = true;
                }
            }
        }

        //if the move never got implemented
        if(!didTheMoveHappen){
            throw new InvalidMoveException("Invalid move");
        }
        //if the move did happen, switch team color
        else{
            if(whoseTurn==TeamColor.WHITE){
                whoseTurn = TeamColor.BLACK;
            }
            else{
                whoseTurn = TeamColor.WHITE;
            }
        }

    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        //get king's position
        var breakFromOuterLoop = false;
        ChessPosition kingPos = null;
        for(int rowCounter = 1; rowCounter < 9; rowCounter++){
            if(breakFromOuterLoop){
                break;
            }
            for(int colCounter = 1; colCounter < 9; colCounter++){
                ChessPosition currentPos = new ChessPosition(rowCounter,colCounter);
                ChessPiece piece = board.getPiece(currentPos);
                if(piece!=null) {
                    if (piece.getPieceType() == ChessPiece.PieceType.KING && piece.getTeamColor() == teamColor) {
                        kingPos = currentPos;
                        breakFromOuterLoop = true;
                        break;
                    }
                }
            }
        }

        for(int rowCounter = 1; rowCounter < 9; rowCounter++) {
            for (int colCounter = 1; colCounter < 9; colCounter++) {
                ChessPosition currentPos = new ChessPosition(rowCounter,colCounter);
                ChessPiece piece = board.getPiece(currentPos);
                if(piece!=null) {

                    //if we're dealing with the white King
                    if (teamColor == TeamColor.WHITE) {
                        //if the piece we're looking at is black
                        if (piece.getTeamColor() == TeamColor.BLACK) {
                            Collection<ChessMove> valids = piece.pieceMoves(board,currentPos);
                            for (ChessMove trial : valids) {
                                if (trial.getEndPosition().equals(kingPos)) {
                                    return true;
                                }
                            }
                        }
                    }
                    //if king is black
                    else {
                        //if the piece we're looking at is white
                        if (piece.getTeamColor() == TeamColor.WHITE) {
                            Collection<ChessMove> valids = piece.pieceMoves(board,currentPos);
                            for (ChessMove trial : valids) {
                                if (trial.getEndPosition().equals(kingPos)) {
                                    return true;
                                }
                            }
                        }
                    }
                }
            }
        }
        return false;

    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        Collection<ChessMove> overallMoves = new ArrayList<>();
        for(int rowCounter = 1; rowCounter < 9; rowCounter++) {
            for (int colCounter = 1; colCounter < 9; colCounter++) {
                ChessPosition currentPos = new ChessPosition(rowCounter,colCounter);
                ChessPiece piece = board.getPiece(currentPos);
                if(piece!=null) {
                    if(piece.getTeamColor()==teamColor){
                        Collection<ChessMove> pmoves = validMoves(currentPos);
                        overallMoves.addAll(pmoves);
                    }
                }
            }
        }
        return isInCheck(teamColor) && overallMoves.isEmpty();
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves while not in check.
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        /*get king's position
        var breakFromOuterLoop = false;
        ChessPosition kingPos = null;
        for(int rowCounter = 1; rowCounter < 9; rowCounter++){
            if(breakFromOuterLoop){
                break;
            }
            for(int colCounter = 1; colCounter < 9; colCounter++){
                ChessPosition currentPos = new ChessPosition(rowCounter,colCounter);
                ChessPiece piece = board.getPiece(currentPos);
                if(piece!=null) {
                    if (piece.getPieceType() == ChessPiece.PieceType.KING && piece.getTeamColor() == teamColor) {
                        kingPos = currentPos;
                        breakFromOuterLoop = true;
                        break;
                    }
                }
            }
        }

        Collection<ChessMove> kingMoves = board.getPiece(kingPos).pieceMoves(board,kingPos);

        return (!isInCheck(teamColor)) && (!kingMoves.isEmpty());*/
        return false;
    }

    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        this.board = board;
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return board;
    }
}
