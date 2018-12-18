package enigma;

import org.junit.Test;
import java.util.ArrayList;
import java.util.Arrays;
import static org.junit.Assert.*;


public class MachineTest {

    @Test
    public void testDoubleStep() {
        Alphabet ac = new CharacterRange('A', 'D');
        Rotor one = new Reflector("R1", new Permutation("(AC) (BD)", ac));
        Rotor two = new MovingRotor("R2", new Permutation("(ABCD)", ac), "C");
        Rotor three = new MovingRotor("R3", new Permutation("(ABCD)", ac), "C");
        Rotor four = new MovingRotor("R4", new Permutation("(ABCD)", ac), "C");
        String setting = "AAA";
        Rotor[] machineRotors = {one, two, three, four};
        String[] rotors = {"R1", "R2", "R3", "R4"};
        Machine mach = new Machine(ac, 4, 3,
                new ArrayList<>(Arrays.asList(machineRotors)));
        mach.insertRotors(rotors);
        mach.setRotors(setting);
        mach.setPlugboard(new Permutation("", ac));

        assertEquals("AAAA", getSetting(ac, machineRotors));
        mach.convert('A');
        assertEquals("AAAB", getSetting(ac, machineRotors));
        mach.convert('A');
        assertEquals("AAAC", getSetting(ac, machineRotors));
        mach.convert('A');
        assertEquals("AABD", getSetting(ac, machineRotors));
        mach.convert('A');
        assertEquals("AABA", getSetting(ac, machineRotors));
        mach.convert('A');
        assertEquals("AABB", getSetting(ac, machineRotors));
        mach.convert('A');
        assertEquals("AABC", getSetting(ac, machineRotors));
        mach.convert('A');
        assertEquals("AACD", getSetting(ac, machineRotors));
        mach.convert('A');
        assertEquals("ABDA", getSetting(ac, machineRotors));
    }

    /** Helper method to get the String representation
     * of the current Rotor settings */
    private String getSetting(Alphabet alph, Rotor[] machineRotors) {
        String currSetting = "";
        for (Rotor r : machineRotors) {
            currSetting += alph.toChar(r.setting());
        }
        return currSetting;
    }

    @Test
    public void testConvert() {
        Alphabet alpha = new CharacterRange('A', 'Z');
        Rotor b = new Reflector("B",
                new Permutation("(AE) (BN) (CK) (DQ) (FU) "
                        + "(GY) (HW) (IJ) (LO) (MP)(RX) (SZ) (TV)", alpha));
        Rotor beta = new FixedRotor("Beta",
                new Permutation("(ALBEVFCYODJWUGNMQTZSKPR) (HIX)", alpha));
        Rotor three = new MovingRotor("Three",
                new Permutation("(ABDHPEJT) "
                        + "(CFLVMZOYQIRWUKXSG) (N)", alpha), "V");
        Rotor four = new MovingRotor("Four",
                new Permutation("(AEPLIYWCOXMRFZBSTGJQNH) "
                        + "(DV) (KU)", alpha), "J");
        Rotor one = new MovingRotor("One",
                new Permutation("(AELTPHQXRU) (BKNW) (CMOY) "
                        + "(DFG) (IV) (JZ) (S)", alpha), "Q");
        String setting = "AXLE";
        Rotor[] machineRotors = {b, beta, three, four, one};
        String[] rotors = {"B", "Beta", "Three", "Four", "One"};
        Machine mach = new Machine(alpha, 5, 3,
                new ArrayList<>(Arrays.asList(machineRotors)));
        mach.insertRotors(rotors);
        mach.setRotors(setting);
        mach.setPlugboard(new Permutation("(YF) (ZH)", alpha));

        assertEquals("AAXLE", getSetting(alpha, machineRotors));
        assertEquals(25, mach.convert(24));

    }


    @Test
    public void testConvertString() {
        Alphabet alpha = new CharacterRange('A', 'Z');
        Rotor b = new Reflector("B",
                new Permutation("(AE) (BN) (CK) (DQ) (FU) (GY) "
                        + "(HW) (IJ) (LO) (MP)(RX) (SZ) (TV)", alpha));
        Rotor beta = new FixedRotor("Beta",
                new Permutation("(ALBEVFCYODJWUGNMQTZSKPR)"
                        + " (HIX)", alpha));
        Rotor three = new MovingRotor("Three",
                new Permutation("(ABDHPEJT) "
                        + "(CFLVMZOYQIRWUKXSG) (N)", alpha), "V");
        Rotor four = new MovingRotor("Four",
                new Permutation("(AEPLIYWCOXMRFZBSTGJQNH) "
                        + "(DV) (KU)", alpha), "J");
        Rotor one = new MovingRotor("One",
                new Permutation("(AELTPHQXRU) (BKNW) "
                        + "(CMOY) (DFG) (IV) (JZ) (S)", alpha), "Q");
        String setting = "AXLE";
        Rotor[] machineRotors = {b, beta, three, four, one};
        String[] rotors = {"B", "Beta", "Three", "Four", "One"};
        Machine mach = new Machine(alpha, 5, 3,
                new ArrayList<>(Arrays.asList(machineRotors)));
        mach.insertRotors(rotors);
        mach.setRotors(setting);
        mach.setPlugboard(new Permutation("(HQ) (EX) (IP) (TR) (BY)", alpha));

        assertEquals("AAXLE", getSetting(alpha, machineRotors));
        assertEquals("QVPQSOKOILPUBKJZPISFXDW",
                mach.convert("FROMhisshoulderHiawatha"));

    }


    @Test
    public void testPrint() {
        String s = "ABCDEFGH";

    }




}
