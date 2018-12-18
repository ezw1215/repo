package enigma;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;

import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.Scanner;

import static enigma.EnigmaException.*;

/** Enigma simulator.
 *  @author Eileen Wang
 */
public final class Main {

    /** Process a sequence of encryptions and decryptions, as
     *  specified by ARGS, where 1 <= ARGS.length <= 3.
     *  ARGS[0] is the name of a configuration file.
     *  ARGS[1] is optional; when present, it names an input file
     *  containing messages.  Otherwise, input comes from the standard
     *  input.  ARGS[2] is optional; when present, it names an output
     *  file for processed messages.  Otherwise, output goes to the
     *  standard output. Exits normally if there are no errors in the input;
     *  otherwise with code 1. */
    public static void main(String... args) {
        try {
            new Main(args).process();
            return;
        } catch (EnigmaException excp) {
            System.err.printf("Error: %s%n", excp.getMessage());
        }
        System.exit(1);
    }

    /** Check ARGS and open the necessary files (see comment on main). */
    Main(String[] args) {
        if (args.length < 1 || args.length > 3) {
            throw error("Only 1, 2, or 3 command-line arguments allowed");
        }

        _config = getInput(args[0]);

        if (args.length > 1) {
            _input = getInput(args[1]);
        } else {
            _input = new Scanner(System.in);
        }

        if (args.length > 2) {
            _output = getOutput(args[2]);
        } else {
            _output = System.out;
        }
    }

    /** Return a Scanner reading from the file named NAME. */
    private Scanner getInput(String name) {
        try {
            return new Scanner(new File(name));
        } catch (IOException excp) {
            throw error("could not open %s", name);
        }
    }

    /** Return a PrintStream writing to the file named NAME. */
    private PrintStream getOutput(String name) {
        try {
            return new PrintStream(new File(name));
        } catch (IOException excp) {
            throw error("could not open %s", name);
        }
    }

    /** Configure an Enigma machine from the contents of configuration
     *  file _config and apply it to the messages in _input, sending the
     *  results to _output. */
    private void process() {
        Machine myMachine = readConfig();
        int numberRotors = myMachine.numRotors();
        if (!_input.hasNext("\\*")) {
            throw new EnigmaException("Wrong configuration");
        }
        while (_input.hasNextLine()) {
            String line = _input.nextLine();
            Scanner scanLine = new Scanner(line);
            String cycles = "";
            String settings = "";
            String message = "";
            if (scanLine.hasNext("\\*")) {
                scanLine.next();
                int i = 0;
                String[] rotors = new String[numberRotors];
                while (i < numberRotors) {
                    rotors[i] = scanLine.next();
                    i++;
                }
                if (i != numberRotors) {
                    throw new EnigmaException("Wrong number of rotors");
                }
                settings += scanLine.next();
                if (scanLine.hasNext()) {
                    cycles = line.trim().substring(line.indexOf("("));
                }
                myMachine.insertRotors(rotors);
                if (!myMachine.machine().get(0).reflecting()) {
                    throw new EnigmaException("Reflector in the wrong place");
                }
                myMachine.setRotors(settings);
                myMachine.setPlugboard(new Permutation(cycles, _alphabet));
            } else {
                if (line.equals("")) {
                    _output.println();
                } else {
                    message = line.trim().replaceAll(" ", "");
                    message = myMachine.convert(message.toUpperCase());
                    printMessageLine(message);
                }
            }
        }
    }

    /** Return an Enigma machine configured from the contents of configuration
     *  file _config. */
    private Machine readConfig() {
        try {
            ArrayList<Rotor> allRotors = new ArrayList<>();
            String nextRotor = "";
            String line = _config.nextLine().trim();

            if (line.split(" ").length != 1 || line.contains("(")
                    || line.contains(")") || line.contains("*")) {
                throw new EnigmaException("Wrong formatting of config");
            }
            _alphabet = new CharacterRange(line.charAt(0),
                    line.charAt(line.length() - 1));
            if (!_config.hasNextInt()) {
                throw new EnigmaException("Missing number of Rotors");
            }
            int numRotors = Integer.parseInt(_config.next("[0-9]*"));
            if (!_config.hasNextInt()) {
                throw new EnigmaException("Missing number of Pawls");
            }
            int numPawls = Integer.parseInt(_config.next("[0-9]*"));
            _config.nextLine();
            while (_config.hasNextLine()) {
                String rotor = _config.nextLine().trim();
                if (_config.hasNextLine()) {
                    nextRotor = _config.nextLine().trim();
                }
                if (!rotor.endsWith(")") || !nextRotor.endsWith(")")) {
                    throw new EnigmaException("Cycles are incomplete");
                }
                if (nextRotor.startsWith("(")) {
                    rotor += " ";
                    rotor += nextRotor;
                    Rotor r = readRotor(rotor);
                    allRotors.add(r);
                } else {
                    Rotor r1 = readRotor(rotor);
                    allRotors.add(r1);
                    Rotor r2 = readRotor(nextRotor);
                    allRotors.add(r2);
                }

            }
            return new Machine(_alphabet, numRotors, numPawls, allRotors);
        } catch (NoSuchElementException excp) {
            throw error("configuration file truncated");
        }
    }

    /** Return a  ROTOR, reading its description from _config. */
    private Rotor readRotor(String rotor) {
        try {
            Scanner scanRotor = new Scanner(rotor);
            String cycles = "";
            String name = scanRotor.next();
            String details = scanRotor.next();
            cycles += rotor.substring(rotor.indexOf("("));

            if (details.charAt(0) == 'M') {
                String notches = details.substring(1);
                return new MovingRotor(name,
                        new Permutation(cycles, _alphabet), notches);
            } else if (details.charAt(0) == 'R') {
                return new Reflector(name, new Permutation(cycles, _alphabet));
            } else {
                return new FixedRotor(name, new Permutation(cycles, _alphabet));
            }
        } catch (NoSuchElementException excp) {
            throw error("bad rotor description");
        }
    }

    /** Set M according to the specification given on SETTINGS,
     *  which must have the format specified in the assignment. */
    private void setUp(Machine M, String settings) {
        M.setRotors(settings);
    }

    /** Print MSG in groups of five (except that the last group may
     *  have fewer letters). */
    private void printMessageLine(String msg) {
        int next = 0;
        for (char letter: msg.toCharArray()) {
            if (next == 5) {
                _output.print(" ");
                next = 0;
            }
            _output.print(letter);
            next++;

        }
        _output.println();

    }

    /** Alphabet used in this machine. */
    private Alphabet _alphabet;

    /** Source of input messages. */
    private Scanner _input;

    /** Source of machine configuration. */
    private Scanner _config;

    /** File for encoded/decoded messages. */
    private PrintStream _output;
}
