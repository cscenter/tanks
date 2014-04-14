package model;

public class GameObject implements Attackable {
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
    
    @Override
    public boolean attacked(Projectile p) {
        return false;
    }
    
    @Override
    public int getHealth() {
        return -1;
    }

}
