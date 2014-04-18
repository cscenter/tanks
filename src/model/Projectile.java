package model;

public class Projectile extends MovableObject {
    
    public static final int SIZE = 1;

    public Projectile(int id, Vector2D p, Team team, Direction d) {
        super(id, p, GameObjectDescription.PROJECTILE, team);
        speed = new Speed(d, 0);
        setOrientation(d);
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
