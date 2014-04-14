package model;

public class Tree extends ImmovableObject {
    
    public static final int SIZE = GameModel.DISCRETE_FACTOR;
    public static final int START_HEALTH = 1;
    
    private int health;
    
    public Tree(int id, Vector2D p) {
        super(id, p, SIZE, SIZE, GameObjectDescription.TREE);    
        health = START_HEALTH;
    }
    
    @Override
    public boolean attacked(Projectile p) {
        return (--health == 0);
    }
    
    @Override
    public int getHealth() {
        return health;
    }
}
