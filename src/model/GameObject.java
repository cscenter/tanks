package model;

public class GameObject implements Attackable, Sizable {
    protected Vector2D position;
    private final int id;
    
    private final GameObjectDescription description;

    protected static final int SIZE = GameModel.DISCRETE_FACTOR;
    
    public GameObject(int id, Vector2D p, GameObjectDescription desc) {
        this.id = id;
        position = p;
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
    
    @Override
    public boolean attacked(Projectile p) {
        return false;
    }
    
    @Override
    public int getHealth() {
        return -1;
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
