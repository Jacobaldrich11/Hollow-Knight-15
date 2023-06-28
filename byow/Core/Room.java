package byow.Core;

import java.io.Serializable;
import java.util.Random;

// Represents a room with width, height, x, and y coordinates.
// Uses random object to determine size.
class Room implements Serializable {

    private final int width;
    private final int height;
    private int xCoordinate;
    private int yCoordinate;

    // Creates a room with random width and height, and a set x and y coordinate.
    Room(Random r, int x, int y) {
        width = RandomUtils.uniform(r, 6, 12);
        height = RandomUtils.uniform(r, 6, 10);
        xCoordinate = x;
        yCoordinate = y;
    }
    public int getWidth() {
        return width;
    }
    public int getHeight() {
        return height;
    }
    public int getX() {
        return xCoordinate;
    }
    public int getY() {
        return yCoordinate;
    }
}
