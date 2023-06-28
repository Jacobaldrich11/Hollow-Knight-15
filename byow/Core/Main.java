package byow.Core;

import java.io.IOException;

/** This is the main entry point for the program. This class simply parses
 *  the command line inputs, and lets the byow.Core.Engine class take over
 *  in either keyboard or input string mode.
 */
public class Main {
    public static void main(String[] args) throws Exception {
            Engine engine = new Engine();
            engine.interactWithKeyboard();
    }
}
