package byow.lab13;

import byow.Core.RandomUtils;
import edu.princeton.cs.introcs.StdDraw;

import java.awt.Color;
import java.awt.Font;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class MemoryGame {
    /** The width of the window of this game. */
    private int width;
    /** The height of the window of this game. */
    private int height;
    /** The current round the user is on. */
    private int round;
    /** The Random object used to randomly generate Strings. */
    private Random rand;
    /** Whether or not the game is over. */
    private boolean gameOver;
    /** Whether or not it is the player's turn. Used in the last section of the
     * spec, 'Helpful UI'. */
    private boolean playerTurn;
    /** The characters we generate random Strings from. */
    private static final char[] CHARACTERS = "abcdefghijklmnopqrstuvwxyz".toCharArray();
    /** Encouraging phrases. Used in the last section of the spec, 'Helpful UI'. */
    private static final String[] ENCOURAGEMENT = {"You can do this!", "I believe in you!",
                                                   "You got this!", "You're a star!", "Go Bears!",
                                                   "Too easy for you!", "Wow, so impressive!"};

    public static void main(String[] args) throws InterruptedException {
        if (args.length < 1) {
            System.out.println("Please enter a seed");
            return;
        }

        long seed = Long.parseLong(args[0]);
        MemoryGame game = new MemoryGame(40, 40, seed);
    }

    public MemoryGame(int width, int height, long seed) throws InterruptedException {
        /* Sets up StdDraw so that it has a width by height grid of 16 by 16 squares as its canvas
         * Also sets up the scale so the top left is (0,0) and the bottom right is (width, height)
         */
        this.width = width;
        this.height = height;
        StdDraw.setCanvasSize(this.width * 16, this.height * 16);
        Font font = new Font("Monaco", Font.BOLD, 30);
        StdDraw.setFont(font);
        StdDraw.setXscale(0, this.width);
        StdDraw.setYscale(0, this.height);
        StdDraw.clear(Color.BLACK);
        StdDraw.enableDoubleBuffering();
        startGame(seed);
    }

    public String generateRandomString(int n, long seed) {
        Random r = new Random(seed);
        String s = "";
        for (int i = 0; i < n; i += 1) {
            RandomUtils.shuffle(r, CHARACTERS);
            s = s + CHARACTERS[0];
        }
        return s;
    }

    public void drawFrame(String s) {
        StdDraw.clear(Color.BLACK);
        StdDraw.setPenColor(Color.WHITE);
        StdDraw.text(20, 20, s);
        StdDraw.show();
    }

    public void flashSequence(String letters) throws InterruptedException {
        char[] lettersArray = letters.toCharArray();
        for (char c : lettersArray) {
            String cString = Character.toString(c);
            drawFrame(cString);
            TimeUnit.SECONDS.sleep(1);
            StdDraw.clear(Color.BLACK);
            StdDraw.show();
            TimeUnit.MILLISECONDS.sleep(500);
        }
    }

    public String solicitNCharsInput(int n) throws InterruptedException {
        String s = "";
        while (true) {
            if (StdDraw.hasNextKeyTyped()) {
                s += StdDraw.nextKeyTyped();
                drawFrame(s);
            }
            if (s.length() == n) {
                TimeUnit.SECONDS.sleep(1);
                return s;
            }
        }
    }

    public void startGame(long seed) throws InterruptedException {
        int round = 1;

        while (true) {
            drawFrame("Round: " + round);
            TimeUnit.SECONDS.sleep(3);
            String s = generateRandomString(round, seed);
            flashSequence(s);
            String h = solicitNCharsInput(round);
            if (h.equals(s)) {
                round += 1;
            } else {
                StdDraw.setFont(new Font("Monaco", Font.BOLD, 24));
                drawFrame("Game over! You made it to round " + round + "!");
                TimeUnit.SECONDS.sleep(5);
                System.exit(0);
            }
        }
    }

}
