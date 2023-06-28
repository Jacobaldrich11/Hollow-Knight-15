package byow.Core;

import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;
import edu.princeton.cs.introcs.StdDraw;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.io.Serializable;
import java.io.File;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.FileOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.awt.Color;
import java.awt.Font;


public class Engine implements Serializable {
    /* Feel free to change the width and height. */
    private static final File CWD = new File(System.getProperty("user.dir"));
    private static final int WIDTH = 80;
    private static final int HEIGHT = 40;
    private static final int COINS = 15;
    private static final int TOTAL_MOVES = 300;
    private TETile[][] voidWorld;
    private TETile[][] world;
    private TETile[][] darkWorld;
    private int avatarX;
    private int avatarY;
    private int coinCount = 0;
    private int moves = 0;
    private int lights = 0;
    private long seed;
    private ArrayList moveArray = new ArrayList<Character>();

    /**
     * Method used for exploring a fresh world. This method should handle all inputs,
     * including inputs from the main menu.
     */
    public void interactWithKeyboard() throws InterruptedException {
        // s returns the input string that the player typed.
        // s can also return l if the player wants to load, or e to exit.
        String s = projectGame(WIDTH, HEIGHT);
        if (s.equals("l")) {
            // Load game.
            loadGame();
            Random r = new Random(seed);
            initializeWorld();
            initializeVoidWorld();
            drawRandomTileMap(r);
            placeCharacter(world);
            placeManySpecialTiles(world, r);
            for (int i = 0; i < moveArray.size(); i += 1) {
                Character c = (Character) moveArray.get(i);
                if (c.equals('f')) {
                    lights += 1;
                } else {
                    moveFunction(c, world);
                    moveArray.remove(moveArray.size() - 1);
                }
            }
        } else if (s.equals("e")) {
            // Exit game.
            System.exit(0);
        } else {
            // World is initialized. Tile map is created. Playable tile and coins are placed.
            seed = Long.parseLong(s);
            Random r = new Random(seed);
            initializeWorld();
            initializeVoidWorld();
            drawRandomTileMap(r);
            placeCharacter(world);
            placeManySpecialTiles(world, r);

            // Files in saved are deleted.
            File seedSaved = join(CWD.toString(), "seed.txt");
            File moveArraySaved = join(CWD.toString(), "moveArray.txt");
            seedSaved.delete();
            moveArraySaved.delete();

            // Draw frame to collect coins.
            drawFrame("Collect all " + COINS + " coins in under " + TOTAL_MOVES
                    + " moves to win!");
            Thread.sleep(4000);
            drawFrame("You have 4 seconds to memorize the board.");
            Thread.sleep(4000);
            drawFrame("You can also press 'f' to light up the board 5 times!");
            Thread.sleep(4000);
        }

        // Render world and frame and wait 4 seconds.
        TERenderer ter = new TERenderer();
        ter.initialize(WIDTH, HEIGHT);
        ter.renderFrame(world);
        Thread.sleep(4000);

        // Then proceed with rendering the dark map.
        darkMap(7);
        ter.renderFrame(darkWorld);
        // Main game loop starts. Can move around tile.
        mainGameLoop();
    }

