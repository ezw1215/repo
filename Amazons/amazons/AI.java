package amazons;


import java.util.Iterator;

import static java.lang.Math.*;

import static amazons.Piece.*;


/** A Player that automatically generates moves.
 *  @author Eileen Wang
 */
class AI extends Player {

    /** A position magnitude indicating a win (for white if positive, black
     *  if negative). */
    private static final int WINNING_VALUE = Integer.MAX_VALUE - 1;
    /** A magnitude greater than a normal value. */
    private static final int INFTY = Integer.MAX_VALUE;

    /** A new AI with no piece or controller (intended to produce
     *  a template). */
    AI() {
        this(null, null);
    }

    /** A new AI playing PIECE under control of CONTROLLER. */
    AI(Piece piece, Controller controller) {
        super(piece, controller);
    }

    @Override
    Player create(Piece piece, Controller controller) {
        return new AI(piece, controller);
    }

    @Override
    String myMove() {
        Move move = findMove();
        _controller.reportMove(move);
        return move.toString();
    }

    /** Return a move for me from the current position, assuming there
     *  is a move. */
    private Move findMove() {
        Board b = new Board(board());
        if (_myPiece == WHITE) {
            findMove(b, maxDepth(b), true, 1, -INFTY, INFTY);
        } else {
            findMove(b, maxDepth(b), true, -1, -INFTY, INFTY);
        }
        return _lastFoundMove;
    }

    /** The move found by the last call to one of the ...FindMove methods
     *  below. */
    private Move _lastFoundMove;

    /** Find a move from position BOARD and return its value, recording
     *  the move found in _lastFoundMove iff SAVEMOVE. The move
     *  should have maximal value or have value > BETA if SENSE==1,
     *  and minimal value or value < ALPHA if SENSE==-1. Searches up to
     *  DEPTH levels.  Searching at level 0 simply returns a static estimate
     *  of the board value and does not set _lastMoveFound. */
    private int findMove(Board board, int depth, boolean saveMove, int sense,
                         int alpha, int beta) {
        if (depth == 0 || board.winner() != null) {
            return staticScore(board);
        }
        Iterator<Move> allMoves = board.legalMoves();
        Move value = null;
        int bestValue;
        if (sense == 1) {
            bestValue = -INFTY;
        } else {
            bestValue = INFTY;
        }
        if (sense == 1) {
            while (allMoves.hasNext()) {
                Move next = allMoves.next();
                board.makeMove(next);
                int response = findMove(board, depth - 1, false, 0 - sense,
                        alpha, beta);
                board.undo();
                if (response >= bestValue) {
                    value = next;
                    bestValue = response;
                    alpha = Math.max(alpha, response);
                    if (beta <= alpha) {
                        break;
                    }
                }
            }

        } else if (sense == -1) {
            while (allMoves.hasNext()) {
                Move next = allMoves.next();
                board.makeMove(next);
                int response = findMove(board, depth - 1, saveMove, 0 - sense,
                        alpha, beta);
                board.undo();
                if (response <= bestValue) {
                    value = next;
                    bestValue = response;
                    beta = Math.min(beta, response);
                    if (beta <= alpha) {
                        break;
                    }
                }
            }

        }
        if (saveMove) {
            _lastFoundMove = value;
        }

        return bestValue;
    }

    /** Return a heuristically determined maximum search depth
     *  based on characteristics of BOARD. */
    private int maxDepth(Board board) {
        int N = board.numMoves();
        if (N < FOURTH_LIMIT) {
            return 1;
        } else if (N < FIRST_LIMIT) {
            return 2;
        } else if (N < SECOND_LIMIT) {
            return 3;
        } else if (N < THIRD_LIMIT) {
            return 4;
        } else {
            return 5;
        }

    }


    /** Return a heuristic value for BOARD. */
    private int staticScore(Board board) {
        Piece winner = board.winner();
        if (winner == BLACK) {
            return -WINNING_VALUE;
        } else if (winner == WHITE) {
            return WINNING_VALUE;
        }

        int whites = 0;
        int blacks = 0;
        Iterator<Move> allWhiteMoves = board.legalMoves(WHITE);
        Iterator<Move> allBlackMoves = board.legalMoves(BLACK);

        while (allBlackMoves.hasNext()) {
            allBlackMoves.next();
            blacks += 1;
        }

        while (allWhiteMoves.hasNext()) {
            allWhiteMoves.next();
            whites += 1;
        }
        return whites - blacks;
    }


    /**A limit for maxDepth.*/
    public static final int FIRST_LIMIT = 100;
    /**A limit for maxDepth.*/
    public static final int SECOND_LIMIT = 150;
    /**A limit for maxDepth.*/
    public static final int THIRD_LIMIT = 200;
    /**A limit for maxDepth.*/
    public static final int FOURTH_LIMIT = 50;


}
