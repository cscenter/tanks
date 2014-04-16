package model;

public class Tank extends MovableObject {

    public static final int SIZE = GameModel.DISCRETE_FACTOR;
    public static final int START_HEALTH = 3;
    
    private TurnDelay fireDelay;
    
    public Tank(int id, Vector2D p, int t, int delay) {
        super(id, p, GameObjectDescription.TANK, t);
        health = START_HEALTH;
        speed.setDelay(delay);
        fireDelay = new TurnDelay(delay, 1);
    }
    
    public boolean canShoot() {
        return fireDelay.makeTurn();
    }
    
    public Projectile shoot(int freeID) {
        Vector2D pos = new Vector2D(position.getX(), position.getY());
        Vector2D or = orientation.getMove();
        pos.setX(pos.getX() + SIZE / 2 + (SIZE / 2 + 1) * or.getX());
        pos.setY(pos.getY() + SIZE / 2 + (SIZE / 2 + 1) * or.getY());
        Projectile p = new Projectile(freeID, pos, getTeam(), orientation);
        return p;
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
