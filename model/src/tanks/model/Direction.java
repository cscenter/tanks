package tanks.model;

import java.util.*;

public enum Direction {
    UP(-1, 0), DOWN(1, 0), LEFT(0, -1), RIGHT(0, 1);
    
    private Vector2D pos;
    
    private Direction(int x, int y) {
        this.pos = new Vector2D(x, y);
    }
    
    public Vector2D getMove() {
        return pos;
    }
}
