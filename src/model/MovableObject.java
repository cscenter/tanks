package model;

public class MovableObject extends GameObject {

    private final int team;

    protected Vector2D speed;
    
    public MovableObject(int id, Vector2D p, int w, int h, char c, int t, Vector2D s) {
        super(id, p, w, h, c);
        speed = s;
        team = t;
    }
    
    public void setPosition(Vector2D p) {
        position = p;
    }
    
    public Vector2D getSpeed() {
        return speed;
    }
    
    public void setSpeed(Vector2D s) {
        speed = s;
    }
    
    public int getTeam() {
        return team;
    }
}
