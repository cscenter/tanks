package model;

public class MovableObject extends GameObject {

    private final int team;
    
    protected Vector2D orientation;
    protected Vector2D speed;
    
    protected int health = 1;
    
    public MovableObject(int id, Vector2D p, GameObjectDescription d, int t) {
        super(id, p, d);
        speed = new Vector2D(0, 0);
        team = t;
        orientation = Direction.DOWN.getMove();
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
    
    public void setOrientation(Vector2D p) {
        orientation = p.normalize();
    }
    
    public Vector2D getOrientation() {
        return orientation;
    }
    
    @Override
    public boolean attacked(Projectile p) {
        if (p.getTeam() != getTeam()) {
            --health;
        }
        return (health == 0);
    }
    
    @Override
    public int getHealth() {
        return health;
    }
}