    /**
     * Method used for autograding and testing your code. The input string will be a series
     * of characters (for example, "n123sswwdasdassadwas", "n123sss:q", "lwww". The engine should
     * behave exactly as if the user typed these characters into the engine using
     * interactWithKeyboard.
     *
     * Recall that strings ending in ":q" should cause the game to quite save. For example,
     * if we do interactWithInputString("n123sss:q"), we expect the game to run the first
     * 7 commands (n123sss) and then quit and save. If we then do
     * interactWithInputString("l"), we should be back in the exact same state.
     *
     * In other words, both of these calls:
     *   - interactWithInputString("n123sss:q")
     *   - interactWithInputString("lww")
     *
     * should yield the exact same world state as:
     *   - interactWithInputString("n123sssww")
     *
     * @param input the input string to feed to your program
     * @return the 2D TETile[][] representing the state of the world
     */
    public TETile[][] interactWithInputString(String input) {
        // Fill out this method so that it run the engine using the input
        // passed in as an argument, and return a 2D tile representation of the
        // world that would have been drawn if the same inputs had been given
        // to interactWithKeyboard().
        //
        // See proj3.byow.InputDemo for a demo of how you can make a nice clean interface
        // that works for many different input types.
        int startingIndexForMoves = 1;
        String initialInput = input.toLowerCase().substring(0, 1);
        char[] charArray = input.toCharArray();
        if (initialInput.equals("l")) {
            // Load world.
            loadGame();
            Random r = new Random(seed);
            initializeWorld();
            drawRandomTileMap(r);
            placeCharacter(world);
            placeManySpecialTiles(world, r);

            for (int i = 0; i < moveArray.size(); i += 1) {
                Character c = (Character) moveArray.get(i);
                moveFunction(c, world);
                moveArray.remove(moveArray.size() - 1);
            }
        } else if (initialInput.equals("n")) {
            // Else generate new world.
            int nIndex = input.toLowerCase().indexOf('n');
            int sIndex = input.toLowerCase().indexOf('s');
            seed = Long.parseLong(input.substring(nIndex + 1, sIndex));
            Random r = new Random(seed);
            initializeWorld();
            drawRandomTileMap(r);
            placeCharacter(world);
            placeManySpecialTiles(world, r);
            startingIndexForMoves = sIndex + 1;
            moveArray.clear();

            File seedSaved = join(CWD.toString(), "seed.txt");
            File moveArraySaved = join(CWD.toString(), "moveArray.txt");
            seedSaved.delete();
            moveArraySaved.delete();

        } else {
            // Else loading string is invalid.
            System.out.println("Invalid loading string.");
            System.exit(0);
        }

        // For char array until :q or input ends, move.
        for (int i = startingIndexForMoves; i < input.length(); i += 1) {
            char next = Character.toLowerCase(charArray[i]);
            if (next == ':') {
                if (Character.toLowerCase(charArray[i + 1]) == 'q') {
                    // Save and exit.
                    saveGame();
                    return world;
                }
            }
            moveFunction(next, world);
        }

        /** Rendering engine not included in graded submission!!!!!!
        TERenderer ter = new TERenderer();
        ter.initialize(WIDTH, HEIGHT);
        ter.renderFrame(world); */
        return world;
    }


    // Initializes world by filling in Height X Width with blank tiles.
    private void initializeWorld() {
        world = new TETile[WIDTH][HEIGHT];
        for (int x = 0; x < WIDTH; x += 1) {
            for (int y = 0; y < HEIGHT; y += 1) {
                world[x][y] = Tileset.NOTHING;
            }
        }
    }


    // Initializes void world.
    private void initializeVoidWorld() {
        voidWorld = new TETile[WIDTH][HEIGHT];
        for (int i = 0; i < WIDTH; i += 1) {
            for (int j = 0; j < HEIGHT; j += 1) {
                voidWorld[i][j] = Tileset.NOTHING;
            }
        }
    }


    // Generates room of size N with a width of W and length of L.
    // Bottom left tile is (X, Y). Includes size of walls (+2 in each direction).
    // Returns true if room is drawn, otherwise returns false.
    private boolean drawRoom(int width, int height, int x, int y, TETile[][] worlds) {
        if (width + x >= WIDTH) {
            // If room is out of bounds, return.
            return false;
        } else if (height + y >= HEIGHT) {
            // If room is out of bounds, return.
            return false;
        } else if (height < 0 || width < 0) {
            // If room is out of bounds, return.
            return false;
        } else {
            // Else we will iterate from x --> x + size and y --> y + size.
            for (int i = x; i < width + x; i += 1) {
                for (int j = y; j < height + y; j += 1) {
                    // If on the edge of room, place wall.
                    if (i == x || j == y || i == width + x - 1 || j == height + y - 1)  {
                        if (worlds[i][j] != Tileset.FLOOR) {
                            worlds[i][j] = Tileset.WALL;
                        }
                    } else {
                        worlds[i][j] = Tileset.FLOOR;
                    }
                }
            }
        }
        return true;
    }


    // Draws hallway of length L.
    // Returns true if room is drawn, otherwise returns false.
    private boolean drawHallwayWidthOne(int length, int x, int y, String s, TETile[][] worlds) {
        if (s.equals("v")) {
            return drawRoom(3, length + 2, x, y, worlds);
        } else if (s.equals("h")) {
            return drawRoom(length + 2, 3, x, y, worlds);
        } else {
            System.out.println("Must specify if hallway is v or h.");
            return false;
        }
    }


