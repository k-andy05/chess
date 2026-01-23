package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {

    private final ChessGame.TeamColor pieceColor;
    private final PieceType type;

    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
        this.pieceColor = pieceColor;
        this.type = type;
    }

    /**
     * The various different chess piece options
     */
    public enum PieceType {
        KING,
        QUEEN,
        BISHOP,
        KNIGHT,
        ROOK,
        PAWN
    }

    /**
     * @return Which team this chess piece belongs to
     */
    public ChessGame.TeamColor getTeamColor() {
        return pieceColor;
    }

    /**
     * @return which type of chess piece this piece is
     */
    public PieceType getPieceType() {
        return type;
    }

    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        ChessPiece piece = board.getPiece(myPosition);
        List<ChessMove> possibleMoves = new ArrayList<>();
        int startingX = myPosition.getColumn();
        int startingY = myPosition.getRow();


        if (piece.getPieceType() == PieceType.BISHOP) {
            // Check top right
            int x = startingX + 1;
            int y = startingY + 1;
            while (x <= 8 && y <= 8) {
                // MAKE SURE ChessPosition(y, x) not (x, y)
                ChessPiece newSpot = board.getPiece(new ChessPosition(y, x));
                if (newSpot == null) { // Check if space is empty
                    possibleMoves.add(new ChessMove(new ChessPosition(startingY, startingX), new ChessPosition(y, x), null)); // If empty, add to list of possible moves
                    // Increment x and y
                    x++;
                    y++;
                }
                // If not empty, check team type
                else if (newSpot.getTeamColor() != pieceColor) { // (May have to put this.pieceColor) If they are not our team, add final spot to list before exiting
                    possibleMoves.add(new ChessMove(new ChessPosition(startingY, startingX), new ChessPosition(y, x), null));
                    // Set x and y to 9 so it exits while loop
                    x = 9;
                    y = 9;
                }
                else { // Piece in the way is on our team and we exit while loop
                    x = 9;
                    y = 9;
                }
            }
            // Check top left
            x = startingX - 1;
            y = startingY + 1;
            while (x >= 1 && y <= 8) {
                ChessPiece newSpot = board.getPiece(new ChessPosition(y, x));
                if (newSpot == null) { // Is potential free space empty... if so, add to list
                    possibleMoves.add(new ChessMove(new ChessPosition(startingY, startingX), new ChessPosition(y, x), null));
                    x--;
                    y++;
                }
                else if (newSpot.getTeamColor() != pieceColor) { // Is piece in the way an opponent... if so, add to list then exit loop
                    possibleMoves.add(new ChessMove(new ChessPosition(startingY, startingX), new ChessPosition(y, x), null));
                    x = 0;
                    y = 9;
                }
                else { // Assuming piece in the way is on our team... exit loop
                    x = 0;
                    y = 9;
                }
            }
            // Check bottom left
            x = startingX - 1;
            y = startingY - 1;
            while (x >= 1 && y >= 1) {
                ChessPiece newSpot = board.getPiece(new ChessPosition(y, x));
                if (newSpot == null) {
                    possibleMoves.add(new ChessMove(new ChessPosition(startingY, startingX), new ChessPosition(y, x), null));
                    x--;
                    y--;
                }
                else if (newSpot.getTeamColor() != pieceColor) { // Is piece in the way an opponent... if so, add to list then exit loop
                    possibleMoves.add(new ChessMove(new ChessPosition(startingY, startingX), new ChessPosition(y, x), null));
                    x = 0;
                    y = 0;
                }
                else { // Assuming piece in the way is on our team... exit loop
                    x = 0;
                    y = 0;
                }
            }
            // Check bottom right
            x = startingX + 1;
            y = startingY - 1;
            while (x >= 1 && y >= 1) {
                ChessPiece newSpot = board.getPiece(new ChessPosition(y, x));
                if (newSpot == null) {
                    possibleMoves.add(new ChessMove(new ChessPosition(startingY, startingX), new ChessPosition(y, x), null));
                    x++;
                    y--;
                }
                else if (newSpot.getTeamColor() != pieceColor) { // Is piece in the way an opponent... if so, add to list then exit loop
                    possibleMoves.add(new ChessMove(new ChessPosition(startingY, startingX), new ChessPosition(y, x), null));
                    x = 9;
                    y = 0;
                }
                else { // Assuming piece in the way is on our team... exit loop
                    x = 9;
                    y = 0;
                }
            }
        }


        else if (piece.getPieceType() == PieceType.KING) {
            // Top left
            if ((startingY + 1) > 0 && (startingY + 1) < 9 && (startingX -1) > 0 && (startingX - 1) < 9) { // Check if new spot in bounds first
                if (board.getPiece(new ChessPosition(startingY + 1, startingX - 1)) == null) { // Checking if top left empty
                    possibleMoves.add(new ChessMove(new ChessPosition(startingY, startingX), new ChessPosition(startingY + 1, startingX - 1), null));
                }
                else {
                    ChessPiece newSpot = board.getPiece(new ChessPosition(startingY + 1, startingX - 1));
                    if (newSpot.getTeamColor() != pieceColor) { // If piece in the way is an opponent... add their spot to the list
                        possibleMoves.add(new ChessMove(new ChessPosition(startingY, startingX), new ChessPosition(startingY + 1, startingX - 1), null));
                    }
                }
            }
            // Top middle
            if ((startingY + 1) > 0 && (startingY + 1) < 9) { // Check if new spot in bounds
                if (board.getPiece(new ChessPosition(startingY + 1, startingX)) == null) { // Checking if empty... if so, add to list
                    possibleMoves.add(new ChessMove(new ChessPosition(startingY, startingX), new ChessPosition(startingY + 1, startingX), null));
                }
                else {
                    ChessPiece newSpot = board.getPiece(new ChessPosition(startingY + 1, startingX));
                    if (newSpot.getTeamColor() != pieceColor) { // If piece in the way is an opponent... add their spot to the list
                        possibleMoves.add(new ChessMove(new ChessPosition(startingY, startingX), new ChessPosition(startingY + 1, startingX), null));
                    }
                }
            }
            // Top right
            if ((startingY + 1) > 0 && (startingY + 1) < 9 && (startingX + 1) > 0 && (startingX + 1) < 9) { // Check if new spot in bounds
                if (board.getPiece(new ChessPosition(startingY + 1, startingX + 1)) == null) { // Checking if empty... if so, add to list
                    possibleMoves.add(new ChessMove(new ChessPosition(startingY, startingX), new ChessPosition(startingY + 1, startingX + 1), null));
                }
                else {
                    ChessPiece newSpot = board.getPiece(new ChessPosition(startingY + 1, startingX + 1));
                    if (newSpot.getTeamColor() != pieceColor) { // If piece in the way is an opponent... add their spot to the list
                        possibleMoves.add(new ChessMove(new ChessPosition(startingY, startingX), new ChessPosition(startingY + 1, startingX + 1), null));
                    }
                }
            }
            // Middle left
            if ((startingX - 1) > 0 && (startingX - 1) < 9) { // Check if new spot in bounds
                if (board.getPiece(new ChessPosition(startingY, startingX - 1)) == null) { // Checking if empty... if so, add to list
                    possibleMoves.add(new ChessMove(new ChessPosition(startingY, startingX), new ChessPosition(startingY, startingX - 1), null));
                }
                else {
                    ChessPiece newSpot = board.getPiece(new ChessPosition(startingY, startingX - 1));
                    if (newSpot.getTeamColor() != pieceColor) { // If piece in the way is an opponent... add their spot to the list
                        possibleMoves.add(new ChessMove(new ChessPosition(startingY, startingX), new ChessPosition(startingY, startingX - 1), null));
                    }
                }
            }
            // Middle right
            if ((startingX + 1) > 0 && (startingX + 1) < 9) { // Check if new spot in bounds
                if (board.getPiece(new ChessPosition(startingY, startingX + 1)) == null) { // Checking if empty... if so, add to list
                    possibleMoves.add(new ChessMove(new ChessPosition(startingY, startingX), new ChessPosition(startingY, startingX + 1), null));
                }
                else {
                    ChessPiece newSpot = board.getPiece(new ChessPosition(startingY, startingX + 1));
                    if (newSpot.getTeamColor() != pieceColor) { // If piece in the way is an opponent... add their spot to the list
                        possibleMoves.add(new ChessMove(new ChessPosition(startingY, startingX), new ChessPosition(startingY, startingX + 1), null));
                    }
                }
            }
            // Bottom left
            if ((startingY - 1) > 0 && (startingY - 1) < 9 && (startingX - 1) > 0 && (startingX - 1) < 9) { // Check if new spot in bounds
                if (board.getPiece(new ChessPosition(startingY - 1, startingX - 1)) == null) { // Checking if empty... if so, add to list
                    possibleMoves.add(new ChessMove(new ChessPosition(startingY, startingX), new ChessPosition(startingY - 1, startingX - 1), null));
                }
                else {
                    ChessPiece newSpot = board.getPiece(new ChessPosition(startingY - 1, startingX - 1));
                    if (newSpot.getTeamColor() != pieceColor) { // If piece in the way is an opponent... add their spot to the list
                        possibleMoves.add(new ChessMove(new ChessPosition(startingY, startingX), new ChessPosition(startingY - 1, startingX - 1), null));
                    }
                }
            }
            // Bottom middle
            if ((startingY - 1) > 0 && (startingY - 1) < 9) { // Check if new spot in bounds
                if (board.getPiece(new ChessPosition(startingY - 1, startingX)) == null) { // Checking if empty... if so, add to list
                    possibleMoves.add(new ChessMove(new ChessPosition(startingY, startingX), new ChessPosition(startingY - 1, startingX), null));
                }
                else {
                    ChessPiece newSpot = board.getPiece(new ChessPosition(startingY - 1, startingX));
                    if (newSpot.getTeamColor() != pieceColor) { // If piece in the way is an opponent... add their spot to the list
                        possibleMoves.add(new ChessMove(new ChessPosition(startingY, startingX), new ChessPosition(startingY - 1, startingX), null));
                    }
                }
            }
            // Bottom right
            if ((startingY - 1) > 0 && (startingY - 1) < 9 && (startingX + 1) > 0 && (startingX + 1) < 9) { // Check if new spot in bounds
                if (board.getPiece(new ChessPosition(startingY - 1, startingX + 1)) == null) { // Checking if empty... if so, add to list
                    possibleMoves.add(new ChessMove(new ChessPosition(startingY, startingX), new ChessPosition(startingY - 1, startingX + 1), null));
                }
                else {
                    ChessPiece newSpot = board.getPiece(new ChessPosition(startingY - 1, startingX + 1));
                    if (newSpot.getTeamColor() != pieceColor) { // If piece in the way is an opponent... add their spot to the list
                        possibleMoves.add(new ChessMove(new ChessPosition(startingY, startingX), new ChessPosition(startingY - 1, startingX + 1), null));
                    }
                }
            }
        }


        else if (piece.getPieceType() == PieceType.ROOK) {
            // Check left
            int x = startingX - 1;
            int y = startingY;
            while (x >= 1) {
                ChessPiece newSpot = board.getPiece(new ChessPosition(y, x));
                if (newSpot == null) { // Check if space is empty
                    possibleMoves.add(new ChessMove(new ChessPosition(startingY, startingX), new ChessPosition(y, x), null)); // If empty, add to list of possible moves
                    // Decrement x
                    x--;
                }
                // If not empty, check team type
                else if (newSpot.getTeamColor() != pieceColor) { // (May have to put this.pieceColor) If they are not our team, add final spot to list before exiting
                    possibleMoves.add(new ChessMove(new ChessPosition(startingY, startingX), new ChessPosition(y, x), null));
                    // Set x to 0 so it exits while loop
                    x = 0;
                } else { // Piece in the way is on our team and we exit while loop
                    x = 0;
                }
            }
            // Check right
            x = startingX + 1;
            while (x <= 8) {
                ChessPiece newSpot = board.getPiece(new ChessPosition(y, x));
                if (newSpot == null) { // Check if space is empty
                    possibleMoves.add(new ChessMove(new ChessPosition(startingY, startingX), new ChessPosition(y, x), null)); // If empty, add to list of possible moves
                    // Increment x
                    x++;
                }
                // If not empty, check team type
                else if (newSpot.getTeamColor() != pieceColor) { // (May have to put this.pieceColor) If they are not our team, add final spot to list before exiting
                    possibleMoves.add(new ChessMove(new ChessPosition(startingY, startingX), new ChessPosition(y, x), null));
                    // Set x to 9 so it exits while loop
                    x = 9;
                } else { // Piece in the way is on our team and we exit while loop
                    x = 9;
                }
            }
            // Check down
            x = startingX;
            y = startingY - 1;
            while (y >= 1) {
                ChessPiece newSpot = board.getPiece(new ChessPosition(y, x));
                if (newSpot == null) { // Check if space is empty
                    possibleMoves.add(new ChessMove(new ChessPosition(startingY, startingX), new ChessPosition(y, x), null)); // If empty, add to list of possible moves
                    // Increment x
                    y--;
                }
                // If not empty, check team type
                else if (newSpot.getTeamColor() != pieceColor) { // (May have to put this.pieceColor) If they are not our team, add final spot to list before exiting
                    possibleMoves.add(new ChessMove(new ChessPosition(startingY, startingX), new ChessPosition(y, x), null));
                    // Set y to 0 so it exits while loop
                    y = 0;
                } else { // Piece in the way is on our team and we exit while loop
                    y = 0;
                }
            }
            // Check up
            y = startingY + 1;
            while (y <= 8) {
                ChessPiece newSpot = board.getPiece(new ChessPosition(y, x));
                if (newSpot == null) { // Check if space is empty
                    possibleMoves.add(new ChessMove(new ChessPosition(startingY, startingX), new ChessPosition(y, x), null)); // If empty, add to list of possible moves
                    // Increment x
                    y++;
                }
                // If not empty, check team type
                else if (newSpot.getTeamColor() != pieceColor) { // (May have to put this.pieceColor) If they are not our team, add final spot to list before exiting
                    possibleMoves.add(new ChessMove(new ChessPosition(startingY, startingX), new ChessPosition(y, x), null));
                    // Set y to 0 so it exits while loop
                    y = 9;
                } else { // Piece in the way is on our team and we exit while loop
                    y = 9;
                }
            }
        }


        else if (piece.getPieceType() == PieceType.KNIGHT) {
            // Left 2 Up 1
            if ((startingY + 1) > 0 && (startingY + 1) < 9 && (startingX - 2) > 0 && (startingX - 2) < 9) { // Check if new spot in bounds first
                if (board.getPiece(new ChessPosition(startingY + 1, startingX - 2)) == null) { // Checking if top left empty
                    possibleMoves.add(new ChessMove(new ChessPosition(startingY, startingX), new ChessPosition(startingY + 1, startingX - 2), null));
                }
                else {
                    ChessPiece newSpot = board.getPiece(new ChessPosition(startingY + 1, startingX - 2));
                    if (newSpot.getTeamColor() != pieceColor) { // If piece in the way is an opponent... add their spot to the list
                        possibleMoves.add(new ChessMove(new ChessPosition(startingY, startingX), new ChessPosition(startingY + 1, startingX - 2), null));
                    }
                }
            }
            // Left 1 Up 2
            if ((startingY + 2) > 0 && (startingY + 2) < 9 && (startingX - 1) > 0 && (startingX - 1) < 9) { // Check if new spot in bounds
                if (board.getPiece(new ChessPosition(startingY + 2, startingX - 1)) == null) { // Checking if empty... if so, add to list
                    possibleMoves.add(new ChessMove(new ChessPosition(startingY, startingX), new ChessPosition(startingY + 2, startingX - 1), null));
                }
                else {
                    ChessPiece newSpot = board.getPiece(new ChessPosition(startingY + 2, startingX - 1));
                    if (newSpot.getTeamColor() != pieceColor) { // If piece in the way is an opponent... add their spot to the list
                        possibleMoves.add(new ChessMove(new ChessPosition(startingY, startingX), new ChessPosition(startingY + 2, startingX - 1), null));
                    }
                }
            }
            // Right 1 Up 2
            if ((startingY + 2) > 0 && (startingY + 2) < 9 && (startingX + 1) > 0 && (startingX + 1) < 9) { // Check if new spot in bounds
                if (board.getPiece(new ChessPosition(startingY + 2, startingX + 1)) == null) { // Checking if empty... if so, add to list
                    possibleMoves.add(new ChessMove(new ChessPosition(startingY, startingX), new ChessPosition(startingY + 2, startingX + 1), null));
                }
                else {
                    ChessPiece newSpot = board.getPiece(new ChessPosition(startingY + 2, startingX + 1));
                    if (newSpot.getTeamColor() != pieceColor) { // If piece in the way is an opponent... add their spot to the list
                        possibleMoves.add(new ChessMove(new ChessPosition(startingY, startingX), new ChessPosition(startingY + 2, startingX + 1), null));
                    }
                }
            }
            // Right 2 Up 1
            if ((startingY + 1) > 0 && (startingY + 1) < 9 && (startingX + 2) > 0 && (startingX + 2) < 9) { // Check if new spot in bounds
                if (board.getPiece(new ChessPosition(startingY + 1, startingX + 2)) == null) { // Checking if empty... if so, add to list
                    possibleMoves.add(new ChessMove(new ChessPosition(startingY, startingX), new ChessPosition(startingY + 1, startingX + 2), null));
                }
                else {
                    ChessPiece newSpot = board.getPiece(new ChessPosition(startingY + 1, startingX + 2));
                    if (newSpot.getTeamColor() != pieceColor) { // If piece in the way is an opponent... add their spot to the list
                        possibleMoves.add(new ChessMove(new ChessPosition(startingY, startingX), new ChessPosition(startingY + 1, startingX + 2), null));
                    }
                }
            }
            // Right 2 Down 1
            if ((startingY - 1) > 0 && (startingY - 1) < 9 && (startingX + 2) > 0 && (startingX + 2) < 9) { // Check if new spot in bounds
                if (board.getPiece(new ChessPosition(startingY - 1, startingX + 2)) == null) { // Checking if empty... if so, add to list
                    possibleMoves.add(new ChessMove(new ChessPosition(startingY, startingX), new ChessPosition(startingY - 1, startingX + 2), null));
                }
                else {
                    ChessPiece newSpot = board.getPiece(new ChessPosition(startingY - 1, startingX + 2));
                    if (newSpot.getTeamColor() != pieceColor) { // If piece in the way is an opponent... add their spot to the list
                        possibleMoves.add(new ChessMove(new ChessPosition(startingY, startingX), new ChessPosition(startingY - 1, startingX + 2), null));
                    }
                }
            }
            // Right 1 Down 2
            if ((startingY - 2) > 0 && (startingY - 2) < 9 && (startingX + 1) > 0 && (startingX + 1) < 9) { // Check if new spot in bounds
                if (board.getPiece(new ChessPosition(startingY - 2, startingX + 1)) == null) { // Checking if empty... if so, add to list
                    possibleMoves.add(new ChessMove(new ChessPosition(startingY, startingX), new ChessPosition(startingY - 2, startingX + 1), null));
                }
                else {
                    ChessPiece newSpot = board.getPiece(new ChessPosition(startingY - 2, startingX + 1));
                    if (newSpot.getTeamColor() != pieceColor) { // If piece in the way is an opponent... add their spot to the list
                        possibleMoves.add(new ChessMove(new ChessPosition(startingY, startingX), new ChessPosition(startingY - 2, startingX + 1), null));
                    }
                }
            }
            // Left 1 Down 2
            if ((startingY - 2) > 0 && (startingY - 2) < 9 && (startingX - 1) > 0 && (startingX - 1) < 9) { // Check if new spot in bounds
                if (board.getPiece(new ChessPosition(startingY - 2, startingX - 1)) == null) { // Checking if empty... if so, add to list
                    possibleMoves.add(new ChessMove(new ChessPosition(startingY, startingX), new ChessPosition(startingY - 2, startingX - 1), null));
                }
                else {
                    ChessPiece newSpot = board.getPiece(new ChessPosition(startingY - 2, startingX - 1));
                    if (newSpot.getTeamColor() != pieceColor) { // If piece in the way is an opponent... add their spot to the list
                        possibleMoves.add(new ChessMove(new ChessPosition(startingY, startingX), new ChessPosition(startingY - 2, startingX - 1), null));
                    }
                }
            }
            // Left 2 Down 1
            if ((startingY - 1) > 0 && (startingY - 1) < 9 && (startingX - 2) > 0 && (startingX - 2) < 9) { // Check if new spot in bounds
                if (board.getPiece(new ChessPosition(startingY - 1, startingX - 2)) == null) { // Checking if empty... if so, add to list
                    possibleMoves.add(new ChessMove(new ChessPosition(startingY, startingX), new ChessPosition(startingY - 1, startingX - 2), null));
                }
                else {
                    ChessPiece newSpot = board.getPiece(new ChessPosition(startingY - 1, startingX - 2));
                    if (newSpot.getTeamColor() != pieceColor) { // If piece in the way is an opponent... add their spot to the list
                        possibleMoves.add(new ChessMove(new ChessPosition(startingY, startingX), new ChessPosition(startingY - 1, startingX - 2), null));
                    }
                }
            }
        }
        return possibleMoves;
    }
}
