package amazons;




import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import static amazons.Piece.*;



/** The state of an Amazons Game.
 *  @author Eileen Wang
 */
class Board {

    /** The number of squares on a side of the board. */
    static final int SIZE = 10;
    /**A 2D array representing a game board.*/
    private Piece [][] board;
    /**Number of moves in the game.*/
    private int numMoves;
    /**ArryaList containing all moves in the game.*/
    private List<Move> moves = new ArrayList<>();

    /** Initializes a game board with SIZE squares on a side in the
     *  initial position. */
    Board() {
        init();
    }

    /** Initializes a copy of MODEL. */
    Board(Board model) {
        copy(model);
    }

    /** Copies MODEL into me. */
    void copy(Board model) {
        init();
        this.moves = model.moves;
        this.numMoves = model.numMoves;
        this._turn = model._turn;
        this._winner = model._winner;
        for (int x = 0; x < SIZE; x++) {
            for (int y = 0; y < SIZE; y++) {
                this.board[y][x] = model.board[y][x];
            }
        }
    }

    /** Clears the board to the initial position. */
    void init() {
        board = new Piece[SIZE][SIZE];
        put(WHITE, 0, 3);
        put(WHITE, 3, 0);
        put(WHITE, 6, 0);
        put(WHITE, 9, 3);
        put(BLACK, 0, 6);
        put(BLACK, 3, 9);
        put(BLACK, 6, 9);
        put(BLACK, 9, 6);
        _turn = WHITE;
        _winner = EMPTY;
    }


    /** Return the Piece whose move it is (WHITE or BLACK). */
    Piece turn() {
        return _turn;
    }

    /** Return the number of moves (that have not been undone) for this
     *  board. */
    int numMoves() {
        return numMoves;
    }

    /** Return the winner in the current position, or null if the game is
     *  not yet finished. */
    Piece winner() {
        if (legalMoves().hasNext()) {
            return null;
        } else {
            _winner = _turn.opponent();
            return _winner;
        }
    }

    /** Return the contents the square at S. */
    final Piece get(Square s) {
        return get(s.col(), s.row());
    }

    /** Return the contents of the square at (COL, ROW), where
     *  0 <= COL, ROW <= 9. */
    final Piece get(int col, int row) {
        return board[col][9 - row];
    }

    /** Return the contents of the square at COL ROW. */
    final Piece get(char col, char row) {
        return get(col - 'a', row - '1');
    }

    /** Set square S to P. */
    final void put(Piece p, Square s) {
        put(p, s.col(), s.row());
    }

    /** Set square (COL, ROW) to P. */
    final void put(Piece p, int col, int row) {
        board[col][9 - row] = p;
        _winner = EMPTY;
    }

    /** Set square COL ROW to P. */
    final void put(Piece p, char col, char row) {
        put(p, col - 'a', row - '1');
    }

    /** Return true iff FROM - TO is an unblocked queen move on the current
     *  board, ignoring the contents of ASEMPTY, if it is encountered.
     *  For this to be true, FROM-TO must be a queen move and the
     *  squares along it, other than FROM and ASEMPTY, must be
     *  empty. ASEMPTY may be null, in which case it has no effect. */
    boolean isUnblockedMove(Square from, Square to, Square asEmpty) {
        if (!from.isQueenMove(to)) {
            return false;
        }
        int dir = from.direction(to);
        Square square = from.queenMove(dir, 1);

        while (square.index() != to.index()) {
            if (get(square) == null || get(square) == EMPTY
                    || square.equals(asEmpty)) {
                square = square.queenMove(dir, 1);
            } else {
                return false;
            }
        }
        return get(to) == null || get(to) == EMPTY || to.equals(asEmpty);
    }

    /** Return true iff FROM is a valid starting square for a move. */
    boolean isLegal(Square from) {
        return get(from) == WHITE || get(from) == BLACK;
    }

    /** Return true iff FROM-TO is a valid first part of move, ignoring
     *  spear throwing. */
    boolean isLegal(Square from, Square to) {
        if (isUnblockedMove(from, to, null) && isLegal(from)) {
            return true;
        }
        return false;
    }

    /** Return true iff FROM-TO(SPEAR) is a legal move in the current
     *  position. */
    boolean isLegal(Square from, Square to, Square spear) {
        if (isLegal(from, to) && isUnblockedMove(to, spear, from)) {
            return true;
        }
        return false;

    }

    /** Return true iff MOVE is a legal move in the current
     *  position. */
    boolean isLegal(Move move) {
        return isLegal(move.from(), move.to(), move.spear());
    }

    /** Move FROM-TO(SPEAR), assuming this is a legal move. */
    void makeMove(Square from, Square to, Square spear) {
        if (!isLegal(from, to, spear)) {
            throw new IllegalArgumentException("Not a legal move");
        }
        if (isLegal(from, to, spear)) {
            put(get(from), to);
            put(EMPTY, from);
            put(SPEAR, spear);
            numMoves += 1;

            moves.add(Move.mv(from, to, spear));
            _winner = winner();
            if (turn() == WHITE) {
                _turn = BLACK;
            } else {
                _turn = WHITE;
            }

        }

    }

    /** Move according to MOVE, assuming it is a legal move. */
    void makeMove(Move move) {
        makeMove(move.from(), move.to(), move.spear());
    }

    /** Undo one move.  Has no effect on the initial board. */
    void undo() {
        if (moves.size() > 0) {
            numMoves -= 1;
            Move prevMove = moves.remove(moves.size() - 1);
            put(EMPTY, prevMove.spear());
            put(get(prevMove.to()), prevMove.from());
            put(EMPTY, prevMove.to());
            _turn = _turn.opponent();
        }
        if (winner() != EMPTY) {
            _winner = EMPTY;
        }

    }

