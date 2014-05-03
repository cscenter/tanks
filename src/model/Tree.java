package model;

public class Tree extends ImmovableObject {
    
    public static final int START_HEALTH = 2;
    
    private int health;
    
    public Tree(int id, Vector2D p, GameObjectDescription description) throws ModelException {
    	super(id, p, description);
    	if (description != GameObjectDescription.PALM && description != GameObjectDescription.TREE) {
        	throw new ModelException("Invalid tree construction. Check Object description.");
        }
        health = START_HEALTH;
    }

    @Override
    public boolean attacked(Projectile p) {
        return (health -= p.getDamage()) <= 0;
    }
    
    @Override
    public int getHealth() {
        return health;
    }
}
