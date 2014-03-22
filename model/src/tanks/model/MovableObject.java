package tanks.model;

public class MovableObject extends GameObject {

    protected Vector2D speed;
    
    public MovableObject(int id, Vector2D p, int w, int h, char c, Vector2D s) {
        super(id, p, w, h, c);
        speed = s;
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
}
