package model;

public class Tank extends MovableObject {

    public static final int SIZE = GameModel.DISCRETE_FACTOR;
    public static final int START_HEALTH = 3;

    public Tank(int id, Vector2D p, int t) {
        super(id, p, GameObjectDescription.TANK, t);
        health = START_HEALTH;
    }
    
    public Projectile shoot(int freeID) {       
        Vector2D pos = new Vector2D(position.getX(), position.getY());
        pos.setX(pos.getX() + SIZE / 2 + (SIZE / 2 + 1) * orientation.getX());
        pos.setY(pos.getY() + SIZE / 2 + (SIZE / 2 + 1) * orientation.getY());
        Projectile p = new Projectile(freeID, pos, getTeam(), orientation.mul(2));
        p.setOrientation(orientation);
        return p;
    }    
    
    public void setPosition(Vector2D p) {
        super.setPosition(p);
        setSpeed(new Vector2D(0, 0));
    }
    
    @Override
    public int getWidth() {
        return SIZE;
    }

    @Override
    public int getHeight() {
        return SIZE;
    }
}