    // Draws hallway of length L with width 2.
    // Returns true if room is drawn, otherwise returns false.
    private boolean drawHallwayWidthTwo(int length, int x, int y, String s, TETile[][] worlds) {
        if (s.equals("v")) {
            return drawRoom(4, length + 2, x, y, worlds);
        } else if (s.equals("h")) {
            return drawRoom(length + 2, 4, x, y, worlds);
        } else {
            System.out.println("Must specify if hallway is v or h.");
            return false;
        }
    }


    // Draws hallway based on input value
    private boolean drawHallwayVariableWidth(int length, int x, int y, String s, int variable,
                                             TETile[][] worlds) {
        if (variable != 0) {
            return drawHallwayWidthOne(length, x, y, s, worlds);
        } else {
            return drawHallwayWidthTwo(length, x, y, s, worlds);
        }
    }


    // Draws a random tile map.
    public void drawRandomTileMap(Random random) {
        // Variables used for randomization purposes.
        int middleX = 10000;
        int middleY = 10000;
        int[] ints1 = new int[]{0, 7, 14, 21, 28, 35, 42, 49, 56, 63, 68,
                                0, 7, 14, 21, 28, 35, 42, 49, 56, 63, 68, 68};
        int[] ints2 = new int[]{0, 6, 12, 18, 24, 30, 0, 6, 12, 18, 24, 30,
                                0, 6, 12, 18, 24, 30};
        int int2Last = 0;

        // Creates 10-15 rooms and places hallways between the rooms randomly.
        for (int i = 0; i < RandomUtils.uniform(random, 13, 16); i += 1) {
            // Shuffles array and creates rooms.
            RandomUtils.shuffle(random, ints1);
            RandomUtils.shuffle(random, ints2);
            while (ints2[0] == int2Last) {
                RandomUtils.shuffle(random, ints2);
            }
            Room r = new Room(random, ints1[0], ints2[0]);
            int2Last = ints2[0];
            ints1 = Arrays.copyOfRange(ints1, 1, ints1.length);
            ints2 = Arrays.copyOfRange(ints2, 1, ints2.length);
            drawRoom(r.getWidth(), r.getHeight(), r.getX(), r.getY(), world);

            // If more than 1 room exist, generate hallway between rooms.
            if (i != 0) {
                int newMiddleX = r.getX() + r.getWidth() / 2 - 1;
                int newMiddleY = r.getY() + r.getHeight() / 2 - 1;
                int randomOneOrTwo = RandomUtils.uniform(random, 0, 3);
                if (middleY > newMiddleY) {
                    drawHallwayVariableWidth(middleY - newMiddleY, middleX, newMiddleY,
                            "v", randomOneOrTwo, world);
                    if (middleX > newMiddleX) {
                        drawHallwayVariableWidth(middleX - newMiddleX, newMiddleX, newMiddleY,
                                "h", randomOneOrTwo, world);
                    } else {
                        drawHallwayVariableWidth(newMiddleX - middleX, middleX, newMiddleY,
                                "h", randomOneOrTwo, world);
                    }
                } else {
                    drawHallwayVariableWidth(newMiddleY - middleY, newMiddleX, middleY,
                            "v", randomOneOrTwo, world);
                    if (middleX > newMiddleX) {
                        drawHallwayVariableWidth(middleX - newMiddleX, newMiddleX, middleY,
                                "h", randomOneOrTwo, world);
                    } else {
                        drawHallwayVariableWidth(newMiddleX - middleX, middleX, middleY,
                                "h", randomOneOrTwo, world);
                    }
                }
            }
            middleX = r.getX() + r.getWidth() / 2 - 1;
            middleY = r.getY() + r.getHeight() / 2 - 1;
        }
    }


    // Main game function.
    public String projectGame(int width, int height) {
        StdDraw.setCanvasSize(width * 16, height * 16);
        StdDraw.setXscale(0, width);
        StdDraw.setYscale(0, height);
        StdDraw.clear(Color.BLACK);
        StdDraw.enableDoubleBuffering();
        drawTitleScreen();
        while (true) {
            if (StdDraw.hasNextKeyTyped()) {
                char c = StdDraw.nextKeyTyped();
                c = Character.toLowerCase(c);
                if (c == 'n') {
                    // Start new game. -> Create new screen with seed.
                    return solicitNCharsInput();
                } else if (c == 'l') {
                    // Load game.
                    return "l";
                } else if (c == 'q') {
                    // Leave game.
                    return "e";
                }
            }
        }
    }


