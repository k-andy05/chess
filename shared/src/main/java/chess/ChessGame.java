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
//        currentBoard.resetBoard();
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

//        boolean inCheck = false;
//        return inCheck;
//        ChessPosition kingPosition = getKingPosition(teamColor);
//        Collection<ChessMove> kingMoves = currentBoard.getPiece(kingPosition).pieceMoves(currentBoard, kingPosition);
//        Collection<ChessMove> attackerMoves = new ArrayList<>();
//        Collection<ChessMove> friendlyMoves = new ArrayList<>();
//        Collection<ChessMove> trajectoryMoves = new ArrayList<>();
////        Collection<ChessMove> kingEndMovesContested = new ArrayList<>();
//
//        // Populate attackerMoves and friendlyMoves lists
//        for (int col = 1; col <=8; col++) {
//            for (int row = 1; row <=8; row++) {
//                ChessPosition position = new ChessPosition(row, col);
//                ChessPiece piece = currentBoard.getPiece(position);
//                if (piece != null) { // Check that there is a piece on the space
//                    if (piece.getTeamColor() != teamColor) {
//                        Collection<ChessMove> attackerPieceMoves = piece.pieceMoves(currentBoard, position);
//                        attackerMoves.addAll(attackerPieceMoves); // Adds attacker moves to list (duplicates too)
//                        // Add the ChessMove right before piece would hit the king
//                    }
//                    else if (piece.getPieceType() != ChessPiece.PieceType.KING && piece.getTeamColor() == teamColor) {
//                        Collection<ChessMove> friendlyPieceMoves = piece.pieceMoves(currentBoard, position);
//                        friendlyMoves.addAll(friendlyPieceMoves); // Adds teammates' moves to list (duplicates too)
//                    }
//                }
//            }
//        }
//
//        Collection<ChessPosition> attackerEndPositions = getEndPositions(attackerMoves);
//        Collection<ChessPosition> kingEndPositions = getEndPositions(kingMoves);
//
//        // Is king vulnerable at current position? (is king's current position in the list of end positions for attacker moves?)
//        for (ChessPosition attackerEndPosition : attackerEndPositions) {
//            if (attackerEndPosition == kingPosition) {
//                // Can king move to a free spot that is safe? (is there an end position in kingMoves not in current attackerMoves end position list?)
//                for (ChessPosition kingEndPosition : kingEndPositions) {
//                    if (!attackerEndPositions.contains(kingEndPosition)) { // is position not in attacker list
//                        ChessPiece newKingSpot = currentBoard.getPiece(kingEndPosition);
//                        // Is there a space king can move to that will capture an enemy?
//                        if (newKingSpot != null) { // yes
//                            // Could be Check or Checkmate, but not either
//                            inCheck = true;
//                            return inCheck;
////                            ChessGame gameCopy = this;
////                            try {
////                                gameCopy.makeMove(new ChessMove(kingPosition, kingEndPosition, null));
////                                Collection<ChessMove> newAttackerMoves = new ArrayList<>(); // Populate newAttackerMoves list
////                                for (int col = 1; col <= 8; col++) {
////                                    for (int row = 1; row <= 8; row++) {
////                                        ChessPosition position = new ChessPosition(row, col);
////                                        ChessPiece piece = gameCopy.currentBoard.getPiece(position);
////                                        if (piece != null && piece.getTeamColor() != teamColor) {
////                                            Collection<ChessMove> attackerPieceMoves = piece.pieceMoves(currentBoard, position);
////                                            newAttackerMoves.addAll(attackerPieceMoves);
////                                        }
////                                    }
////                                }
////                                Collection<ChessPosition> newAttackerEndPositions = getEndPositions(newAttackerMoves);
////                                // Is new king spot in attacker list?
////                                for (ChessPosition newAttackerEndPosition : newAttackerEndPositions) {
////                                    if (newAttackerEndPosition == kingEndPosition) {
////                                        // CHECKMATE
////                                        inCheck = true;
////                                        return inCheck;
////                                    }
////                                }
////                            }
////                            catch (InvalidMoveException e){
////                                System.out.println("That move is invalid.");
////                            }
//                        }
//                        // no, move on to #4 check
//                        // Can a friendlyPiece block all attacker moves on the king?
////                        Collection<ChessMove>;
////                        Collection<ChessPosition>;
//                    }
//                }
//                inCheck = true;
//                return inCheck;
//            }
//        }
//        return inCheck; // King is not vulnerable at all, not check nor checkmate







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
        if (teamTurn == teamColor) {
            if (this.isInCheck(teamColor)) { // In Check?
                ChessPosition kingPosition = this.getKingPosition(teamColor, currentBoard);
                Collection<ChessMove> validMoves = this.validMoves(kingPosition);
                return validMoves.isEmpty();
            }
        }
        return false;
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
        if (this.teamTurn == teamColor && !isInCheck(teamColor)) { // Check that it is our turn and we aren't in check
            for (int row=1; row<=8; row++) {
                for (int col=1; col<=8; col++) {
                    ChessPosition position = new ChessPosition(row, col);
                    ChessPiece piece = currentBoard.getPiece(position);
                    if (piece != null && piece.getTeamColor() == teamColor) { // Look at all our team's remaining pieces
                        if (!validMoves(position).isEmpty()) { // If they have valid moves, then it isn't a stalemate
                            return false;
                        }
                    }
                }
            }
            return true;
        return false;
    }

    /**
     * Returns with current board's king spot of a team.
     *
     * @param teamColor the team who's king we're looking for
     */

