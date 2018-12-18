package enigma;

import static enigma.EnigmaException.*;

/** Class that represents a rotating rotor in the enigma machine.
 *  @author Eileen Wang
 */
class MovingRotor extends Rotor {

    /** A rotor named NAME whose permutation in its default setting is
     *  PERM, and whose notches are at the positions indicated in NOTCHES.
     *  The Rotor is initally in its 0 setting (first character of its
     *  alphabet).
     */
    MovingRotor(String name, Permutation perm, String notches) {
        super(name, perm);
        _notches = notches;

    }

    @Override
    boolean atNotch() {
        Alphabet alphabet = permutation().alphabet();
        char settingChar = alphabet.toChar(this.setting());
        return _notches.indexOf(settingChar) != -1;
    }

    @Override
    boolean rotates() {
        return true;
    }

    @Override
    void advance() {
        int newSetting = this.setting() + 1;

        int r = newSetting % size();
        if (r < 0) {
            r += size();
        }
        this.set(r);
    }

    /** The notches on the moving rotor. **/
    private String _notches;

}