    // New screen loop.
    private String solicitNCharsInput() {
        drawFrameSeed("");
        StdDraw.show();
        String s = "";
        while (true) {
            if (StdDraw.hasNextKeyTyped()) {
                char c = StdDraw.nextKeyTyped();
                c = Character.toLowerCase(c);
                if (!Character.isDigit(c) || s.length() >= 18) {
                    if (c == 's' && s.length() != 0) {
                        return s;
                    }
                } else {
                    s += c;
                    drawFrameSeed(s);
                }
            }
        }
    }


    // Draws frame with text in middle of screen and a seed.
    private void drawFrameSeed(String s) {
        StdDraw.setFont(new Font("Papyrus", Font.PLAIN, 28));
        StdDraw.clear(Color.BLACK);
        StdDraw.setPenColor(Color.WHITE);
        StdDraw.text(40, 20, s);
        StdDraw.text(40, 10, "Press s after inputting seed.");
        StdDraw.show();
    }


    // Draws frame with text in middle of screen.
    private void drawFrame(String s) {
        StdDraw.setFont(new Font("Papyrus", Font.PLAIN, 28));
        StdDraw.clear(Color.BLACK);
        StdDraw.setPenColor(Color.WHITE);
        StdDraw.text(40, 20, s);
        StdDraw.show();
    }


    // Draws frame with no clear.
    private void drawFrameNoClear(String s, int x, int y, Color color) {
        StdDraw.setFont(new Font("Papyrus", Font.PLAIN, 20));
        StdDraw.setPenColor(color);
        StdDraw.textLeft(x, y, s);
        StdDraw.show();
    }


    // Draws the title screen.
    private void drawTitleScreen() {
        StdDraw.picture(50, 10, join(CWD.toString(), "Tiles/Wallpaper.jpeg").toString());
        StdDraw.setPenColor(Color.WHITE);
        StdDraw.setFont(new Font("Papyrus", Font.BOLD, 56));
        StdDraw.text(16, 30, "Hollow Knight");
        StdDraw.setPenColor(Color.WHITE);
        StdDraw.setFont(new Font("Papyrus", Font.PLAIN, 28));
        StdDraw.text(16, 20, "New Game: (N)");
        StdDraw.text(16, 17, "Load Game: (L)");
        StdDraw.text(16, 14, "Quit: (Q)");
        StdDraw.show();
    }


    // Shows help menu.
    private void showHelp() {
        StdDraw.clear(Color.BLACK);
        drawFrameNoClear("W - Up", 5, HEIGHT - 3, Color.CYAN);
        drawFrameNoClear("A - Left", 5, HEIGHT - 6, Color.CYAN);
        drawFrameNoClear("S - Down", 5, HEIGHT - 9, Color.CYAN);
        drawFrameNoClear("D - Right", 5, HEIGHT - 12, Color.CYAN);
        drawFrameNoClear("H - Help/Leave Help Menu", 5, HEIGHT - 15, Color.CYAN);
        drawFrameNoClear(":Q - Save and Quit", 5, HEIGHT - 18, Color.CYAN);
        StdDraw.show();
    }


    // Shows GUI
    private void showGUI(String tileName) {
        if (coinCount >= COINS - 5) {
            drawFrameNoClear("Coins: " + coinCount, 1, HEIGHT - 2, Color.ORANGE);
        } else {
            drawFrameNoClear("Coins: " + coinCount, 1, HEIGHT - 2, Color.WHITE);
        }

        if (moves >= TOTAL_MOVES - 25) {
            drawFrameNoClear("Moves: " + moves, 1, HEIGHT - 4, Color.ORANGE);
        } else {
            drawFrameNoClear("Moves: " + moves, 1, HEIGHT - 4, Color.WHITE);
        }

        if (lights >= 3 && lights != 5) {
            drawFrameNoClear("Lights: " + lights, 1, HEIGHT - 6, Color.ORANGE);
        } else if (lights == 5) {
            drawFrameNoClear("Lights: " + lights, 1, HEIGHT - 6, Color.RED);
        } else {
            drawFrameNoClear("Lights: " + lights, 1, HEIGHT - 6, Color.WHITE);
        }

        drawFrameNoClear("Tile: " + tileName, 1, HEIGHT - 8, Color.WHITE);
        drawFrameNoClear("Help - H", 1, HEIGHT - 10, Color.WHITE);
    }


