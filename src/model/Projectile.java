package model;

public class Projectile extends MovableObject {
    
    public static final int SIZE = 1;
    private boolean justCreated;

    public Projectile(int id, Vector2D p, int t, Vector2D s) {
        super(id, p, GameObjectDescription.PROJECTILE, t);
        justCreated = true;
        speed = s;
    }
    
    public void setCreateStatus(boolean p) {
        justCreated = p;
    }
    
    public boolean isJustCreated() {
        return justCreated;
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
