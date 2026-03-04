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
            calculatePawnMoves(board, myPosition, possibleMoves);
        }


        return possibleMoves;
    }
    private void calculateSlidingMoves(ChessBoard board, ChessPosition myPosition, List<ChessMove> possibleMoves,
                                       int rowDirection, int colDirection) {
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
    private void calculateStepMoves(ChessBoard board, ChessPosition myPosition, List<ChessMove> possibleMoves,
                                    int rowOffset, int colOffset) {
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
    private void calculatePawnMoves(ChessBoard board, ChessPosition myPosition, List<ChessMove> possibleMoves) {
        int direction = (this.pieceColor == ChessGame.TeamColor.WHITE) ? 1 : -1;
        int startRow = (this.pieceColor == ChessGame.TeamColor.WHITE) ? 2 : 7;
        int promoRow = (this.pieceColor == ChessGame.TeamColor.WHITE) ? 8 : 1;
        int row = myPosition.getRow();
        int col = myPosition.getColumn();
        int nextRow = row + direction;

        // Forward 1
        if (nextRow >= 1 && nextRow <= 8 && board.getPiece(new ChessPosition(nextRow, col)) == null) {
            addPawnMove(myPosition, new ChessPosition(nextRow, col), nextRow == promoRow, possibleMoves);
            // Forward 2
            int doubleRow = row + (2 * direction);
            if (row == startRow && board.getPiece(new ChessPosition(doubleRow, col)) == null) {
                possibleMoves.add(new ChessMove(myPosition, new ChessPosition(doubleRow, col), null));
            }
        }

        // Captures
        int[] captureCols = {col - 1, col + 1};
        for (int c : captureCols) {
            if (c >= 1 && c <= 8 && nextRow >= 1 && nextRow <= 8) {
                ChessPiece target = board.getPiece(new ChessPosition(nextRow, c));
                if (target != null && target.getTeamColor() != this.pieceColor) {
                    addPawnMove(myPosition, new ChessPosition(nextRow, c), nextRow == promoRow, possibleMoves);
                }
            }
        }
    }

    private void addPawnMove(ChessPosition start, ChessPosition end, boolean isPromotion, List<ChessMove> moves) {
        if (isPromotion) {
            moves.add(new ChessMove(start, end, PieceType.QUEEN));
            moves.add(new ChessMove(start, end, PieceType.ROOK));
            moves.add(new ChessMove(start, end, PieceType.BISHOP));
            moves.add(new ChessMove(start, end, PieceType.KNIGHT));
        } else {
            moves.add(new ChessMove(start, end, null));
        }
    }
}