//    private ChessPosition getKingPosition(TeamColor teamColor) {
//        ChessPosition kingPosition = null;
//        for (int col = 1; col <=8; col++) {
//            for (int row = 1; row <=8; row++) {
//                ChessPiece piece = currentBoard.getPiece(new ChessPosition(row, col));
//                if (piece != null && piece.getTeamColor() == teamColor && piece.getPieceType() == ChessPiece.PieceType.KING) {
//                    kingPosition = new ChessPosition(row, col);
//                }
//            }
//        }
//        return kingPosition;
//    }

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

    private Collection<ChessPosition> getTrajectoryMoves(Collection<ChessMove> attackerMoves, ChessPosition kingPosition) {
        Collection<ChessPosition> trajectoryPositions = new ArrayList<>();
        int kingCol = kingPosition.getColumn();
        int kingRow = kingPosition.getRow();

        // 1. Can you hit the king?
        for (ChessMove move : attackerMoves) {
            ChessPosition start = move.getStartPosition();
            ChessPosition end = move.getEndPosition();
            ChessPiece.PieceType promotion = move.getPromotionPiece();
            int attackerCol = start.getColumn();
            int attackerRow = start.getRow();

            if (end == kingPosition) { // 1. yes

                // 2. What piece are you?
                ChessPiece.PieceType type = currentBoard.getPiece(start).getPieceType();
                if (type == ChessPiece.PieceType.ROOK) {
                    if (attackerCol == kingCol) {
                        if (attackerRow > kingRow) { // If rook above king
                            trajectoryPositions.add(new ChessPosition(kingRow + 1, kingCol));
                        }
                        else { // If rook below king
                            trajectoryPositions.add(new ChessPosition(kingRow - 1, kingCol));
                        }
                    }
                    else if (attackerRow == kingRow) {
                        if (attackerCol > kingCol) { // If rook is to right of king
                            trajectoryPositions.add(new ChessPosition(kingRow, kingCol + 1));
                        }
                        else { // If rook is to the left of king
                            trajectoryPositions.add(new ChessPosition(kingRow, kingCol - 1));
                        }
                    }
                }
                if (type == ChessPiece.PieceType.BISHOP) {
                    if (attackerRow < kingRow) {
                        if (attackerCol < kingCol) { // bottom left
                            trajectoryPositions.add(new ChessPosition(kingRow - 1, kingCol - 1));
                        }
                        else { // top left
                            trajectoryPositions.add(new ChessPosition(kingRow - 1, kingCol + 1));
                        }
                    }
                    else {
                        if (attackerCol < kingCol) { // top left
                            trajectoryPositions.add(new ChessPosition(kingRow + 1, kingCol - 1));
                        }
                        else { // top right
                            trajectoryPositions.add(new ChessPosition(kingRow + 1, kingCol + 1));
                        }
                    }
                }
//                if (type == ChessPiece.PieceType.)
            }
            // 1. no (no code needed here)
        }

        return trajectoryPositions;
    }
}
