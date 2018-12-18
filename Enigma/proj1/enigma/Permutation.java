package enigma;

import java.util.ArrayList;
import java.util.List;

import static enigma.EnigmaException.*;

/** Represents a permutation of a range of integers starting at 0 corresponding
 *  to the characters of an alphabet.
 *  @author Eileen Wang
 */
class Permutation {



    /** Set this Permutation to that specified by CYCLES, a string in the
     *  form "(cccc) (cc) ..." where the c's are characters in ALPHABET, which
     *  is interpreted as a permutation in cycle notation.  Characters in the
     *  alphabet that are not included in any cycle map to themselves.
     *  Whitespace is ignored. */
    Permutation(String cycles, Alphabet alphabet) {
        _alphabet = alphabet;
        _cycles = new ArrayList<>();

        while (cycles.trim().length() != 0) {
            int start = cycles.indexOf('(');
            int end = cycles.indexOf(')');
            _cycles.add(cycles.substring(start + 1, end));
            cycles = cycles.substring(end + 1);

        }
    }

    /** Add the cycle c0->c1->...->cm->c0 to the permutation, where CYCLE is
     *  c0c1...cm. */
    private void addCycle(String cycle) {
        _cycles.add(cycle);
    }

    /** Return the value of P modulo the size of this permutation. */
    final int wrap(int p) {
        int r = p % size();
        if (r < 0) {
            r += size();
        }
        return r;
    }

    /** Returns the size of the alphabet I permute. */
    int size() {
        return alphabet().size();
    }

    /** Return the result of applying this permutation to P modulo the
     *  alphabet size. */
    int permute(int p) {
        char begChar = _alphabet.toChar(wrap(p));
        char newChar;

        for (int i = 0; i < _cycles.size(); i++) {
            String word = _cycles.get(i);
            int begIndex = _cycles.get(i).indexOf(begChar);
            if (begIndex != -1) {
                if (begIndex != word.length() - 1) {
                    newChar = word.charAt(begIndex + 1);
                } else {
                    newChar = word.charAt(0);
                }
                return  _alphabet.toInt(newChar);
            }
        }
        return wrap(p);
    }

    /** Return the result of applying the inverse of this permutation
     *  to  C modulo the alphabet size. */
    int invert(int c) {
        char begChar = _alphabet.toChar(wrap(c));
        char newChar;

        for (int i = 0; i < _cycles.size(); i++) {
            String word = _cycles.get(i);
            int begIndex = _cycles.get(i).indexOf(begChar);
            if (begIndex != -1) {
                if (begIndex != 0) {
                    newChar = word.charAt(begIndex - 1);
                } else {
                    newChar = word.charAt(word.length() - 1);
                }
                return  _alphabet.toInt(newChar);
            }
        }
        return wrap(c);
    }

    /** Return the result of applying this permutation to the index of P
     *  in ALPHABET, and converting the result to a character of ALPHABET. */
    char permute(char p) {
        return _alphabet.toChar(permute((_alphabet.toInt(p))));
    }

    /** Return the result of applying the inverse of this permutation to C. */
    char invert(char c) {
        return _alphabet.toChar(invert(_alphabet.toInt(c)));
    }

    /** Return the alphabet used to initialize this Permutation. */
    Alphabet alphabet() {
        return _alphabet;
    }

    /** Return true iff this permutation is a derangement (i.e., a
     *  permutation for which no value maps to itself). */
    boolean derangement() {
        for (int i = 0; i < size(); i++) {
            char word = _alphabet.toChar(i);
            if (permute(word) == word || invert(word) == word) {
                return false;
            }
        }
        return true;
    }

    /** Alphabet of this permutation. */
    private Alphabet _alphabet;
    /**ArrayList containing the cycles of the Permutation.**/
    private List<String> _cycles;
}
