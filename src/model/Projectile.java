package model;

public class Projectile extends MovableObject {
    
    public static final int SIZE = 1;

    private static final int DEFAULT_DAMAGE = 1;
    
    private int damage;

	public Projectile(int id, Vector2D p, Team team, Direction d) {
        super(id, p, GameObjectDescription.PROJECTILE, team);
        speed = new Speed(d, 0);
        setOrientation(d);
        this.damage = DEFAULT_DAMAGE;
    }

	public Projectile(int id, Vector2D p, Team team, Direction d, int damage) {
        super(id, p, GameObjectDescription.PROJECTILE, team);
        speed = new Speed(d, 2);
        setOrientation(d);
        this.damage = damage;
    }
	
    @Override
    public int getWidth() {
        return SIZE;
    }

    @Override
    public int getHeight() {
        return SIZE;
    }
    
    public int getDamage() {
		return damage;
	}

	public void setDamage(int damage) {
		this.damage = damage;
	}
}
