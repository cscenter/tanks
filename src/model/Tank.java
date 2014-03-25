package model;

public class Tank extends MovableObject {

    public static final int START_HEALTH = 3;
    
    private int health;
    
    private Vector2D gunOrientation;

    public Tank(int id, Vector2D p, int w, int h, char c, Vector2D s) {
        super(id, p, w, h, c, s);
        gunOrientation = Direction.DOWN.getMove();
        health = START_HEALTH;
    }
    
    public Projectile shoot(int freeID) {
        
        /// hardcoded numbers detected !!!
        /// this code must depend on GameModel.discreteFactor
        
        Vector2D pos = new Vector2D(position.getX(), position.getY());
        pos.setX(pos.getX() + getHeight() / 2 + 2 * gunOrientation.getX());
        pos.setY(pos.getY() + getWidth() / 2 + 2 * gunOrientation.getY());
        return new Projectile(freeID, pos, gunOrientation.mul(2), '*');
    }    
    
    public void setGunOrientation(Vector2D p) {
        gunOrientation = p.normalize();
    }
    
    public void setPosition(Vector2D p) {
        super.setPosition(p);
        setSpeed(new Vector2D(0, 0));
    }
    
    public boolean attacked(Projectile p) {
        --health;
        return (health == 0);
    }
    
    public int getHealth() {
        return health;
    }
    
}
