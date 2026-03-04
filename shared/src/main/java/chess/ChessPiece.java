package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessPiece that = (ChessPiece) o;
        return pieceColor == that.pieceColor && type == that.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(pieceColor, type);
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
            calculateSlidingMoves(board, myPosition, possibleMoves, 1, 1);   // Top Right
            calculateSlidingMoves(board, myPosition, possibleMoves, 1, -1);  // Top Left
            calculateSlidingMoves(board, myPosition, possibleMoves, -1, -1); // Bottom Left
            calculateSlidingMoves(board, myPosition, possibleMoves, -1, 1);  // Bottom Right
        }

        else if (piece.getPieceType() == PieceType.KING) {
            int[][] kingMoves = {
                    {1, -1}, {1, 0}, {1, 1},
                    {0, -1},         {0, 1},
                    {-1, -1}, {-1, 0}, {-1, 1}
            };

            for (int[] move : kingMoves) {
                calculateStepMoves(board, myPosition, possibleMoves, move[0], move[1]);
            }

            // Castling
            ChessPiece king = board.getPiece(myPosition);
            if (king.getTeamColor() == ChessGame.TeamColor.WHITE) {
                ChessPiece uno = board.getPiece(new ChessPosition(1, 1));
                ChessPiece dos = board.getPiece(new ChessPosition(1, 2));
                ChessPiece tres = board.getPiece(new ChessPosition(1, 3));
                ChessPiece cuatro = board.getPiece(new ChessPosition(1, 4));
                ChessPiece cinco = board.getPiece(new ChessPosition(1, 5));
                ChessPiece seis = board.getPiece(new ChessPosition(1, 6));
                ChessPiece siete = board.getPiece(new ChessPosition(1, 7));
                ChessPiece ocho = board.getPiece(new ChessPosition(1, 8));

                // Check left of king
                if (uno != null && uno.getPieceType() == PieceType.ROOK && dos != null
                        && tres == null && cuatro == null && cinco != null
                        && cinco.getPieceType() == PieceType.KING) {
                    possibleMoves.add(new ChessMove(new ChessPosition(startingY, startingX), new ChessPosition(1, 3), null)); // king
                    possibleMoves.add(new ChessMove(new ChessPosition(1, 1), new ChessPosition(1, 4), null)); // rook
                }

                // Check right of king
                if (ocho != null && ocho.getPieceType() == PieceType.ROOK && siete == null
                        && seis == null && cinco != null
                        && cinco.getPieceType() == PieceType.KING) {
                    possibleMoves.add(new ChessMove(new ChessPosition(startingY, startingX), new ChessPosition(1, 3), null));
                    possibleMoves.add(new ChessMove(new ChessPosition(1, 1), new ChessPosition(1, 4), null));
                }
            }
        }

        else if (piece.getPieceType() == PieceType.ROOK) {
            calculateSlidingMoves(board, myPosition, possibleMoves, 1, 0);  // Up
            calculateSlidingMoves(board, myPosition, possibleMoves, -1, 0); // Down
            calculateSlidingMoves(board, myPosition, possibleMoves, 0, -1); // Left
            calculateSlidingMoves(board, myPosition, possibleMoves, 0, 1);  // Right
        }

        else if (piece.getPieceType() == PieceType.KNIGHT) {
            int[][] knightMoves = {
                    {2, 1}, {2, -1}, {1, 2}, {1, -2},
                    {-1, 2}, {-1, -2}, {-2, 1}, {-2, -1}
            };

            for (int[] move : knightMoves) {
                calculateStepMoves(board, myPosition, possibleMoves, move[0], move[1]);
            }
        }

        else if (piece.getPieceType() == PieceType.QUEEN) {
            calculateSlidingMoves(board, myPosition, possibleMoves, 1, 1);   // Top Right
            calculateSlidingMoves(board, myPosition, possibleMoves, 1, -1);  // Top Left
            calculateSlidingMoves(board, myPosition, possibleMoves, -1, -1); // Bottom Left
            calculateSlidingMoves(board, myPosition, possibleMoves, -1, 1);  // Bottom Right
            calculateSlidingMoves(board, myPosition, possibleMoves, 1, 0);   // Up
            calculateSlidingMoves(board, myPosition, possibleMoves, -1, 0);  // Down
            calculateSlidingMoves(board, myPosition, possibleMoves, 0, -1);  // Left
            calculateSlidingMoves(board, myPosition, possibleMoves, 0, 1);   // Right
        }


        else if (piece.getPieceType() == PieceType.PAWN) {
            List<PieceType> upgrades = List.of(PieceType.QUEEN, PieceType.ROOK, PieceType.BISHOP, PieceType.KNIGHT);
            if (pieceColor == ChessGame.TeamColor.WHITE) {
                if ((startingY + 1) <=8) { // Check if space in front is in bounds, then check if at the end to change null
                    ChessPiece newSpot1 = board.getPiece(new ChessPosition(startingY + 1, startingX));
                    if (newSpot1 == null) { // Check if space is empty
                        if ((startingY + 1) == 8) {
                            for (PieceType upgrade : upgrades) {
                                possibleMoves.add(new ChessMove(new ChessPosition(startingY, startingX), new ChessPosition(startingY + 1, startingX), upgrade));
                            }
                        }
                        else {
                            possibleMoves.add(new ChessMove(new ChessPosition(startingY, startingX), new ChessPosition(startingY + 1, startingX), null));
                        }
                    }
                }
                if (startingY == 2) { // Check starting location
                    ChessPiece newSpot2 = board.getPiece(new ChessPosition(startingY + 2, startingX));
                    ChessPiece steppingStone = board.getPiece(new ChessPosition(startingY + 1, startingX));
                    if (newSpot2 == null && steppingStone == null) { // Check if space is empty
                        possibleMoves.add(new ChessMove(new ChessPosition(startingY, startingX), new ChessPosition(startingY + 2, startingX), null));
                    }
                }
                if ((startingY + 1) <= 8 && (startingX - 1) >= 1) { // Check if diagonal, then check if at end to change null
                    ChessPiece attackSpot1 = board.getPiece(new ChessPosition(startingY + 1, startingX - 1));
                    if (attackSpot1 != null && attackSpot1.getTeamColor() != pieceColor) { // Check if in bounds
                        if ((startingY + 1) == 8) {
                            for (PieceType upgrade : upgrades) {
                                possibleMoves.add(new ChessMove(new ChessPosition(startingY, startingX), new ChessPosition(startingY + 1, startingX - 1), upgrade));
                            }
                        }
                        else {
                            possibleMoves.add(new ChessMove(new ChessPosition(startingY, startingX), new ChessPosition(startingY + 1, startingX - 1), null));
                        }
                    }
                }
                if ((startingY + 1) <= 8 && (startingX + 1) <= 8) { // Same as above
                    ChessPiece attackSpot2 = board.getPiece(new ChessPosition(startingY + 1, startingX + 1));
                    if (attackSpot2 != null && attackSpot2.getTeamColor() != pieceColor) { // Check if in bounds
                        if ((startingY + 1) == 8) {
                            for (PieceType upgrade : upgrades) {
                                possibleMoves.add(new ChessMove(new ChessPosition(startingY, startingX), new ChessPosition(startingY + 1, startingX + 1), upgrade));
                            }
                        }
                        else {
                            possibleMoves.add(new ChessMove(new ChessPosition(startingY, startingX), new ChessPosition(startingY + 1, startingX + 1), null));
                        }
                    }
                }
            }
            else if (pieceColor == ChessGame.TeamColor.BLACK) {
                if ((startingY - 1) >= 1) {
                    ChessPiece newSpot1 = board.getPiece(new ChessPosition(startingY - 1, startingX));
                    if (newSpot1 == null) {
                        if ((startingY - 1) == 1) {
                            for (PieceType upgrade : upgrades) {
                                possibleMoves.add(new ChessMove(new ChessPosition(startingY, startingX), new ChessPosition(startingY - 1, startingX), upgrade));
                            }
                        }
                        else {
                            possibleMoves.add(new ChessMove(new ChessPosition(startingY, startingX), new ChessPosition(startingY - 1, startingX), null));
                        }
                    }
                }
                if (startingY == 7) { // Check starting location
                    ChessPiece newSpot2 = board.getPiece(new ChessPosition(startingY - 2, startingX));
                    ChessPiece steppingStone = board.getPiece(new ChessPosition(startingY-1, startingX));
                    if (newSpot2 == null && steppingStone == null) {
                        possibleMoves.add(new ChessMove(new ChessPosition(startingY, startingX), new ChessPosition(startingY - 2, startingX), null));
                    }
                }
                if ((startingY - 1) >= 1 && (startingX - 1) >= 1) {
                    ChessPiece attackSpot1 = board.getPiece(new ChessPosition(startingY - 1, startingX - 1));
                    if (attackSpot1 != null && attackSpot1.getTeamColor() != pieceColor) { // Check if in bounds
                        if ((startingY - 1) == 1) {
                            for (PieceType upgrade : upgrades) {
                                possibleMoves.add(new ChessMove(new ChessPosition(startingY, startingX), new ChessPosition(startingY - 1, startingX - 1), upgrade));
                            }
                        }
                        else {
                            possibleMoves.add(new ChessMove(new ChessPosition(startingY, startingX), new ChessPosition(startingY - 1, startingX - 1), null));
                        }
                    }
                }
                if ((startingY - 1) >= 1 && (startingX + 1) <= 8) {
                    ChessPiece attackSpot2 = board.getPiece(new ChessPosition(startingY - 1, startingX + 1));
                    if (attackSpot2 != null && attackSpot2.getTeamColor() != pieceColor) { // Check if in bounds
                        if ((startingY - 1) == 1) {
                            for (PieceType upgrade : upgrades) {
                                possibleMoves.add(new ChessMove(new ChessPosition(startingY, startingX), new ChessPosition(startingY - 1, startingX + 1), upgrade));
                            }
                        }
                        else {
                            possibleMoves.add(new ChessMove(new ChessPosition(startingY, startingX), new ChessPosition(startingY - 1, startingX + 1), null));
                        }
                    }
                }
            }
        }


        return possibleMoves;
    }
    private void calculateSlidingMoves(ChessBoard board, ChessPosition myPosition, List<ChessMove> possibleMoves, int rowDirection, int colDirection) {
        int currentRow = myPosition.getRow() + rowDirection;
        int currentCol = myPosition.getColumn() + colDirection;

        while (currentRow >= 1 && currentRow <= 8 && currentCol >= 1 && currentCol <= 8) {
            ChessPosition newPos = new ChessPosition(currentRow, currentCol);
            ChessPiece pieceAtNewPos = board.getPiece(newPos);

            if (pieceAtNewPos == null) {
                possibleMoves.add(new ChessMove(myPosition, newPos, null));
                currentRow += rowDirection;
                currentCol += colDirection;
            } else {
                if (pieceAtNewPos.getTeamColor() != this.pieceColor) {
                    possibleMoves.add(new ChessMove(myPosition, newPos, null));
                }
                break;
            }
        }
    }
    private void calculateStepMoves(ChessBoard board, ChessPosition myPosition, List<ChessMove> possibleMoves, int rowOffset, int colOffset) {
        int newRow = myPosition.getRow() + rowOffset;
        int newCol = myPosition.getColumn() + colOffset;

        // Check if the new position is on the board
        if (newRow >= 1 && newRow <= 8 && newCol >= 1 && newCol <= 8) {
            ChessPosition newPos = new ChessPosition(newRow, newCol);
            ChessPiece pieceAtNewPos = board.getPiece(newPos);

            // If the space is empty OR has an enemy piece, it's a valid move
            if (pieceAtNewPos == null || pieceAtNewPos.getTeamColor() != this.pieceColor) {
                possibleMoves.add(new ChessMove(myPosition, newPos, null));
            }
        }
    }
}