    /** Return an Iterator over the Squares that are reachable by an
     *  unblocked queen move from FROM. Does not pay attention to what
     *  piece (if any) is on FROM, nor to whether the game is finished.
vb      *  Treats square ASEMPTY (if non-null) as if it were EMPTY.  (This
     *  feature is useful when looking for Moves, because after moving a
     *  piece, one wants to treat the Square it came from as empty for
     *  purposes of spear throwing.) */
    Iterator<Square> reachableFrom(Square from, Square asEmpty) {
        return new ReachableFromIterator(from, asEmpty);
    }

    /** Return an Iterator over all legal moves on the current board. */
    Iterator<Move> legalMoves() {
        return new LegalMoveIterator(_turn);
    }

    /** Return an Iterator over all legal moves on the current board for
     *  SIDE (regardless of whose turn it is). */
    Iterator<Move> legalMoves(Piece side) {
        return new LegalMoveIterator(side);
    }

    /** An iterator used by reachableFrom. */
    private class ReachableFromIterator implements Iterator<Square> {

        /** Iterator of all squares reachable by queen move from FROM,
         *  treating ASEMPTY as empty. */
        ReachableFromIterator(Square from, Square asEmpty) {
            _from = from;
            _dir = 0;
            _steps = 0;
            _asEmpty = asEmpty;
            toNext();
        }

        @Override
        public boolean hasNext() {
            return _dir < 8;
        }

        @Override
        public Square next() {
            if (hasNext()) {
                int steps = _steps;
                int dir = _dir;
                toNext();
                return _from.queenMove(dir, steps);
            }
            return null;
        }

        /** Advance _dir and _steps, so that the next valid Square is
         *  _steps steps in direction _dir from _from. */
        private void toNext() {
            Square square = _from.queenMove(_dir, _steps + 1);
            if (isUnblockedMove(_from, square, _asEmpty)) {
                _steps += 1;
            } else {
                _steps = 0;
                _dir += 1;
                if (hasNext()) {
                    toNext();
                }
            }
        }

        /** Starting square. */
        private Square _from;
        /** Current direction. */
        private int _dir;
        /** Current distance. */
        private int _steps;
        /** Square treated as empty. */
        private Square _asEmpty;
    }

    /** An iterator used by legalMoves. */
    private class LegalMoveIterator implements Iterator<Move> {

        /** All legal moves for SIDE (WHITE or BLACK). */
        LegalMoveIterator(Piece side) {
            _spearThrows = NO_SQUARES;
            _pieceMoves = NO_SQUARES;
            _fromPiece = side;
            squares = Square.iterator();
            toNext();
        }

        /**Creates an ArrayList of all the starting pieces
         * that returns an ArrayList.*/
        public ArrayList<Square> startingSquares() {
            ArrayList<Square> list = new ArrayList<>();
            for (int i = 0; i < SIZE * SIZE; i++) {
                if (get(Square.sq(i)) == _fromPiece) {
                    list.add(Square.sq(i));
                }
            }
            return list;
        }

        @Override
        public boolean hasNext() {
            return squares.hasNext();
        }

        @Override
        public Move next() {
            Move newMove = Move.mv(_start, _nextSquare, _nextSpear);
            toNext();
            return newMove;
        }

        /** Advance so that the next valid Move is
         *  _start-_nextSquare(sp), where sp is the next value of
         *  _spearThrows. */
        private void toNext() {
            if (!_spearThrows.hasNext()) {
                if (!_pieceMoves.hasNext()) {
                    while (squares.hasNext()) {
                        Square sq = squares.next();
                        if (get(sq) == _fromPiece) {
                            _start = sq;
                            _pieceMoves = reachableFrom(_start, null);
                            _nextSquare = _pieceMoves.next();
                            if (_nextSquare == null) {
                                continue;
                            }
                            _spearThrows = reachableFrom(_nextSquare, _start);
                            _nextSpear = _spearThrows.next();
                            break;
                        }
                    }
                } else {
                    _nextSquare = _pieceMoves.next();
                    _spearThrows = reachableFrom(_nextSquare, _start);
                    _nextSpear = _spearThrows.next();
                }
            } else {
                _nextSpear = _spearThrows.next();
            }

        }

        /**Iterator of all Squares on the board.*/
        private Iterator<Square> squares;
        /**The next spear square from the iterator.*/
        private Square _nextSpear;
        /** Color of side whose moves we are iterating. */
        private Piece _fromPiece;
        /** Current starting square. */
        private Square _start;
        /** Current piece's new position. */
        private Square _nextSquare;
        /** Remaining moves from _start to consider. */
        private Iterator<Square> _pieceMoves;
        /** Remaining spear throws from _piece to consider. */
        private Iterator<Square> _spearThrows;
    }

    @Override
    public String toString() {

        String s = "";
        for (int x = 0; x < SIZE; x++) {
            for (int y = 0; y < SIZE; y++) {
                Piece p = this.board[y][x];
                if (y == 0) {
                    s += "  ";
                }
                if (p == SPEAR) {
                    s += " S";
                } else if (p == WHITE) {
                    s += " W";
                } else if (p == BLACK) {
                    s += " B";
                } else {
                    s += " -";
                }
                if (y == 9) {
                    s += "\n";
                }
            }
        }
        return s;
    }

    /** An empty iterator for initialization. */
    private static final Iterator<Square> NO_SQUARES =
        Collections.emptyIterator();

    /** Piece whose turn it is (BLACK or WHITE). */
    private Piece _turn;
    /** Cached value of winner on this board, or EMPTY if it has not been
     *  computed. */
    private Piece _winner;
}
