package enigma;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import static enigma.EnigmaException.*;

/** Class that represents a complete enigma machine.
 *  @author Eileen Wang
 */
class Machine {



    /**Number of rotors in machine.*/
    private int numberRotors;
    /**Number of pawls in machine.*/
    private int numPawls;
    /**ArrayList representing machine.*/
    private ArrayList<Rotor> machine;
    /**Collection of all available rotors.*/
    private Collection<Rotor> _allRotors;
    /**The plugboard.*/
    private Permutation _plugboard;

    /** A new Enigma machine with alphabet ALPHA, 1 < NUMROTORS rotor slots,
     *  and 0 <= PAWLS < NUMROTORS pawls.  ALLROTORS contains all the
     *  available rotors. */
    Machine(Alphabet alpha, int numRotors, int pawls,
            Collection<Rotor> allRotors) {
        _alphabet = alpha;
        numberRotors = numRotors;
        numPawls = pawls;
        machine = new ArrayList<>();
        _allRotors = allRotors;
        _plugboard = new Permutation("", _alphabet);


    }

    /** Return the number of rotor slots I have. */
    int numRotors() {
        return numberRotors;
    }

    /** Return the number pawls (and thus rotating rotors) I have. */
    int numPawls() {
        return numPawls;
    }

    /** Returns the rotors contained within the machine.*/
    ArrayList<Rotor> machine() {
        return machine;
    }

    /** Set my rotor slots to the rotors named ROTORS from my set of
     *  available rotors (ROTORS[0] names the reflector).
     *  Initially, all rotors are set at their 0 setting. */
    void insertRotors(String[] rotors) {
        int i = 0;
        int pawlCount = 0;
        machine = new ArrayList<>();
        while (i < rotors.length) {
            for (Rotor r: _allRotors) {
                if (r.name().toUpperCase().equals(rotors[i].toUpperCase())) {
                    machine.add(r);
                    if (r.rotates()) {
                        pawlCount++;
                    }
                }
            }
            i++;
        }

        if (rotors.length != machine.size()) {
            throw new EnigmaException("Bad rotor name");
        }
        if (pawlCount != numPawls()) {
            throw new EnigmaException("Wrong number of moving rotors");
        }
        long distinct = Arrays.stream(machine.toArray()).distinct().count();
        if ((int) distinct != numRotors()) {
            throw new EnigmaException("Duplicate rotors");
        }

    }

    /** Set my rotors according to SETTING, which must be a string of
     *  numRotors()-1 upper-case letters. The first letter refers to the
     *  leftmost rotor setting (not counting the reflector).  */
    void setRotors(String setting) {
        if (setting.length() < numRotors() - 1
                || setting.length() > numRotors() - 1) {
            throw new EnigmaException("Wrong number of settings");
        }
        for (int i = 1; i < machine.size(); i++) {
            char set = setting.charAt(0);
            if (!_alphabet.contains(set)) {
                throw new EnigmaException("Setting not in alphabet");
            }
            machine.get(i).set(set);
            setting = setting.substring(1);
        }
    }

    /** Set the plugboard to PLUGBOARD. */
    void setPlugboard(Permutation plugboard) {
        _plugboard = plugboard;
    }

    /** Returns the result of converting the input character C (as an
     *  index in the range 0..alphabet size - 1), after first advancing
     *  the machine. */
    int convert(int c) {

        boolean[] advanced = new boolean[machine.size()];
        advanced[advanced.length - 1] = true;
        for (int k = machine.size() - 2; k > 0; k--) {
            Rotor r = machine.get(k);
            if ((r.atNotch() && machine.get(k - 1).rotates())
                    || machine.get(k + 1).atNotch() && !advanced[k]) {
                if (r.rotates()) {
                    advanced[k] = true;
                }
            }

        }

        for (int m = 0; m < machine.size(); m++) {
            if (advanced[m]) {
                machine.get(m).advance();
            }
        }
        if (!_alphabet.contains(_alphabet.toChar(wrap(c)))) {
            throw new EnigmaException("Alphabet does not contain letter");
        }
        int result = _plugboard.permute(wrap(c));

        for (int n = machine.size() - 1; n > -1; n--) {
            result = machine.get(n).convertForward(result);
        }

        for (int i = 1; i < machine.size(); i++) {
            result = machine.get(i).convertBackward(result);
        }
        result = _plugboard.permute(result);
        return result;


    }

    /** Returns the encoding/decoding of MSG, updating the state of
     *  the rotors accordingly. */
    String convert(String msg) {
        msg = msg.toUpperCase();
        String result = "";
        for (int i = 0; i < msg.length(); i++) {
            char word = msg.charAt(i);
            if (word == ' ') {
                result += word;
            } else {
                result += _alphabet.toChar(convert(_alphabet.toInt(word)));
            }
        }
        return result;
    }

    /** Return the value of P modulo the size of the alphabet. */
    final int wrap(int p) {
        int r = p % _alphabet.size();
        if (r < 0) {
            r += _alphabet.size();
        }
        return r;
    }

    /** Common alphabet of my rotors. */
    private final Alphabet _alphabet;
}
