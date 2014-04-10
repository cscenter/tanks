package model;

public class Tank extends MovableObject {

    public static final int SIZE = GameModel.DISCRETE_FACTOR;
    public static final int START_HEALTH = 3;
    
    private int health;

    public Tank(int id, Vector2D p, int t, Vector2D s) {
        super(id, p, SIZE, SIZE, GameObjectDescription.TANK, t, s);
        health = START_HEALTH;
    }
    
    public Projectile shoot(int freeID) {       
        Vector2D pos = new Vector2D(position.getX(), position.getY());
        pos.setX(pos.getX() + getHeight() / 2 + 2 * orientation.getX());
        pos.setY(pos.getY() + getWidth() / 2 + 2 * orientation.getY());
        Projectile p = new Projectile(freeID, pos, orientation.mul(2), getTeam());
        p.setOrientation(orientation);
        return p;
    }    
    
    public void setPosition(Vector2D p) {
        super.setPosition(p);
        setSpeed(new Vector2D(0, 0));
    }
    
    public boolean attacked(Projectile p) {
        if (p.getTeam() != getTeam()) {
            --health;
        }
        return (health == 0);
    }
    
    public int getHealth() {
        return health;
    }
    
}
