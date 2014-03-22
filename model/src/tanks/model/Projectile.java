package tanks.model;

public class Projectile extends MovableObject {
    
    public static final int size = 1;
    private boolean justCreated;
    
    public Projectile(int id, Vector2D p, Vector2D s, char c) {
        super(id, p, size, size, c, s);
        justCreated = true;
    }
    
    public void setCreateStatus(boolean p) {
        justCreated = p;
    }
    
    public boolean isJustCreated() {
        return justCreated;
    }
}
