package model;

public class Projectile extends MovableObject {
    
    public static final int SIZE = 1;
    private boolean justCreated;
    
    public Projectile(int id, Vector2D p, Vector2D s, GameObjectDescription d, int t) {
        super(id, p, SIZE, SIZE, d, t, s);
        justCreated = true;
    }
    
    public void setCreateStatus(boolean p) {
        justCreated = p;
    }
    
    public boolean isJustCreated() {
        return justCreated;
    }
}
