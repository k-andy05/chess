package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Objects;

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
        currentBoard.resetBoard();
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

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessGame chessGame = (ChessGame) o;
        return Objects.equals(currentBoard, chessGame.currentBoard) && teamTurn == chessGame.teamTurn;
    }

    @Override
    public int hashCode() {
        return Objects.hash(currentBoard, teamTurn);
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
        if (piece == null) {
            return null;
        }
        ChessPosition kingPosition = this.getKingPosition(piece.getTeamColor(), currentBoard); // King location
        Collection<ChessMove> pieceMoves = piece.pieceMoves(currentBoard, startPosition);

        // Check pieceMoves to see if each attackerList generated after a move puts the king's location in there
        Iterator<ChessMove> pieceMovesIterator = pieceMoves.iterator();
        while (pieceMovesIterator.hasNext()) {
            ChessMove move = pieceMovesIterator.next();
            Collection<ChessMove> attackerMoves = new ArrayList<>();
            ChessGame copyGame = new ChessGame(); // New game
            ChessBoard copyBoard = this.deepCopy(currentBoard);
            copyGame.setBoard(copyBoard); // Set board to copy of original game's board
            // Do the "move"
            copyGame.currentBoard.addPiece(move.getEndPosition(), null);
            if (move.getPromotionPiece() == null) {
                copyGame.currentBoard.addPiece(move.getEndPosition(), piece);
            }
            else {
                copyGame.currentBoard.addPiece(move.getEndPosition(), new ChessPiece(piece.getTeamColor(), move.getPromotionPiece()));
            }
            copyGame.currentBoard.addPiece(move.getStartPosition(), null);
            attackerMoves = copyGame.getAttackerMoves(piece.getTeamColor(), copyGame.currentBoard); // Generate updated attackerMove list
            ChessPosition newKingPosition = copyGame.getKingPosition(piece.getTeamColor(), copyGame.currentBoard);
            for (ChessMove attackerMove : attackerMoves) {
                if (attackerMove.getEndPosition().equals(newKingPosition)) {
                    pieceMovesIterator.remove();
                    break;
                }
            }
        }
        return pieceMoves;
    }

    private ChessBoard deepCopy(ChessBoard original) {
        ChessBoard copy = new ChessBoard();

        for (int row = 1; row <= 8; row++) {
            for (int col = 1; col <= 8; col++) {
                ChessPosition pos = new ChessPosition(row, col);
                ChessPiece piece = original.getPiece(pos);
                // If there is a piece, put it on the new board
                if (piece != null) {
                    copy.addPiece(pos, piece);
                }
            }
        }
        return copy;
    }


    /**
     * Returns an enemy's list of possible moves
     *
     * @param teamColor not attacker team
     * @param board ChessBoard object
     */
    private Collection<ChessMove> getAttackerMoves(TeamColor teamColor, ChessBoard board) {
        Collection<ChessMove> attackerMoves = new ArrayList<>();
        for (int row = 1; row <= 8; row++) {
            for (int col = 1; col <= 8; col++) {
                ChessPosition position = new ChessPosition(row, col);
                ChessPiece piece = board.getPiece(position);
                if (piece != null && piece.getTeamColor() != teamColor) {
                    attackerMoves.addAll(piece.pieceMoves(board, position));
                }
            }
        }
        return attackerMoves;
    }

    /**
     * Returns a king's position
     *
     * @param teamColor not attacker team
     * @param board ChessBoard object
     */
    private ChessPosition getKingPosition(TeamColor teamColor, ChessBoard board) {
        for (int row = 1; row <= 8; row++) {
            for (int col = 1; col <= 8; col++) {
                ChessPosition position = new ChessPosition(row, col);
                ChessPiece piece = board.getPiece(position);
                if (piece != null && piece.getTeamColor() == teamColor && piece.getPieceType() == ChessPiece.PieceType.KING) {
                    return position;
                }
            }
        }
        return null;
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
        ChessPiece.PieceType promotion = move.getPromotionPiece();
        ChessPiece piece = currentBoard.getPiece(start);
        Collection<ChessMove> validMoves = this.validMoves(start);


        if (piece == null || !validMoves.contains(move) || piece.getTeamColor() != teamTurn) {
            throw new InvalidMoveException();
        }
        currentBoard.addPiece(end, null); // Make sure new spot is empty
        if (promotion == null) {
            currentBoard.addPiece(end, piece);
        }
        else {
            currentBoard.addPiece(end, new ChessPiece(piece.getTeamColor(), promotion)); // Copy piece to new spot
        }
        currentBoard.addPiece(start, null); // Set old spot to empty
        if (teamTurn == TeamColor.BLACK) {
            teamTurn = TeamColor.WHITE;
        }
        else if (teamTurn == TeamColor.WHITE) {
            teamTurn = TeamColor.BLACK;
        }
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        Collection<ChessMove> attackerMoves = this.getAttackerMoves(teamColor, currentBoard);
        Collection<ChessPosition> attackerEndPositions = this.getEndPositions(attackerMoves);
        ChessPosition kingPosition = this.getKingPosition(teamColor, currentBoard);

        return attackerEndPositions.contains(kingPosition);
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        if (!isInCheck(teamColor)) {
            return false;
        }
        if (teamTurn != teamColor) {
            return false;
        }
//            for (int row=1; row<=8; row++) {
//                for (int col = 1; col <= 8; col++) {
//                    ChessPosition position = new ChessPosition(row, col);
//                    ChessPiece piece = currentBoard.getPiece(position);
//
//                    if (piece != null && piece.getTeamColor() == teamColor) {
//                        if (!validMoves(position).isEmpty()) {
//                            return false;
//                        }
//                    }
//                }
//            }

//            for (int row=1; row<=8; row++) {
//                for (int col=1; col<=8; col++) {
//                    ChessPosition position = new ChessPosition(row, col);
//                    ChessPiece piece = currentBoard.getPiece(position);
//                    if (piece != null && piece.getTeamColor() == teamColor) {
//                        // Allow for when pawns reach the end and become something else
//                        if (!this.validMoves(position).isEmpty()) {
//                            return false;
//                        }
//                    }
//                }
//            }
//            return true;
        return true;
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves while not in check.
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        if (this.teamTurn == teamColor && !isInCheck(teamColor)) { // Check that it is our turn and we aren't in check
            for (int row = 1; row <= 8; row++) {
                for (int col = 1; col <= 8; col++) {
                    ChessPosition position = new ChessPosition(row, col);
                    ChessPiece piece = currentBoard.getPiece(position);
                    if (piece != null && piece.getTeamColor() == teamColor) { // Look at all our team's remaining pieces
                        if (!validMoves(position).isEmpty()) { // If they also have zero valid moves...
                            return false;
                        }
                    }
                }
            }
            return true;
        }
        return false;
    }

    /**
     * Returns with current board's king spot of a team.
     *
     * @param teamColor the team who's king we're looking for
     */


    /**
     * Returns with a list of end positions (ChessPosition).
     *
     * @param movesList a list of ChessMove objects.
     */

    private Collection<ChessPosition> getEndPositions(Collection<ChessMove> movesList) {
        Collection<ChessPosition> endPositions = new ArrayList<>();
        for (ChessMove move : movesList) {
            endPositions.add(move.getEndPosition());
        }
        return endPositions;
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

    /**
     * Returns a list of moves where the attacker is one spot before hitting the king
     *
     * @param attackerMoves list of all possible attacker moves
     * @param kingPosition ChessPosition of current team's king
     */
}
