package amazons;

import org.junit.Test;
import static org.junit.Assert.*;
import ucb.junit.textui;
import static amazons.Piece.*;

/** The suite of all JUnit tests for the enigma package.
 *  @author
 */
public class UnitTest {

    /** Run the JUnit tests in this package. Add xxxTest.class entries to
     *  the arguments of runClasses to run other JUnit tests. */
    public static void main(String[] ignored) {
        textui.runClasses(UnitTest.class, IteratorTests.class);
    }

    /** A dummy test as a placeholder for real ones. */


    /** Tests basic correctness of put and get on the initialized board. */
    @Test
    public void testBasicPutGet() {
        Board b = new Board();
        b.put(BLACK, Square.sq(3, 5));
        assertEquals(b.get(3, 5), BLACK);
        b.put(WHITE, Square.sq(9, 9));
        assertEquals(b.get(9, 9), WHITE);
        b.put(EMPTY, Square.sq(3, 5));
        assertEquals(b.get(3, 5), EMPTY);
    }

    /** Tests proper identification of legal/illegal queen moves. */
    @Test
    public void testIsQueenMove() {
        assertFalse(Square.sq(1, 5).isQueenMove(Square.sq(1, 5)));
        assertFalse(Square.sq(1, 5).isQueenMove(Square.sq(2, 7)));
        assertFalse(Square.sq(0, 0).isQueenMove(Square.sq(5, 1)));
        assertTrue(Square.sq(1, 1).isQueenMove(Square.sq(9, 9)));
        assertTrue(Square.sq(2, 7).isQueenMove(Square.sq(8, 7)));
        assertTrue(Square.sq(3, 0).isQueenMove(Square.sq(3, 4)));
        assertTrue(Square.sq(7, 9).isQueenMove(Square.sq(0, 2)));
    }

    /** Tests toString for initial board state and a smiling board state. :) */
    @Test
    public void testToString() {
        Board b = new Board();
        assertEquals(INIT_BOARD_STATE, b.toString());
        makeSmile(b);
        assertEquals(SMILE, b.toString());
    }

    private void makeSmile(Board b) {
        b.put(EMPTY, Square.sq(0, 3));
        b.put(EMPTY, Square.sq(0, 6));
        b.put(EMPTY, Square.sq(9, 3));
        b.put(EMPTY, Square.sq(9, 6));
        b.put(EMPTY, Square.sq(3, 0));
        b.put(EMPTY, Square.sq(3, 9));
        b.put(EMPTY, Square.sq(6, 0));
        b.put(EMPTY, Square.sq(6, 9));
        for (int col = 1; col < 4; col += 1) {
            for (int row = 6; row < 9; row += 1) {
                b.put(SPEAR, Square.sq(col, row));
            }
        }
        b.put(EMPTY, Square.sq(2, 7));
        for (int col = 6; col < 9; col += 1) {
            for (int row = 6; row < 9; row += 1) {
                b.put(SPEAR, Square.sq(col, row));
            }
        }
        b.put(EMPTY, Square.sq(7, 7));
        for (int lip = 3; lip < 7; lip += 1) {
            b.put(WHITE, Square.sq(lip, 2));
        }
        b.put(WHITE, Square.sq(2, 3));
        b.put(WHITE, Square.sq(7, 3));
    }

    static final String INIT_BOARD_STATE =
            "   - - - B - - B - - -\n"
                    + "   - - - - - - - - - -\n"
                    + "   - - - - - - - - - -\n"
                    + "   B - - - - - - - - B\n"
                    + "   - - - - - - - - - -\n"
                    + "   - - - - - - - - - -\n"
                    + "   W - - - - - - - - W\n"
                    + "   - - - - - - - - - -\n"
                    + "   - - - - - - - - - -\n"
                    + "   - - - W - - W - - -\n";

    static final String SMILE =
            "   - - - - - - - - - -\n"
                    + "   - S S S - - S S S -\n"
                    + "   - S - S - - S - S -\n"
                    + "   - S S S - - S S S -\n"
                    + "   - - - - - - - - - -\n"
                    + "   - - - - - - - - - -\n"
                    + "   - - W - - - - W - -\n"
                    + "   - - - W W W W - - -\n"
                    + "   - - - - - - - - - -\n"
                    + "   - - - - - - - - - -\n";


    @Test
    public void testDirection() {
        Square sq1 = Square.sq(0, 3);
        Square sq2 = Square.sq(0, 6);
        Square sq3 = Square.sq(3, 3);
        Square sq4 = Square.sq(4, 4);
        Square sq5 = Square.sq(2, 2);
        Square sq6 = Square.sq(3, 2);
        Square sq7 = Square.sq(4, 2);
        assertEquals(0, sq1.direction(sq2));
        assertEquals(1, sq3.direction(sq4));
        assertEquals(5, sq3.direction(sq5));
        assertEquals(4, sq3.direction(sq6));
        assertEquals(3, sq3.direction(sq7));
    }

    @Test
    public void testQueenMove() {
        Square sq1 = Square.sq(0, 3);
        Square sq2 = Square.sq(0, 6);
        Square sq3 = Square.sq(3, 3);
        Square sq4 = Square.sq(4, 4);
        Square sq5 = Square.sq(2, 2);
        Square sq6 = Square.sq(3, 2);
        Square sq7 = Square.sq(4, 2);
        Square sq8 = Square.sq(1, 5);
        assertEquals(sq2, sq1.queenMove(0, 3));
        assertEquals(sq4, sq3.queenMove(1, 1));
        assertEquals(sq5, sq3.queenMove(5, 1));
        assertEquals(sq6, sq3.queenMove(4, 1));
        assertEquals(sq7, sq3.queenMove(3, 1));
        assertEquals(sq8, sq3.queenMove(7, 2));

    }

    @Test
    public void testSquares() {
        Square sq1 = Square.sq(0, 3);
        Square sq2 = Square.sq(6, 6);
        Square sq3 = Square.sq(3, 3);
        assertEquals(sq1, Square.sq("a", "4"));
        assertEquals(sq2, Square.sq("g7"));
        assertEquals(sq3, Square.sq(3, 3));
    }

    @Test
    public void testUnblockedMove() {
        Board b = new Board();
        Square sq1 = Square.sq(0, 3);
        Square sq2 = Square.sq(0, 6);
        Square sq3 = Square.sq(2, 5);
        assertFalse(b.isUnblockedMove(sq1, sq2, null));
        assertTrue(b.isUnblockedMove(sq1, sq3, null));
        assertTrue(b.isUnblockedMove(sq1, sq2, sq2));
    }

    @Test
    public void testMakeMove() {
        Board b = new Board();
        Square sq1 = Square.sq(0, 3);
        Square sq2 = Square.sq(0, 4);
        Square sq3 = Square.sq(2, 4);
        b.makeMove(sq1, sq2, sq3);
        assertTrue(b.get(sq2) == WHITE);
    }

}


