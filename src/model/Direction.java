package model;

public enum Direction {
    UP(-1, 0), DOWN(1, 0), LEFT(0, -1), RIGHT(0, 1), NONE(0, 0);
    
    private Vector2D pos;
    
    private Direction(int x, int y) {
        this.pos = new Vector2D(x, y);
    }
    
    public Vector2D getMove() {
        return pos;
    }
	
    public Direction getOpposite() {
    	switch (this) {
    	case UP:
    		return DOWN;
    	case DOWN:
    		return UP;
    	case LEFT:
    		return RIGHT;
    	case RIGHT:
    		return LEFT;
		default:
			return NONE;
    	}
    }
    
	public static Direction fromVector2D(Vector2D v) {
		for (Direction d : Direction.values()) {
			if (v.equals(d.getMove())) {
				return d;
			}
		}
		// unhandled situation!
		return UP;
	}
}
