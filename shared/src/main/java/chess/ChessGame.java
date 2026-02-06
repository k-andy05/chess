package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashSet;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {

    ChessBoard currentBoard = new ChessBoard(); // Initialize starting chessboard
    private TeamColor teamTurn = TeamColor.WHITE; // Initialize with white going first
    public ChessGame() {

    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return teamTurn;
    }

    /**
     * Set's which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        teamTurn = team;
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
        ChessPiece piece = currentBoard.getPiece(startPosition); // Get piece at starting Position
        if (piece == null) { // If empty spot, return null
            return null;
        }
        TeamColor pieceColor = piece.getTeamColor(); // else, get the team color
        if (this.isInCheckmate(pieceColor)) { // If in stalemate, then return an empty list
            return new ArrayList<>();
        }
        Collection<ChessMove> filteredMoves = piece.pieceMoves(currentBoard, startPosition); // Get raw moves list
        Iterator<ChessMove> filteredMovesIterator= filteredMoves.iterator(); // Create iterator for filtering
        while (filteredMovesIterator.hasNext()) { // While loop removes moves where check is involved
            ChessMove currentMove = filteredMovesIterator.next();
            if (isIllegal(currentMove, pieceColor)) {
                filteredMovesIterator.remove();
            }
        }
        return filteredMoves; // Updated possible moves list is returned
    }
    private boolean isIllegal(ChessMove move, TeamColor color) {
        boolean illegal = false;
        ChessBoard copyBoard = this.getBoard(); // Make copy of chessboard to work with
        ChessGame copyGame = new ChessGame();
        copyGame.setBoard(copyBoard); // Create new chess game copy to check for check condition
//        if (copyGame.isInCheck(color)) { // If king in check, perform move
//            copyGame.makeMove(move);
//        }
        // Check if already in check, then check if the move will save the king, if so, return false
        // Do move, if it makes king be in check, then return true


        return illegal;
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to perform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        ChessPosition start = move.getStartPosition();
        ChessPosition end = move.getEndPosition();
        ChessPiece piece = currentBoard.getPiece(start);
        Collection<ChessMove> validMoves = this.validMoves(start);
        if (!validMoves.contains(move)) {
            throw new InvalidMoveException();
        }
//        currentBoard.addPiece(end, null); // Make sure new spot is empty
        currentBoard.addPiece(end, piece); // Copy piece to new spot
        currentBoard.addPiece(start, null); // Set old spot to empty
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        boolean inCheck = false;
        ChessPosition kingPosition = getKingPosition(teamColor);
        Collection<ChessMove> kingMoves = currentBoard.getPiece(kingPosition).pieceMoves(currentBoard, kingPosition);
        Collection<ChessMove> attackerMoves = new ArrayList<>();
        Collection<ChessMove> friendlyMoves = new ArrayList<>();

        for (int col = 1; col <=8; col++) { // Populate two lists
            for (int row = 1; row <=8; row++) {
                ChessPosition position = new ChessPosition(row, col);
                ChessPiece piece = currentBoard.getPiece(position);
                if (piece != null) { // Check that there is a piece on the space
                    if (piece.getTeamColor() != teamColor) {
                        Collection<ChessMove> attackerPieceMoves = piece.pieceMoves(currentBoard, position);
                        attackerMoves.addAll(attackerPieceMoves); // Adds attacker moves to list (duplicates too)
                        // Add the ChessMove right before piece would hit the king
                    }
                    else if (piece.getPieceType() != ChessPiece.PieceType.KING && piece.getTeamColor() == teamColor) {
                        friendlyMoves.addAll(piece.pieceMoves(currentBoard, position)); // Adds teammates' moves to list (duplicates too)
                    }
                }
            }
        }

        return inCheck;







//        boolean inCheck = false;
//        // They may have duplicates, so use HashSet to remove duplicates later
//        Collection<ChessMove> attackerMoves = new ArrayList<>();
//        Collection<ChessMove> kingMoves = new ArrayList<>();
//        Collection<ChessMove> friendlyMoves = new ArrayList<>();
//        Collection<ChessMove> kingAttackTrajectory = new ArrayList<>();
//        ChessPosition kingPosition = null; //new ChessPosition(0,0); // Set out of bounds if not collected correctly later
//
//        for (int col = 1; col <=8; col++) { // Populate all three lists
//            for (int row = 1; row <=8; row++) {
//                ChessPosition position = new ChessPosition(row, col);
//                ChessPiece piece = currentBoard.getPiece(position);
//                if (piece != null) { // Check that there is a piece on the space
//                    if (piece.getTeamColor() != teamColor) {
//                        Collection<ChessMove> attackerPieceMoves = piece.pieceMoves(currentBoard, position);
//                        attackerMoves.addAll(attackerPieceMoves); // Adds attacker moves to list (duplicates too)
//                        // Add the ChessMove right before piece would hit the king
//                    }
//                    else if (piece.getPieceType() == ChessPiece.PieceType.KING && piece.getTeamColor() == teamColor) {
//                        kingMoves = piece.pieceMoves(currentBoard, position); // Populates king possible moves (doesn't include starting position)
//                        kingPosition = new ChessPosition(row, col);
//                    }
//                    else if (piece.getPieceType() != ChessPiece.PieceType.KING && piece.getTeamColor() == teamColor) {
//                        friendlyMoves.addAll(piece.pieceMoves(currentBoard, position)); // Adds teammates' moves to list (duplicates too)
//                    }
//                }
//            }
//        }
//        // Remove duplicates from lists
//        attackerMoves = new ArrayList<>(new LinkedHashSet<>(attackerMoves));
//        kingMoves = new ArrayList<>(new LinkedHashSet<>(kingMoves));
//        friendlyMoves = new ArrayList<>(new LinkedHashSet<>(friendlyMoves));
//
//        // King's position not in attacker's list => not in check nor checkmate
//        if (!attackerMoves.contains(kingPosition)) {
//            return inCheck;
//        }
//        // King can move to an open square => check, not checkmate
//        for (ChessMove kingMove : kingMoves) {
//            if (!attackerMoves.contains(kingMove)) {
//                inCheck = true;
//                return inCheck;
//            }
//        }
//        // Can friendly piece block all attacker's line of attack? NOT IMPLEMENTED BECAUSE THAT IS CHECKING FOR CHECKMATE
//
//        return inCheck;
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        throw new RuntimeException("Not implemented");
        // Create list of possible moves for king
        // Loop through entire board for all pieces
        // Make second list of possible enemy move spots
        // Make a third list of possible friendly movements

//        Collection<ChessMove> kingMoves =
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves while not in check.
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        throw new RuntimeException("Not implemented");
    }

    /**
     * Returns with current board's king spot of a team.
     *
     * @param teamColor the team who's king we're looking for
     */

    private ChessPosition getKingPosition(TeamColor teamColor) {
        ChessPosition kingPosition = null;
        for (int col = 1; col <=8; col++) {
            for (int row = 1; row <=8; row++) {
                ChessPiece piece = currentBoard.getPiece(new ChessPosition(row, col));
                if (piece != null && piece.getTeamColor() == teamColor && piece.getPieceType() == ChessPiece.PieceType.KING) {
                    kingPosition = new ChessPosition(row, col);
                }
            }
        }
        return kingPosition;
    }


    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */

    public void setBoard(ChessBoard board) {
        this.currentBoard = board;
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return this.currentBoard;
    }
}