    // Main game loop.
    private void mainGameLoop() throws InterruptedException {
        TERenderer t = new TERenderer();
        String oldTileName = "n";
        boolean lightsOn = false;
        while (true) {
            // Method for rendering tile mouse is hovering over.
            String tileName = getTileName();
            if (!tileName.equals(oldTileName)) {
                darkMap(7);
                t.renderFrame(darkWorld);
                lightsOn = false;
                showGUI(tileName);
                oldTileName = tileName;
            }
            // Take in keys typed.
            if (StdDraw.hasNextKeyTyped()) {
                char c = StdDraw.nextKeyTyped();
                c = Character.toLowerCase(c);
                if (c == ':') {
                    // If user types :Q, save and exit.
                    while (true) {
                        if (StdDraw.hasNextKeyTyped()) {
                            char x = StdDraw.nextKeyTyped();
                            x = Character.toLowerCase(x);
                            if (x == 'q') {
                                // Save and exit the program.
                                saveGame();
                                System.exit(0);
                            } else {
                                break;
                            }
                        }
                    }
                } else if (c == 'h') {
                    // If character is h, show help menu.
                    showHelp();
                    while (true) {
                        if (StdDraw.hasNextKeyTyped()) {
                            char x = StdDraw.nextKeyTyped();
                            x = Character.toLowerCase(x);
                            if (x == 'h') {
                                darkMap(7);
                                t.renderFrame(darkWorld);
                                lightsOn = false;
                                showGUI(tileName);
                                break;
                            }
                        }
                    }
                } else if (c == 'f' && lights < 5 && !lightsOn) {
                    // If user types in f and hasn't shined lights more than 5 times,
                    // and lights aren't already on, render entire world map.
                    t.renderFrame(world);
                    lights += 1;
                    lightsOn = true;
                    moveArray.add('f');
                } else {
                    // Else we move.
                    moveFunction(c, world);
                    darkMap(7);
                    t.renderFrame(darkWorld);
                    lightsOn = false;
                    showGUI(tileName);
                    // If coinCount is equal to total coins, player wins.
                    if (coinCount == COINS) {
                        drawFrame("You win!");
                        Thread.sleep(4000);
                        System.exit(0);
                    }
                    // If moves is equal to total moves, player loses.
                    if (moves == TOTAL_MOVES) {
                        drawFrame("You lose!");
                        Thread.sleep(4000);
                        System.exit(0);
                    }
                }
            }
        }
    }


    // Puts moving tile in top right corner.
    private void placeCharacter(TETile[][] worlds) {
        for (int i = WIDTH - 1; i != 1; i -= 1) {
            for (int j = HEIGHT - 1; j != 1; j -= 1) {
                // If tile is floor, place moving tile down.
                if (worlds[i][j].equals(Tileset.FLOOR)) {
                    worlds[i][j] = Tileset.AVATAR;
                    avatarX = i;
                    avatarY = j;
                    return;
                }
            }
        }
    }


    // Places coin tile.
    private void placeSpecialTile(TETile[][] worlds, Random r) {
        while (true) {
            int x = RandomUtils.uniform(r, 0, WIDTH);
            int y = RandomUtils.uniform(r, 0, HEIGHT);
            if (worlds[x][y] == Tileset.FLOOR) {
                worlds[x][y] = Tileset.FLOWER;
                return;
            }
        }
    }


    // Places many coin tiles.
    private void placeManySpecialTiles(TETile[][] worlds, Random r) {
        for (int i = 0; i < COINS; i += 1) {
            placeSpecialTile(worlds, r);
        }
    }


    // Makes entire map dark except for x tiles around player.
    private void darkMap(int range) {
        initializeVoidWorld();
        darkWorld = voidWorld;

        // If tile is within (range) tiles of avatar, render it.
        // Otherwise tile is NOTHING on darkWorld.
        for (int i = avatarX - range; i < avatarX + range; i += 1) {
            for (int j = avatarY - range; j < avatarY + range; j += 1) {
                if (j < 0 || i < 0 || j >= HEIGHT || i >= WIDTH) {
                    continue;
                } else {
                    darkWorld[i][j] = world[i][j];
                }
            }
        }
    }


    // Joins two file paths together.
    static File join(String first, String... others) {
        return Paths.get(first, others).toFile();
    }


