package tanks.model;

public class GameObject {
    protected Vector2D position;
    private final int width;
    private final int height;
    private final int id;
    
    private final char description; // to be deleted
    
    public GameObject(int id, Vector2D p, int w, int h, char c) {
        this.id = id;
        position = p;
        width = w;
        height = h;
        description = c;
    }
    
    public Vector2D getPosition() {
        return position;
    }
    
    public int getID() {
        return id;
    }
    
    public char getLetter() {
        return description;
    }
    
    public int getWidth() {
        return width;
    }
    
    public int getHeight() {
        return height;
    }
    
    public void attacked() {}
}
