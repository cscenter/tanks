package model;

import java.lang.UnsupportedOperationException;

public class Tree extends ImmovableObject {
    
    public static final int SIZE = GameModel.DISCRETE_FACTOR;
    public static final int START_HEALTH = 1;
    
    private int health;
    
    public Tree(int id, Vector2D p) {
        super(id, p, SIZE, SIZE, GameObjectDescription.TREE);    
        health = START_HEALTH;
    }
    
    public boolean attacked(Projectile p) {
        return (--health == 0);
    }
}