    // Writes object to file.
    private void writeObjectToFile(File save, Object o) {
        try {
            ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(save));
            oos.writeObject(o);
        } catch (IOException e) {
            throw new IllegalArgumentException();
        }
    }


    // Reads object.
    private Object readObject(File save) {
        try {
            ObjectInputStream oos = new ObjectInputStream(new FileInputStream(save));
            return oos.readObject();
        } catch (ClassNotFoundException | IOException e) {
            throw new IllegalArgumentException();
        }
    }

    // Helper function to save the game.
    private void saveGame() {
        // Get all files required for saving.
        File seedSaved = join(CWD.toString(), "seed.txt");
        File moveArraySaved = join(CWD.toString(), "moveArray.txt");

        // Create files and directories for saving.
        try {
            seedSaved.createNewFile();
            moveArraySaved.createNewFile();
        } catch (IOException e) {
            throw new IllegalArgumentException();
        }

        // Saves world, coinCount, moves, lights, avatarX, and avatarY.
        writeObjectToFile(seedSaved, seed);
        writeObjectToFile(moveArraySaved, moveArray);
    }


    // Helper function to load the game.
    private void loadGame() {
        File seedSaved = join(CWD.toString(), "seed.txt");
        File moveArraySaved = join(CWD.toString(), "moveArray.txt");
        if (!seedSaved.exists() || !moveArraySaved.exists()) {
            // If files don't exist, return.
            System.out.println("No save exists!");
            System.exit(0);
        } else {
            // Else get files from saved folder.
            seed = (long) readObject(seedSaved);
            moveArray = (ArrayList) readObject(moveArraySaved);
        }
    }


    // Helper function to return tile name.
    private String getTileName() {
        // Automatically gets mouseX and mouseY. Then finds tile.
        int mouseX = (int) StdDraw.mouseX();
        int mouseY = (int) StdDraw.mouseY();
        if (mouseX == WIDTH) {
            mouseX -= 1;
        }
        if (mouseY == HEIGHT) {
            mouseY -= 1;
        }
        TETile tile = voidWorld[mouseX][mouseY];

        // Returns name of tile.
        if (tile.equals(Tileset.NOTHING)) {
            return "?";
        } else if (tile.equals(Tileset.WALL)) {
            return "Space Wall";
        } else if (tile.equals(Tileset.AVATAR)) {
            return "Hollow Knight";
        } else if (tile.equals(Tileset.FLOOR)) {
            return "Space Grass";
        } else if (tile.equals(Tileset.FLOWER)) {
            return "Coin";
        } else {
            return "?";
        }
    }


    // Helper function for moving.
    private void moveFunction(char c, TETile[][] worlds) {
        switch (c) {
            // Move avatar. Previous position becomes floor.
            // avatarX or avatarY and moves += 1, moveArray adds wasd.
            case 'w': // Move up
                if (worlds[avatarX][avatarY + 1] == Tileset.WALL) {
                    return;
                } else {
                    if (worlds[avatarX][avatarY + 1] == Tileset.FLOWER) {
                        coinCount += 1;
                    }
                    worlds[avatarX][avatarY] = Tileset.FLOOR;
                    worlds[avatarX][avatarY + 1] = Tileset.AVATAR;
                    avatarY += 1;
                    moves += 1;
                    moveArray.add('w');
                }
                break;
            case 'a': // Move left
                if (worlds[avatarX - 1][avatarY] == Tileset.WALL) {
                    return;
                } else {
                    if (worlds[avatarX - 1][avatarY] == Tileset.FLOWER) {
                        coinCount += 1;
                    }
                    worlds[avatarX][avatarY] = Tileset.FLOOR;
                    worlds[avatarX - 1][avatarY] = Tileset.AVATAR;
                    avatarX -= 1;
                    moves += 1;
                    moveArray.add('a');
                }
                break;
            case 's': // Move down
                if (worlds[avatarX][avatarY - 1] == Tileset.WALL) {
                    return;
                } else {
                    if (worlds[avatarX][avatarY - 1] == Tileset.FLOWER) {
                        coinCount += 1;
                    }
                    worlds[avatarX][avatarY] = Tileset.FLOOR;
                    worlds[avatarX][avatarY - 1] = Tileset.AVATAR;
                    avatarY -= 1;
                    moves += 1;
                    moveArray.add('s');
                }
                break;
            case 'd': // Move right
                if (worlds[avatarX + 1][avatarY] == Tileset.WALL) {
                    return;
                } else {
                    if (worlds[avatarX + 1][avatarY] == Tileset.FLOWER) {
                        coinCount += 1;
                    }
                    worlds[avatarX][avatarY] = Tileset.FLOOR;
                    worlds[avatarX + 1][avatarY] = Tileset.AVATAR;
                    avatarX += 1;
                    moves += 1;
                    moveArray.add('d');
                }
                break;
            default:
                return;
        }
    }
}
