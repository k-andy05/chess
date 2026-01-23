package chess; // This tells the compiler that this file belongs to a folder named chess

/**
 * A chessboard that can hold and rearrange chess pieces.
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessBoard { // Chessboard class that is in charge of adding pieces, getting a chess piece, and resetting the board

    ChessPiece[][] squares = new ChessPiece[8][8]; // a new ChessPiece class object called squares is created with a 8x8 grid size
    public ChessBoard() { // The constructor that is run when I type new ChessBoard()
        
    }

    /**
     * Adds a chess piece to the chessboard
     *
     * @param position where to add the piece to
     * @param piece    the piece to add
     */
    public void addPiece(ChessPosition position, ChessPiece piece) { // method that takes a position and piece and puts them on the ChessBoard
        squares[position.getRow()-1][position.getColumn()-1] = piece;
    }

    /**
     * Gets a chess piece on the chessboard
     *
     * @param position The position to get the piece from
     * @return Either the piece at the position, or null if no piece is at that
     * position
     */
    public ChessPiece getPiece(ChessPosition position) { // Returns which piece is standing on a specific square on the board
        return squares[position.getRow()-1][position.getColumn()-1];
    }

    /**
     * Sets the board to the default starting board
     * (How the game of chess normally starts)
     */
    public void resetBoard() {
        throw new RuntimeException("Not implemented");
    }
}
