package model;

public class GameObject {
    protected Vector2D position;
    private final int width;
    private final int height;
    private final int id;
    
    private final GameObjectDescription description;
    
    public GameObject(int id, Vector2D p, int w, int h, GameObjectDescription desc) {
        this.id = id;
        position = p;
        width = w;
        height = h;
        description = desc;
    }
    
    public Vector2D getPosition() {
        return position;
    }
    
    public int getID() {
        return id;
    }
    
    public GameObjectDescription getDescription() {
        return description;
    }
    
    public int getWidth() {
        return width;
    }
    
    public int getHeight() {
        return height;
    }
    
    public boolean attacked(Projectile p) {
        return false;
    }
}
